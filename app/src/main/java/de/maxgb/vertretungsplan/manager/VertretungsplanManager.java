package de.maxgb.vertretungsplan.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.AnzeigeActivity;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.LehrerVertretung;
import de.maxgb.vertretungsplan.util.SchuelerVertretung;
import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Singleton Klasse zur Auswertung des Vertretungsplans und zur Speicherung der
 * Vertretungen
 * 
 * @author Max Becker
 * 
 */
public class VertretungsplanManager {
	private static VertretungsplanManager instance;

	public static VertretungsplanManager getCreatedInstance() {
		return instance;
	}

	public static synchronized VertretungsplanManager getInstance(Context context,
																  boolean schueler, boolean lehrer) {

		if (instance == null) {
			instance = new VertretungsplanManager(context, schueler, lehrer);
		} else if (instance.schueler != schueler || instance.lehrer != lehrer) {
			instance.schueler = schueler;
			instance.lehrer = lehrer;
			instance.auswerten();
		}
		return instance;
	}

	private final String TAG = "VertretungsplanManager";
	private final Context context;
	private ArrayList<OnUpdateListener> update_listener;
	private ArrayList<OnUpdateFinishedListener> update_finished_listener;
	private String schuelerStand = "";
	private String lehrerStand = "";
	private ArrayList<SchuelerVertretung> schuelerVertretungen;
	private ArrayList<LehrerVertretung> lehrerVertretungen;
	private long schuelerDateiLastModified;
	private long lehrerDateiLastModified;
	private boolean schueler;
	private boolean lehrer;

	private VertretungsplanManager(Context context, boolean schueler, boolean lehrer) {
		this.schueler = schueler;
		this.lehrer = lehrer;
		this.context = context;
		update_listener = new ArrayList<OnUpdateListener>();
		update_finished_listener = new ArrayList<OnUpdateFinishedListener>();
		auswerten();
	}

	// ------------------------
	public void asyncAuswerten() {
		AuswertenTask task = new AuswertenTask();
		task.execute();
	}

	public boolean auswerten() {
		boolean modified = false;
		try {
			if (schueler) {

				File f1 = new File(context.getFilesDir()
						, Constants.SCHUELER_PLAN_FILE_NAME);
					if (schuelerDateiLastModified != f1.lastModified()
							|| schuelerVertretungen == null) {
						schuelerDateiLastModified = f1.lastModified();
						modified = auswertenSchueler(f1);

					}

			}


			if (lehrer) {
				File f2 = new File(context.getFilesDir()
						, Constants.LEHRER_PLAN_FILE_NAME);
				if (lehrerDateiLastModified != f2.lastModified()
						|| lehrerVertretungen == null) {
					lehrerDateiLastModified = f2.lastModified();
					modified = auswertenLehrer(f2);

				}

			}
		} catch (Exception e) {
			Logger.e(TAG, "Auswerten fehlgeschlagen",e);
			modified=false;
			AnzeigeActivity.fehler("Bitte sende einen Fehlerreport in den Einstellungen");
		}
		return modified;
	}

	public String getLehrerStand() {
		return lehrerStand;
	}

	public ArrayList<LehrerVertretung> getLehrerVertretungen() {
		if (lehrerVertretungen == null) {
			return (new ArrayList<LehrerVertretung>());
		}
		return lehrerVertretungen;
	}

	public String getSchuelerStand() {
		return schuelerStand;
	}

	public ArrayList<SchuelerVertretung> getSchuelerVertretungen() {
		if (schuelerVertretungen == null) {
			return (new ArrayList<SchuelerVertretung>());
		}
		return schuelerVertretungen;
	}

	public void registerOnUpdateFinishedListener(
			OnUpdateFinishedListener listener) {
		if (!this.update_finished_listener.contains(listener)) {
			this.update_finished_listener.add(listener);
		}

	}

	public void registerOnUpdateListener(OnUpdateListener listener) {
		if (!this.update_listener.contains(listener)) {
			this.update_listener.add(listener);
		}
	}

	public void unregisterOnUpdateFinishedListener(
			OnUpdateFinishedListener listener) {
		this.update_finished_listener.remove(listener);
	}

	public void unregisterOnUpdateListener(OnUpdateListener listener) {
		this.update_listener.remove(listener);
	}

