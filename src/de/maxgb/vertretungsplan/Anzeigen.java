package de.maxgb.vertretungsplan;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.R.color;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Anzeigen extends SherlockFragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private static final String PREFS_NAME = "Einstellungen";
	private static final String TAG = "Anzeigen_Activity";
	private static final String login_url = "https://www.ratsgymnasium-bielefeld.de/index.php/intern?task=user.login";
	private static final String plan_url = "https://www.ratsgymnasium-bielefeld.de/index.php/intern/vertretungsplan-schueler";
	private static final String loginsite_url = "https://www.ratsgymnasium-bielefeld.de/index.php/intern";

	SectionsPagerAdapter mSectionsPagerAdapter;
	private String username;
	private String password;
	private String klasse;
	public String cookie = "";
	private ProgressDialog progressDialog;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anzeigen);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		planAnzeigen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.anzeigen, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_refresh: // Aktualisieren

				if (isOnline()) {
					// Laden der Nutzereinstellungen
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					username = settings.getString("username", "");
					password = settings.getString("password", "");
					klasse = settings.getString("klasse", "");

					// --------------------------------------
					if (username != "" && password != "" && klasse != "") {
						new LoadPlanTask().execute();// Lädt
														// den
														// Plan
														// im
														// Hintergrund
					} else {
						fehler(new Exception("Bitte Nutzername, Passwort und Klasse einstellen"),this);
						Log.w(TAG,
								"Nutzername,Passwort oder Klasse nicht eingestellt");
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Keine Internetverbindung", Toast.LENGTH_SHORT)
							.show();

				}

				return true;
			case R.id.action_settings:// Optionen öffnen
				Log.i(TAG, "Optionen anzeigen");
				Intent i = new Intent();
				i.setClass(this, Options.class);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newconfig) {
		super.onConfigurationChanged(newconfig);
	}
	
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		boolean eigeneKurse=false;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
			eigeneKurse = settings.getBoolean("kurse_anzeigen",false);
			if (android.os.Build.VERSION.SDK_INT < 11) {
				eigeneKurse=false;
			}
			
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment;
			if(eigeneKurse){
				switch (position) {
					case 0:
						fragment = new EigeneKurseFragment();
						break;
					case 1:
						fragment = new EigeneStufeFragment();
						break;
					case 2:
						fragment = new AlleVertretungenFragment();
						break;
					default:
						fragment = new Fragment();
						//
	
				}
			}
			else{
				switch (position) {
					case 0:
						fragment = new EigeneStufeFragment();
						break;
					case 1:
						fragment = new AlleVertretungenFragment();
						break;
					default:
						fragment = new Fragment();
						//
				}
			}

			return fragment;
		}

		@Override
		public int getCount() {
			if(eigeneKurse){
				return 3;
			}
			else{
				return 2;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			if(eigeneKurse){
				switch (position) {
					case 0:
						return getString(R.string.title_section1).toUpperCase(l);
					case 1:
						return getString(R.string.title_section2).toUpperCase(l);
					case 2:
						return getString(R.string.title_section3).toUpperCase(l);
				}
			}
			else{
				switch (position) {
					case 0:
						return getString(R.string.title_section2).toUpperCase(l);
					case 1:
						return getString(R.string.title_section3).toUpperCase(l);
				}
			}
			return null;
		}
		
		@Override
		public int getItemPosition(Object object) {
			   return POSITION_NONE;
			}
	}

	/**
	 * Abstraktes Fragment mit einigen fertigen Methoden
	 * 
	 */

	private static abstract class AnzeigeFragment extends Fragment {
		String stand = "";

		protected ArrayList<Vertretung> auswerten(File file) throws Exception {
			try {
				Log.i(TAG, "Auswerten gestartet");
				// Erstellen eines DocBuilder und parsen der PlanWebsite in ein
				// Document
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory
						.newDocumentBuilder();
				Document doc = docBuilder.parse(file);
				doc.getDocumentElement().normalize();
				// ---------------------------------------------------------

				// Stand herausfinden
				try {
					NodeList table = doc.getElementsByTagName("TABLE");
					if (table != null) {
						Log.i(TAG, table.getLength()
								+ " TABLE-Elemente gefunden");
						Node center = table.item(0);
						Node tr = center.getChildNodes().item(1);
						Log.i(TAG, "Type: " + tr);
						Log.i(TAG, "Childs: " + tr.getChildNodes().getLength());
						Node td = tr.getChildNodes().item(1);
						Log.i(TAG, "Type: " + td);
						Log.i(TAG, "Childs: " + td.getChildNodes().getLength());
						Log.i(TAG, "Childs: " + td.getChildNodes().item(1));
						Node xyz = td.getChildNodes().item(1);
						Log.i(TAG, "Type: " + xyz);
						Log.i(TAG, "Childs: " + xyz.getChildNodes().getLength());
						String stand = xyz.getChildNodes().item(0)
								.getNodeValue();
						this.stand = stand;
						Log.i(TAG, "Stand: " + stand);
					} else {
						Log.w(TAG, "Stand nicht gefunden");
					}
				} catch (Exception e) {
				}
				// ------------------------------------------------

				// Vertretungen auslesen
				NodeList font = doc.getElementsByTagName("font"); // Filtern der
																	// neun
																	// Font-Nodes
				Log.i(TAG, font.getLength() + " Font-Elemente gefunden");

				ArrayList<Vertretung> vertretungen = new ArrayList<Vertretung>();
				for (int j = 0; j < font.getLength(); j += 3) { // Durchlaufen
																// der
																// Font-Nodes,
																// wobei
																// jede dritte
																// Node
																// ein Tag ist
					String tag = font.item(j).getChildNodes().item(1)
							.getChildNodes().item(0).getNodeValue();// Auslesen
																	// des
																	// Tags
					NodeList tr = font.item(j).getChildNodes().item(3)
							.getChildNodes();
					Log.i(TAG, tag + ": " + tr.getLength()
							+ " tr-Elemente gefunden");

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
										NodeList childnodes = node
												.getChildNodes();
										String klasse = childnodes.item(0)
												.getChildNodes().item(0)
												.getChildNodes().item(0)
												.getNodeValue();
										String stunde = childnodes.item(1)
												.getChildNodes().item(0)
												.getNodeValue();
										String art = childnodes.item(2)
												.getChildNodes().item(0)
												.getNodeValue();
										Node fach = childnodes.item(3)
												.getChildNodes().item(0);
										String sfach = null;
										if (fach.getNodeType() == Node.TEXT_NODE) {
											sfach = fach.getNodeValue();
										} else if (fach.getNodeType() == Node.ELEMENT_NODE) {
											sfach = fach.getChildNodes()
													.item(0).getNodeValue();
										}
										String raum = childnodes.item(4)
												.getChildNodes().item(0)
												.getNodeValue();
										if (sfach == null) {
											sfach = "--";
										}
										vertretungen.add(new Vertretung(klasse,
												stunde, art, sfach, raum, tag));

									}
								}
							}

						}
					}
				}
				Log.i(TAG, "Auswerten abgeschloï¿½en");
				return vertretungen;
				// ------------------------------------------------------------------

			} catch (SAXParseException err) {
				String fehler = "** Parsing error" + ", line "
						+ err.getLineNumber() + ", uri " + err.getSystemId()
						+ "\n Message: " + err.getMessage();
				Log.e(TAG, "Parsen fehlgeschlagen: ", err);
				throw new Exception(fehler);

			} catch (SAXException e) {
				Log.e(TAG, "Parsen fehlgeschlagen: ", e);
				throw new Exception("Auslesefehler");

			} catch (Exception t) {
				Log.e(TAG, "Auslesen fehlgeschlagen: ", t);
				throw t;
			}

		}

		protected void anzeigen(ArrayList<Vertretung> vertretungen, ScrollView s) {
			if (vertretungen != null && vertretungen.size() > 0) {
				Log.i(TAG, "Anzeigen gestartet");
				
				//Layout erstellen
				LayoutParams params = new LinearLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				TableRow.LayoutParams trparams = new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				trparams.setMargins(3, 3, 2, 6);
				LinearLayout l = new LinearLayout(getActivity());
				l.setLayoutParams(params);
				l.setOrientation(LinearLayout.VERTICAL);
				//------------------------------
				
				//Stand anzeigen
				SpannableString spanStand = new SpannableString(stand);
				spanStand.setSpan(new StyleSpan(Typeface.ITALIC), 0,
						spanStand.length(), 0);
				TextView tv_stand = new TextView(getActivity());
				tv_stand.setLayoutParams(new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				tv_stand.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.CENTER_HORIZONTAL);
				tv_stand.setText(spanStand);

				TableRow tr_stand = new TableRow(getActivity());
				tr_stand.setLayoutParams(new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				tr_stand.addView(tv_stand);

				l.addView(tr_stand);

				String tag = vertretungen.get(0).tag;
				TableLayout layoutTable = neuerTag(tag, l);

				for (int i = 0; i < vertretungen.size(); i++) {

					Vertretung v = vertretungen.get(i);
					if (tag != v.tag) {
						l.addView(layoutTable);
						tag = vertretungen.get(i).tag;
						layoutTable = neuerTag(tag, l);
					}
					TextView klasse = new TextView(getActivity());
					klasse.setLayoutParams(trparams);
					klasse.setText(v.klasse.trim() + " ");

					TextView stunde = new TextView(getActivity());
					stunde.setLayoutParams(trparams);
					stunde.setText(v.stunde.trim() + " ");

					TextView art = new TextView(getActivity());
					art.setLayoutParams(trparams);
					art.setText(v.art.trim() + " ");

					TextView fach = new TextView(getActivity());
					fach.setLayoutParams(trparams);
					fach.setText(v.fach.trim() + " ");

					TextView raum = new TextView(getActivity());
					raum.setLayoutParams(trparams);
					raum.setText(v.raum);

					TableRow tr = new TableRow(getActivity());
					tr.setLayoutParams(params);
					tr.addView(klasse);
					tr.addView(stunde);
					tr.addView(art);
					tr.addView(fach);
					tr.addView(raum);

					tr.setBackgroundColor(color.white);

					layoutTable.addView(tr);
				}
				l.addView(layoutTable);
				s.addView(l);
			}
			else{
				TableLayout table = new TableLayout(getActivity());
				SpannableString spanStand = new SpannableString(stand+": ");
				spanStand.setSpan(new StyleSpan(Typeface.ITALIC), 0,
						spanStand.length(), 0);
				TextView tv_stand = new TextView(getActivity());
				tv_stand.setLayoutParams(new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				tv_stand.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.CENTER_HORIZONTAL);
				tv_stand.setText(spanStand);

				TableRow tr_stand = new TableRow(getActivity());
				tr_stand.setLayoutParams(new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				tr_stand.addView(tv_stand);
				
				TextView leer = new TextView(getActivity());
				leer.setText("  ");
				TableRow leereReihe = new TableRow(getActivity());
				leereReihe.addView(leer);
				
				SpannableString spanNachricht = new SpannableString("Keine entsprechenden Vertretungen gefunden");
				spanNachricht.setSpan(new StyleSpan(Typeface.ITALIC), 0,
						spanNachricht.length(), 0);
				TextView tv_nachricht = new TextView(getActivity());
				tv_nachricht.setLayoutParams(new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				tv_nachricht.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.CENTER_HORIZONTAL);
				tv_nachricht.setText(spanNachricht);

				TableRow tr_nachricht = new TableRow(getActivity());
				tr_nachricht.setLayoutParams(new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				tr_nachricht.addView(tv_nachricht);
				
				
				table.addView(tr_stand);
				table.addView(leereReihe);
				table.addView(tr_nachricht);
				
				s.addView(table);
				

			}
		}

		private TableLayout neuerTag(String tag, LinearLayout l) {
			LayoutParams params = new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			TableRow.LayoutParams trparams = new TableRow.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			trparams.setMargins(3, 3, 2, 6);

			if (l.getChildCount() != 1) {
				TextView leer = new TextView(getActivity());
				leer.setText("  ");
				TableRow leereReihe = new TableRow(getActivity());
				leereReihe.setLayoutParams(trparams);
				leereReihe.addView(leer);
				l.addView(leereReihe);
			}

			SpannableString spanString = new SpannableString(tag);
			spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
			spanString.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spanString.length(), 0);
			TextView tv1 = new TextView(getActivity());
			tv1.setText(spanString);
			TableRow tr1 = new TableRow(getActivity());
			// tr.setLayoutParams(trparams);
			tr1.addView(tv1);
			l.addView(tr1);

			TableLayout layoutTable = new TableLayout(getActivity());
			layoutTable.setLayoutParams(params);
			layoutTable.setBackgroundColor(color.black);

			TableRow tr2 = new TableRow(getActivity());
			tr2.setLayoutParams(trparams);

			TextView klasse = new TextView(getActivity());
			TextView stunde = new TextView(getActivity());
			TextView art = new TextView(getActivity());
			TextView fach = new TextView(getActivity());
			TextView raum = new TextView(getActivity());

			SpannableString spanKlasse = new SpannableString("Klasse ");
			spanKlasse.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spanKlasse.length(), 0);

			SpannableString spanStunde = new SpannableString("Stunde ");
			spanStunde.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spanStunde.length(), 0);

			SpannableString spanArt = new SpannableString("Art ");
			spanArt.setSpan(new StyleSpan(Typeface.BOLD), 0, spanArt.length(),
					0);

			SpannableString spanFach = new SpannableString("Fach ");
			spanFach.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spanFach.length(), 0);

			SpannableString spanRaum = new SpannableString("Raum ");
			spanRaum.setSpan(new StyleSpan(Typeface.BOLD), 0,
					spanRaum.length(), 0);

			klasse.setText(spanKlasse);
			stunde.setText(spanStunde);
			art.setText(spanArt);
			fach.setText(spanFach);
			raum.setText(spanRaum);
			
			tr2.setLayoutParams(params);
			tr2.addView(klasse);
			tr2.addView(stunde);
			tr2.addView(art);
			tr2.addView(fach);
			tr2.addView(raum);

			tr2.setBackgroundColor(color.white);
			layoutTable.addView(tr2);
			return layoutTable;

		}
	}

	/**
	 * Ein Fragment, dass Vertretungen für die eigenen Kurse anzeigt
	 */

	@SuppressLint("NewApi")
	public static class EigeneKurseFragment extends AnzeigeFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_eigene_kurse,
					container, false);
			File f = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/vertretungsplan/plan.html");
			try {
				if (f.exists()) { // Wenn Datei vorhanden->Auswerten
					ArrayList<Vertretung> vertretungen = auswerten(f);

					SharedPreferences settings = getActivity()
							.getSharedPreferences(PREFS_NAME, 0);
					String klasse = settings.getString("klasse", null);
					Set<String> kurse = settings.getStringSet("kurse", null);

					ArrayList<Vertretung> eigeneVertretungen = new ArrayList<Vertretung>();
					if (klasse != null && klasse != "") {
						for (int i = 0; i < vertretungen.size(); i++) {
							if (vertretungen.get(i).klasse.trim().equals(
									klasse.trim())) {

								if (kurse != null) {
									if (kurse
											.contains(vertretungen.get(i).fach)) {
										eigeneVertretungen.add(vertretungen
												.get(i));
									}
								} else {
									eigeneVertretungen.add(vertretungen.get(i));
								}
							}
						}
					}

					ScrollView l = (ScrollView) rootView
							.findViewById(R.id.android_eigene_kurse_layout);
					anzeigen(eigeneVertretungen, l);

				}

			} catch (Exception e) {
				fehler(e, getActivity());
			}
			return rootView;
		}

	}

	/**
	 * Ein Fragment, dass Vertretungen für die eigene Stufe anzeigt
	 */

	public static class EigeneStufeFragment extends AnzeigeFragment {

		// TODO Nur Vertretugen am anfang neu auslesen;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_eigene_stufe,
					container, false);

			File f = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/vertretungsplan/plan.html");
			try {
				if (f.exists()) { // Wenn Datei vorhanden->Auswerten
					ArrayList<Vertretung> vertretungen = auswerten(f);

					SharedPreferences settings = getActivity()
							.getSharedPreferences(PREFS_NAME, 0);
					String klasse = settings.getString("klasse", null);

					ArrayList<Vertretung> eigeneVertretungen = new ArrayList<Vertretung>();
					if (klasse != null && klasse != "") {
						for (int i = 0; i < vertretungen.size(); i++) {
							if (vertretungen.get(i).klasse.trim().equals(
									klasse.trim())) {
								eigeneVertretungen.add(vertretungen.get(i));
							}
						}
					}

					ScrollView l = (ScrollView) rootView
							.findViewById(R.id.android_eigene_stufe_layout);
					anzeigen(eigeneVertretungen, l);

				}

			} catch (Exception e) {
				fehler(e, getActivity());
			}

			return rootView;
		}
	}

	/**
	 * Ein Fragment, dass alle Vertretungen anzeigt
	 */

	public static class AlleVertretungenFragment extends AnzeigeFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_alle_vertretungen, container, false);

			File f = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/vertretungsplan/plan.html");
			try {
				if (f.exists()) { // Wenn Datei vorhanden->Auswerten
					ArrayList<Vertretung> vertretungen = auswerten(f);

					ScrollView l = (ScrollView) rootView
							.findViewById(R.id.android_alle_vertretungen_layout);
					anzeigen(vertretungen, l);

				}
			} catch (Exception e) {
				fehler(e, getActivity());
			}

			return rootView;
		}
	}

	// ----------Plan Abrufen

	@SuppressLint("NewApi")
	public void planAnzeigen() {

		// Irgendwas- Schon wieder vergessen- Irgendeine Fehlervorbeugung
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		// --------------------------------------
		// Laden der Nutzereinstellungen
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		username = settings.getString("username", "");
		password = settings.getString("password", "");
		klasse = settings.getString("klasse", "");

		// --------------------------------------
		if (username != "" && password != "" && klasse != "") {

			if (isOnline()) {
				new LoadPlanTask().execute();
			}// Bei Internetverbindung Plan aktualiseren und anzeigen
			else { // Ohne Internet Verbindung
				Toast.makeText(getApplicationContext(),
						"Keine Internetverbindung", Toast.LENGTH_SHORT).show(); // Anzeige
																				// von
																				// "Keine
																				// Internetverbindung"
			}
		}
		else{
			fehler(new Exception("Bitte Nutzername,Passwort und Klasse einstellen"),this);
		}
	}

	public boolean planAbrufen() {

		File dir = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/vertretungsplan/");
		dir.mkdirs(); // Erstellen des Verzeichnises falls noch nicht vorhanden
		Log.i(TAG, "Anfrage gestartet");
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpClient httpclient = new MyHttpsClient(getApplicationContext(),
				httpParams); // neuer HttpsClient mit eigener Timeoutzeit

		try {

			abrufen(httpclient);// abrufen der Loginseite
			login(httpclient, username, password);// Login -- gleicher
													// HttpClient
			auslesen(httpclient);// Speichern der Loginseite -- gleicher
									// HttpClient

			File f = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/vertretungsplan/plan.html");
			if (f.exists()) {
				Log.i(TAG, "Anfrage erfolgreich abgeschloï¿½en");
				return true;
			} else
				throw new Exception("Datei nicht gefunden");

		} catch (Exception e) {
			fehler(e);
			return false;
		}

	}

	// Asynchrones Laden des Plans mit Hilfe von AsyncTask
	private class LoadPlanTask extends AsyncTask<String, Void, Boolean> {
		// Vor ausführen in einem seperaten Task
		@Override
		protected void onPreExecute() {
			// Neuer progress dialog
			progressDialog = new ProgressDialog(Anzeigen.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle("Lädt...");
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();

		}

		/**
		 * BackgroundProzess
		 * 
		 * @params params leer
		 */

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				// Get the current thread`s token ????
				synchronized (this) {
					return planAbrufen();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		// after Execution
		@Override
		protected void onPostExecute(Boolean result) {
			// ProgressDialog schlieï¿½en
			progressDialog.dismiss();
			mSectionsPagerAdapter.notifyDataSetChanged();
			

		}
	}

	/**
	 * Ruft die Loginseite ab
	 * 
	 * @param httpclient
	 *            Httpsclient zum abrufen(HttpsClient mit Ratszertifikat
	 *            benÃ¶tigt)
	 * @return Erfolg der Anfrage
	 */
	public boolean abrufen(HttpClient httpclient) throws Exception {
		try {
			Log.i(TAG, "Abrufen der Loginseite gestartet");
			HttpResponse response = httpclient.execute(new HttpGet(
					loginsite_url)); // Abruf der Seite
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // Erfolg
																				// der
																				// Anfrage
				Log.i(TAG, "Erfolgreicher Loginseiten Abruf");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out); // Schreiben der Loginseite
													// in den
													// ByteArrayOutputStream
				out.close();
				String responseString = out.toString(); // Umwandlung des
														// ByteArrayOutputStream(Loginseite)
														// in einen String

				// save(responseString, "debug_login.html"); // Speichern der
				// Seite
				// zum Debuggen

				String gesucht = "<input type=\"hidden\" name=\"return\" value=\"L2luZGV4LnBocC9pbnRlcm4v\" />\n      <input type=\"hidden\" name=";// SuchString
				int index = responseString.indexOf(gesucht);// Sucht mit dem
															// Suchstring nach
															// Anfang des
															// "Cookies"
				// System.out.println(index);
				char[] chars = responseString.toCharArray();
				cookie = String.copyValueOf(chars,
						index + gesucht.length() + 1, 32);// Auslesen des
															// "Cookies"(LÃ¤nge:
															// 32) und speichern
															// in der globalen
															// Variable cookie
				Log.i(TAG, "Cookie ausgelesen. Wert: " + cookie);

				return true;

			} else {
				StatusLine statusLine = response.getStatusLine();
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());// Fehlermeldung
																	// werfen
																	// wenn
																	// Abfrage
																	// fehlgeschlagen
			}

		} catch (Exception e) {
			Log.e(TAG, "Fehlgeschlagener Loginseiten Abruf. Fehler: ", e);
			throw new Exception(e.getMessage());
		}

	}

	/**
	 * Login mit Http-Post
	 * 
	 * @param httpclient
	 *            HttpClient
	 * @param username
	 *            Nutzername
	 * @param password
	 *            Passwort
	 * @return Erfolg der Anfrage
	 */
	public boolean login(HttpClient httpclient, String username, String password)
			throws Exception {

		try {
			Log.i(TAG, "Loginvorgang gestartet");
			// Log.i(TAG,"Loginvorgang gestartet. Username: "+username+" Passwort: "+password);

			// Erstellen des Postrequest
			HttpPost httppost = new HttpPost(login_url);

			List<NameValuePair> paare = new ArrayList<NameValuePair>(2); // Post-Parameter
			paare.add(new BasicNameValuePair("username", username));
			paare.add(new BasicNameValuePair("password", password));
			paare.add(new BasicNameValuePair("return",
					"L2luZGV4LnBocC9pbnRlcm4v")); // Unbekannte Funktion
			paare.add(new BasicNameValuePair(cookie, "1"));
			httppost.setEntity(new UrlEncodedFormEntity(paare));
			// -----------------------------------------------
			HttpResponse response = httpclient.execute(httppost);// Login mit
																	// Http-Post

			// Siehe abrufen()
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Log.i(TAG,
						"Loginvorgang erfolgreich abgeschloï¿½en. Status aber unbekannt");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// String responseString = out.toString();

				// save(responseString, "debug_login2.html");

				return true;
			} else {
				StatusLine statusLine = response.getStatusLine();
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
			// ------------------------------------------------

		} catch (Exception e) {
			Log.e(TAG, "Loginvorgang fehlgeschlagen");
			throw e;

		}
	}

	/**
	 * Abruf und speichern der Planseite
	 * 
	 * @param client
	 *            HttpClient
	 */
	public void auslesen(HttpClient client) throws Exception {
		try {
			Log.i(TAG, "Abrufen des Plans und Auslesen gestartet");
			HttpResponse response = client.execute(new HttpGet(plan_url));// Planseite
																			// abrufen

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Log.i(TAG, "Abrufen des Plans erfolgreich abgeschloï¿½en");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();

				save(responseString, "plan.html");

			} else {
				StatusLine statusLine = response.getStatusLine();
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (Exception e) {
			Log.e(TAG, "Planabruf fehlgeschlagen: ", e);
			throw new Exception(e.getMessage());
		}

	}

	public void save(String s, String file) throws IOException {
		Log.i(TAG, "Speichern der Datei: " + file + " gestartet");
		FileWriter o = new FileWriter(Environment.getExternalStorageDirectory()
				.getPath() + "/vertretungsplan/" + file, false);
		BufferedWriter bw = new BufferedWriter(o);
		bw.write(s);
		bw.close();
		o.close();
		Log.i(TAG, "Speichern der Datei: " + file + " abgeschloï¿½en");

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		return false;
	}

	public void fehler(Exception e) {

		Log.e(TAG, "Fehler", e);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(e.getMessage()).setTitle("Fehler");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void fehler(Exception e, Context c) {
		Log.e(TAG, "Fehler", e);
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setMessage(e.getMessage()).setTitle("Fehler");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

}