	private boolean auswertenLehrer(File f) {
		Document doc = getDoc(f);
		if (doc == null) {
			Logger.e(TAG, "Fehler beim erzeugen des Dokuments");
			return false;
		}
		String neuerStand = standHerausfinden(doc);
		if (lehrerStand.equals(neuerStand)) {
			return false;
		} else {
			lehrerStand = neuerStand;
		}

		ArrayList<LehrerVertretung> vertretungen = new ArrayList<LehrerVertretung>();

		//NEUE VERSION

		// Mon_* Elemente
		NodeList table = doc.getElementsByTagName("table");
		Logger.i(TAG, table.getLength() + " Tableelemente");


		ArrayList<Node> mon_lists = getElementsByClassName(table, "mon_list");
		Logger.i(TAG, mon_lists.size() + " Mon List Elemente");

		NodeList divs = doc.getElementsByTagName("div");

		ArrayList<Node> mon_titles = getElementsByClassName(divs, "mon_title");
		Logger.i(TAG, mon_titles.size() + " Mon Title Elemente");

		if (mon_titles.size() != mon_lists.size()) {
			Logger.w(TAG, "Mon element count doesnt match");
		}
		int daycount = mon_lists.size();


		for (int j = 0; j < daycount; j++) {
			String tag = mon_titles.get(j).getChildNodes().item(0)
					.getNodeValue();
			Logger.i(TAG, "Processing Tag: " + tag);
			NodeList tr = mon_lists.get(j).getChildNodes();
			Logger.i(TAG, "Found " + tr.getLength() + " TR-Elements");


			for (int i = 2; i < tr.getLength(); i++)// Durchlaufen aller
													// tr-Elemente
			{
				Node node = tr.item(i);
				// System.out.println(node.getNodeValue()+"---"+node.getNodeName());
				// ÃœberprÃ¼fen von welchem Typ die Node ist und ob sie
				// Attribute hat und dann ÃœberprÃ¼fen der class, ob es
				// sich
				// im eine Vertretung handelt
				if (node.getNodeName() != "#text") {
					NamedNodeMap attr = node.getAttributes();
					// System.out.println(attr.getLength());
					if (attr.getLength() > 0) {
						Node attrclass = attr.getNamedItem("class");
						if (attrclass != null) {
							String value = attrclass.getNodeValue();
							// System.out.println(value);
							if (value.indexOf("list odd") != -1
									|| value.indexOf("list even") != -1) {
								// Vertretung gefunden
								NodeList childnodes = node.getChildNodes();

								String neu = "";
								try {
									Node nNeu =childnodes.item(0).getChildNodes().item(0);
									neu = nNeu.getNodeValue();
									neu=neu.replace("+nbsp;","");
								} catch (NullPointerException e1) {
									Logger.i(TAG, "Neu Feld Leer");
								}

								String vertreter = "";
								try {
									Node nVertreter = childnodes.item(1)
											.getChildNodes().item(0);
									if (nVertreter.getNodeType() == Node.TEXT_NODE) {
										vertreter = nVertreter.getNodeValue();
									} else if (nVertreter.getNodeType() == Node.ELEMENT_NODE) {
										vertreter = nVertreter.getChildNodes()
												.item(1).getNodeValue();
									}
									vertreter=vertreter.replace("+nbsp;", "");
								} catch (NullPointerException e1) {
									Logger.i(TAG, "Vertreter Feld Leer");
								}

								String art=null;
								try {
									art = childnodes.item(2).getChildNodes()
											.item(0).getNodeValue();
									art=art.replace("+nbsp;", "");
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Art Feld");
								}
								if(art==null){
									art="";
								}


								String stunde = "";
								try {
									stunde = childnodes.item(3).getChildNodes()
											.item(0).getNodeValue();
									stunde=stunde.replace("+nbsp;", "");
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Stunden Feld");
								}


								String klasse = "";
								try {
									klasse = childnodes.item(4).getChildNodes()
											.item(0).getNodeValue();
									klasse=klasse.replace("+nbsp;", "");
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Klassen Feld");
								}


								String zuVertretender = "";
								try {
									//Logger.i(TAG,nodeToString(childnodes.item(4),0));
									zuVertretender = childnodes.item(5).getTextContent();
									zuVertretender=zuVertretender.replace("+nbsp;", "---");
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres zuVertretender Feld");
								}


								Node fach = childnodes.item(6).getChildNodes()
										.item(0);
								String sfach = null;
								try {
									if (fach.getNodeType() == Node.TEXT_NODE) {
										sfach = fach.getNodeValue();
									} else if (fach.getNodeType() == Node.ELEMENT_NODE) {
										sfach = fach.getChildNodes().item(0)
												.getNodeValue();
									}
									sfach=sfach.replace("+nbsp;", "");
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Fach Feld");
									sfach = "";
								}
								if (sfach == null) {
									sfach = "---";
								}


								String raum = "";
								try {
									raum = childnodes.item(7).getChildNodes()
											.item(0).getNodeValue();
									raum=raum.replace("+nbsp;", "");
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Raum Feld");
								}


								String bemerkung = "";
								try {
									bemerkung = childnodes.item(8)
											.getChildNodes().item(0)
											.getNodeValue();
									bemerkung=bemerkung.replace("+nbsp;", "");
								} catch (NullPointerException e) {
								}


								String klausur = "";
								try {
									klausur = childnodes.item(9)
											.getChildNodes().item(0)
											.getNodeValue();
									klausur=klausur.replace("+nbsp;", "");
								} catch (NullPointerException e) {
								}



								vertretungen.add(new LehrerVertretung(neu, klasse,
										stunde, art, sfach, raum, tag, klausur,
										bemerkung, vertreter, zuVertretender));

							}
						}
					}

				}
			}
		}
		lehrerVertretungen = vertretungen;
		return true;

	}

	private boolean auswertenSchueler(File f) {
		Document doc = getDoc(f);
		if (doc == null) {
			Logger.e(TAG, "Fehler beim erzeugen des Dokuments");
			return false;
		}

		String neuerStand = standHerausfinden(doc);
		if (schuelerStand.equals(neuerStand)) {
			return false;
		} else {
			schuelerStand = neuerStand;
		}

		ArrayList<SchuelerVertretung> vertretungen = new ArrayList<SchuelerVertretung>();

		// Neue Version:

		// Mon_* Elemente
		NodeList table = doc.getElementsByTagName("table");
		Logger.i(TAG, table.getLength() + " Tableelemente");


		ArrayList<Node> mon_lists = getElementsByClassName(table, "mon_list");
		Logger.i(TAG, mon_lists.size() + " Mon List Elemente");

		NodeList divs = doc.getElementsByTagName("div");

		ArrayList<Node> mon_titles = getElementsByClassName(divs, "mon_title");
		Logger.i(TAG, mon_titles.size() + " Mon Title Elemente");

		if (mon_titles.size() != mon_lists.size()) {
			Logger.w(TAG, "Mon element count doesnt match");
		}
		int daycount = mon_lists.size();

		for (int j = 0; j < daycount; j++) {
			String tag = mon_titles.get(j).getChildNodes().item(0)
					.getNodeValue();
			Logger.i(TAG, "Processing Tag: " + tag);
			NodeList tr = mon_lists.get(j).getChildNodes();
			Logger.i(TAG, "Found " + tr.getLength() + " TR-Elements");

			// Vertretungen auslesen
			for (int i = 2; i < tr.getLength(); i++)// Durchlaufen aller
			// tr-Elemente
			{
				Node node = tr.item(i);
				// System.out.println(node.getNodeValue()+"---"+node.getNodeName());
				// ÃœberprÃ¼fen von welchem Typ die Node ist und ob sie
				// Attribute hat und dann ÃœberprÃ¼fen der class, ob es
				// sich
				// im eine Vertretung handelt
				if (node.getNodeName() != "#text") {
					NamedNodeMap attr = node.getAttributes();
					// System.out.println(attr.getLength());
					if (attr.getLength() > 0) {
						Node attrclass = attr.getNamedItem("class");
						if (attrclass != null) {
							String value = attrclass.getNodeValue();
							// System.out.println(value);
							if (value.indexOf("list odd") != -1
									|| value.indexOf("list even") != -1) {
								// Vertretung gefunden
								NodeList childnodes = node.getChildNodes();

								String klasse;
								try {
									klasse = childnodes.item(0).getChildNodes()
											.item(0).getChildNodes().item(0)
											.getNodeValue();
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Klassen Feld");
									klasse = "";
								}

								String stunde;
								try {
									stunde = childnodes.item(1).getChildNodes()
											.item(0).getNodeValue();
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Stunden Feld");
									stunde = "";
								}

								String art;
								try {
									art = childnodes.item(2).getChildNodes()
											.item(0).getNodeValue();
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Art Feld");
									art = "";
								}


								String sfach = null;
								try {
									Node fach = childnodes.item(3).getChildNodes()
											.item(0);
									if (fach.getNodeType() == Node.TEXT_NODE) {
										sfach = fach.getNodeValue();
									} else if (fach.getNodeType() == Node.ELEMENT_NODE) {
										sfach = fach.getChildNodes().item(0)
												.getNodeValue();
									}
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Fach Feld");
									sfach = "";
								}
								if (sfach == null||"+nbsp;".equals(sfach)) {
									sfach = "--";
								}

								String raum;
								try {
									raum = childnodes.item(4).getChildNodes()
											.item(0).getNodeValue();
								} catch (NullPointerException e) {
									Logger.i(TAG, "Leeres Raum Feld");
									raum = "";
								}

								String bemerkung;
								try {
									bemerkung = childnodes.item(6)
											.getChildNodes().item(0)
											.getNodeValue();
									bemerkung=bemerkung.replace("+nbsp;", "");
								} catch (NullPointerException e) {
									bemerkung = "Unknown";
								}


								String klausur;
								try {
									klausur = childnodes.item(8)
											.getChildNodes().item(0)
											.getNodeValue();
									klausur=klausur.replace("+nbsp;", "");
								} catch (NullPointerException e) {
									Logger.e(TAG, "test",e);
									klausur = "";
								}

								String vertreter;
								try {
									vertreter = childnodes.item(7)
											.getChildNodes().item(0)
											.getNodeValue();
									vertreter=vertreter.replace("+nbsp;", "");
								} catch (NullPointerException e) {
									vertreter = "";
								}


								vertretungen.add(new SchuelerVertretung(klasse,
										stunde, art, sfach, raum, tag, klausur,
										bemerkung,vertreter));

							}
						}
					}

				}
			}
		}
		// Vertretungen auslesen

		schuelerVertretungen = vertretungen;
		return true;
	}

	private Document getDoc(File file) {
		// Erstellen eines DocBuilder und parsen der PlanWebsite in ein
		// Document
		try {
			//DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			//DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			XMLReader reader = new Parser();
			  reader.setFeature(Parser.namespacesFeature, false);
			  reader.setFeature(Parser.namespacePrefixesFeature, false);


			Transformer transformer = TransformerFactory.newInstance().newTransformer();

			DOMResult result = new DOMResult();
			transformer.transform(new SAXSource(reader, new InputSource(new FileInputStream(file))),
					result);

			// here we go - an DOM built from abitrary HTML
			  Document doc= (Document) result.getNode();

			/*
			  Tidy tidy=new Tidy();
			tidy.setXHTML(true);
			tidy.setShowErrors(0);
			tidy.setPrintBodyOnly(true);
			tidy.setQuiet(true);


			Document doc=tidy.parseDOM(new FileInputStream(file), null);*/

			//Document doc = docBuilder.parse(file);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (IOException e) {
			Logger.e(TAG, "Lesen der Datei fehlgeschlagen", e);
			return null;
		} catch (Exception e) {
			Logger.e(TAG, "Fehler beim Parsen", e);
			return null;
		}
	}

	private ArrayList<Node> getElementsByClassName(NodeList elements,
			String className) {
		ArrayList<Node> found = new ArrayList<Node>();
		for (int i = 0; i < elements.getLength(); i++) {
			Node n = elements.item(i);
			if (n != null) {
				NamedNodeMap attr = n.getAttributes();
				if (attr != null && attr.getLength() > 0) {
					Node itemClass = attr.getNamedItem("class");
					if (itemClass != null) {
						String value = itemClass.getNodeValue();
						if (value != null) {
							if (value.contains(className)) {
								found.add(n);
							}
						}
					}
				}
			}
		}
		return found;
	}

	private String getSpace(int n) {
		if (n <= 0) {
			return "";
		}
		String s = "";
		for (int i = 0; i < n; i++) {
			s += "   ";
		}
		return s;
	}

	private String nodeToString(Node n, int depth) {
		if (n == null) {
			return "null";
		}
		String s = "Name: " + n.getNodeName() + " Type: " + n.getNodeType() + " Value: " + n.getNodeValue() + " TxtContent: " + n.getTextContent();
		NamedNodeMap m = n.getAttributes();
		if (m != null && m.getLength() > 0) {
			for (int i = 0; i < m.getLength(); i++) {
				s += "\n" + getSpace(depth) + "Attribute: " + nodeToString(m.item(i), depth + 1);
			}
		}
		NodeList l = n.getChildNodes();
		if (l != null && l.getLength() > 0) {
			for (int i = 0; i < l.getLength(); i++) {
				s += "\n" + getSpace(depth) + "Child: " + nodeToString(l.item(i), depth + 1);
			}
		}
		return s;
	}

	private void notifyUpdateFinishedListener(boolean update) {
		for (int i = 0; i < update_finished_listener.size(); i++) {
			if (update_finished_listener.get(i) != null) {
				update_finished_listener.get(i)
						.onVertretungsplanUpdateFinished(update);
			}
		}
	}

	private void notifyUpdateListener() {
		for (int i = 0; i < update_listener.size(); i++) {
			if (update_listener.get(i) != null) {
				update_listener.get(i).onVertretungsplanUpdate();
			}
		}
	}

	private String standHerausfinden(Document doc) {

		try {

			//Mon_Head Elemente finden
			NodeList table = doc.getElementsByTagName("table");
			Logger.i(TAG, table.getLength() + " Tableelemente");

			ArrayList<Node> mon_heads = getElementsByClassName(table, "mon_head");
			Logger.i(TAG, mon_heads.size() + " Mon Head Elemente");

			Log.d(TAG, "Stand herausfinden: Aufbau: Name Typ Value");
			NodeList table_childs = mon_heads.get(0).getChildNodes();
			Node tr = table_childs.item(0);
			Log.d(TAG, "TR Info: "+tr.getNodeName()+" "+tr.getNodeType()+" "+tr.getNodeValue()+" "+tr.getChildNodes().getLength());
			NodeList tr_childs=tr.getChildNodes();
			Node td = tr_childs.item(2);

			Log.d(TAG, "TD Info: "+td.getNodeName()+" "+td.getNodeType()+" "+td.getNodeValue()+" "+td.getChildNodes().getLength());
			NodeList td_childs=td.getChildNodes();
			Node text = td_childs.item(13);
			Log.d(TAG, "Font Info: "+text.getNodeName()+" "+text.getNodeType()+" "+text.getNodeValue()+" "+text.getChildNodes().getLength());
			String stand = text.getNodeValue().trim();
			return stand;

		} catch (Exception e1) {
			Logger.e(TAG, "Failed to retrieve Stand",e1);
			return "Login-Info vlt. falsch";
		}

		/* ALT
		// Stand herausfinden
		try {
			NodeList table = doc.getElementsByTagName("TABLE");
			if (table != null) {
				Logger.i(TAG, table.getLength() + " TABLE-Elemente gefunden");
				Node center = table.item(0);
				Node tr = center.getChildNodes().item(1);
				Logger.i(TAG, "Type: " + tr);
				Logger.i(TAG, "Childs: " + tr.getChildNodes().getLength());
				Node td = tr.getChildNodes().item(1);
				Logger.i(TAG, "Type: " + td);
				Logger.i(TAG, "Childs: " + td.getChildNodes().getLength());
				Logger.i(TAG, "Childs: " + td.getChildNodes().item(1));
				Node xyz = td.getChildNodes().item(1);
				Logger.i(TAG, "Type: " + xyz);
				Logger.i(TAG, "Childs: " + xyz.getChildNodes().getLength());
				String stand = xyz.getChildNodes().item(0).getNodeValue();

				Logger.i(TAG, "Stand: " + stand);
				return stand;
			} else {
				Logger.w(TAG, "Stand nicht gefunden");
				throw new Exception("TABLE im Document nicht gefunden");
			}
		} catch (Exception e) {
			return "Login-Info vlt. falsch";
		}
		*/
	}

	public interface OnUpdateFinishedListener {
		void onVertretungsplanUpdateFinished(boolean update);// Wenn
		// update==false
		// Keine
		// Änderung
	}

	// Listener-------------
	public interface OnUpdateListener {
		void onVertretungsplanUpdate();
	}

	private class AuswertenTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			return auswerten();
		}

		@Override
		protected void onPostExecute(Boolean update) {
			if (update) {
				notifyUpdateListener();
			}
			notifyUpdateFinishedListener(update);
		}
	}
}
