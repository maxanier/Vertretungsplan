package com.example.vertretungsplan;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

public class Anzeige extends Activity {
	private static final String login_url="https://www.ratsgymnasium-bielefeld.de/index.php/intern?task=user.login";
	private static final String plan_url="https://www.ratsgymnasium-bielefeld.de/index.php/intern/vertretungsplan-schueler";
	private static final String loginsite_url="https://www.ratsgymnasium-bielefeld.de/index.php/intern";
	public static final String newline = System.getProperty("line.separator");
	private static final String PREFS_NAME = "Einstellungen";
	private static final String TAG = "Anzeige_Activity";
	private static final String no_username ="<html><body><p style=\"padding-top:40%;\"><div align=\"center\">Bitte Nutzernamen, Passwort und Klasse einstellen</div></p></body></html>";
	private static final String no_internet="<html><body><p style=\"padding-top:40%;\"><div align=\"center\">Keine Internetverbindung</div></p></body></html>";
	
	private WebView webview=null;
	private ProgressDialog progressDialog;
	private String username;
	private String password;
	private String klasse;
	private boolean initialisiert=false; //true wenn das Layout geladen wurde
	
	public String cookie="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Irgendwas- Schon wieder vergessen- Irgendeine Fehlervorbeugung
		if (android.os.Build.VERSION.SDK_INT > 9) { 
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		//--------------------------------------
		//Laden der Nutzereinstellungen
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		username = settings.getString("username","");
		password = settings.getString("password","");
		klasse = settings.getString("klasse","");
		//--------------------------------------
		if(username!=""&&password!=""&&klasse!=""){
				
			if(isOnline()){
				new LoadPlanTask().execute(username,password,klasse);}//Bei Internetverbindung Plan aktualiseren und anzeigen
			else{	//Ohne Internet Verbindung
				Toast.makeText(getApplicationContext(), "Keine Internetverbindung", Toast.LENGTH_SHORT).show();	//Anzeige von "Keine Internetverbindung
				setContentView(R.layout.activity_anzeige); //Layout laden
				webview = (WebView) findViewById(R.id.webView1); //Webview  finden und speichern
				File f=new File(Environment.getExternalStorageDirectory().getPath()+"/vertretungsplan/plan.html"); 
				if(f.exists()){
				Log.i("Anzeige ohne Internetverbindung");
				webview.loadData(anzeigen(auswerten(f),username, klasse),"text/html; charset=UTF-8",null)//Gespeicherten Plan anzeigen
				}
			}
			
		}
		else
		{
			setContentView(R.layout.activity_anzeige); //Layout laden
			webview = (WebView) findViewById(R.id.webView1); //Webview  finden und speichern
			webview.loadData(no_username,"text/html; charset=UTF-8",null); //Anzeige der Fehlermeldung, dass no_username
			Log.w(TAG,"Nutzername,Passwort oder Klasse nicht eingestellt");
		}		
		

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.anzeige, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.action_refresh: //Aktualisieren
				SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
				username = settings.getString("username","");
				password = settings.getString("password","");
				klasse = settings.getString("klasse","");
				if(isOnline()){	
				
					if(username!=""&&password!=""&&klasse!=""){					
						new LoadPlanTask().execute(username,password,klasse);//L�dt den Plan im Hintergrund
					}
					else
					{
						webview.loadData(no_username,"text/html; charset=UTF-8",null);//Zeigt no_username Fehler an
						Log.w(TAG,"Nutzername,Passwort oder Klasse nicht eingestellt");
					}
				}
				else{
					Toast.makeText(getApplicationContext(), "Keine Internetverbindung", Toast.LENGTH_SHORT).show();
					
				}
			
			return true;
		case R.id.action_settings://Optionen �ffnen
			Log.i(TAG,"Optionen anzeigen");
			Intent i=new Intent();
			i.setClass(this, Options.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	//Asynchrones Laden des Plans mit Hilfe von AsyncTask
	private class LoadPlanTask extends AsyncTask<String, Void, String>
	{
		//Vor ausf�hren in einem seperaten Task
		@Override
		protected void onPreExecute(){
			//Neuer progress dialog
			progressDialog = new ProgressDialog(Anzeige.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle("L�dt...");
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
			
		}
		/**
		 * BackgroundProzess
		 * @params params Nutzername,passwort,klasse
		 * /
		protected String doInBackground(String... params)
		{
			try{
				//Get the current thread`s token ????
				synchronized(this)
				{
					return planAnzeigen(params[0],params[1],params[2]);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		//after Execution
		@Override
		protected void onPostExecute(String result)
		{
			//ProgressDialog schlie�en
			progressDialog.dismiss();
			if(!initialisiert){
			//initialisiere View
			setContentView(R.layout.activity_anzeige);
			//finde WebView
			webview = (WebView) findViewById(R.id.webView1);}
			//Ergebnis Anzeigen
			webview.loadData(result,"text/html; charset=UTF-8",null);
			Toast.makeText(getApplicationContext(), "Aktualisiert", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * zeigt den Plan an
	 * @params username Nutzername für Login
	 * @params passwort Passwort für Login
	 * @params klasse Klasse
	 * @return Html-String mit Plan
	 * /

	public String planAnzeigen(String username,String password,String klasse)
	{
		File dir = new File(Environment.getExternalStorageDirectory().getPath( )+"/vertretungsplan/");
		dir.mkdirs();	//Erstellen des Verzeichnises falls noch nicht vorhanden
		Log.i(TAG,"Anfrage gestartet");
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,10000);
		HttpConnectionParams.setSoTimeout(httpParams,10000);
		HttpClient httpclient = new MyHttpsClient(getApplicationContext(),httpParams); //neuer HttpsClient mit eigener Timeoutzeit
		
		try{
			
			abrufen(httpclient);//abrufen der Loginseite 
			login(httpclient,username,password);//Login -- gleicher HttpClient
			auslesen(httpclient);//Speichern der Loginseite -- gleicher HttpClient
			
			File f=new File(Environment.getExternalStorageDirectory().getPath()+"/vertretungsplan/plan.html");
			if(f.exists()){ //Wenn Datei vorhanden->Auswerten und erstellen des HTML Codes
				Log.i(TAG,"Anfrage erfolgreich abgeschlo�en");
				return anzeigen(auswerten(f),username, klasse);
			}
			else{
				throw new Exception("Datei nicht gefunden");
			}
			
		}
		catch(Exception e){
			Log.e(TAG,"Anfrage fehlgeschlagen: ",e);
			return e.getMessage();
		}
		

		

	
	}
	/**
	 * Ruft die Loginseite ab
	 * @param httpclient Httpsclient zum abrufen(HttpsClient mit Ratszertifikat benötigt)
	 * @return Erfolg der Anfrage
	 * /
	public boolean abrufen(HttpClient httpclient) throws Exception
	{
		try{
			Log.i(TAG,"Abrufen der Loginseite gestartet");
			HttpResponse response = httpclient.execute(new HttpGet(loginsite_url)); //Abruf der Seite
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){ //Erfolg der Anfrage
				Log.i(TAG,"Erfolgreicher Loginseiten Abruf");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			        response.getEntity().writeTo(out); //Schreiben der Loginseite in den ByteArrayOutputStream
			        out.close();
			        String responseString = out.toString(); //Umwandlung des ByteArrayOutputStream(Loginseite) in einen String
			        
			        
			        save(responseString,"debug_login.html"); //Speichern der Seite zum Debuggen
			        
			        String gesucht="<input type=\"hidden\" name=\"return\" value=\"L2luZGV4LnBocC9pbnRlcm4v\" />\n      <input type=\"hidden\" name=";//SuchString
			        int index = responseString.indexOf(gesucht);//Sucht mit dem Suchstring nach Anfang des "Cookies"
			        //System.out.println(index);
			        char[] chars=responseString.toCharArray();
			        cookie=String.copyValueOf(chars,index+gesucht.length()+1,32);//Auslesen des "Cookies"(Länge: 32) und speichern in der globalen Variable cookie
			        Log.i(TAG,"Cookie ausgelesen. Wert: "+cookie);
			        
			        return true;
		        
			}
			else
			{
				StatusLine statusLine=response.getStatusLine();
				response.getEntity().getContent().close();
		        throw new IOException(statusLine.getReasonPhrase());//Fehlermeldung werfen wenn Abfrage fehlgeschlagen
			}	
		
		}
		catch (Exception e)
		{
			Log.e(TAG,"Fehlgeschlagener Loginseiten Abruf. Fehler: ",e);
			throw new Exception(e.getMessage());
		}
		
	}
	
	/**
	 * Login mit Http-Post
	 * @param httpclient HttpClient
	 * @param username Nutzername
	 * @param password Passwort
	 * @return Erfolg der Anfrage
	 * /
	public boolean login(HttpClient httpclient,String username,String password) throws Exception
	{
		
		try{
			Log.i(TAG,"Loginvorgang gestartet");
			//Log.i(TAG,"Loginvorgang gestartet. Username: "+username+" Passwort: "+password);
			
			//Erstellen des Postrequest
			HttpPost httppost = new HttpPost(login_url);
			
			List<NameValuePair> paare = new ArrayList<NameValuePair>(2); //Post-Parameter
			paare.add(new BasicNameValuePair("username",username));
			paare.add(new BasicNameValuePair("password",password));
			paare.add(new BasicNameValuePair("return","L2luZGV4LnBocC9pbnRlcm4v")); //Unbekannte Funktion
			paare.add(new BasicNameValuePair(cookie,"1"));
			httppost.setEntity(new UrlEncodedFormEntity(paare));
			//-----------------------------------------------
			HttpResponse response = httpclient.execute(httppost);//Login mit Http-Post
			
			//Siehe abrufen()
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				Log.i(TAG,"Loginvorgang erfolgreich abgeschlo�en. Status aber unbekannt");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			        response.getEntity().writeTo(out);
			        out.close();
			        String responseString = out.toString();
			        
			        
			        save(responseString,"debug_login2.html");
		        
		        return true;
			}
			else
			{
				StatusLine statusLine=response.getStatusLine();
				response.getEntity().getContent().close();
		        throw new IOException(statusLine.getReasonPhrase());
			}
			//------------------------------------------------
			
		}
		catch(Exception e)
		{
			Log.e(TAG,"Loginvorgang fehlgeschlagen: ",e);
			throw new Exception(e.getMessage());
			
		}		
	}
	
	/**
	 * Abruf und speichern der Planseite
	 * @param client HttpClient
	 * /
	public void auslesen(HttpClient client) throws Exception
	{
		try{
			Log.i(TAG,"Abrufen des Plans und Auslesen gestartet");
			HttpResponse response = client.execute(new HttpGet(plan_url));//Planseite abrufen
			
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				Log.i(TAG,"Abrufen des Plans erfolgreich abgeschlo�en");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			        response.getEntity().writeTo(out);
			        out.close();
			        String responseString = out.toString();
			        
			        save(responseString,"plan.html");
		        
		        
			}
			else
			{
				StatusLine statusLine=response.getStatusLine();
				response.getEntity().getContent().close();
		        throw new IOException(statusLine.getReasonPhrase());
			}
		}
		catch(Exception e)
		{
			Log.e(TAG,"Planabruf fehlgeschlagen: ",e);
			throw new Exception(e.getMessage());
		}

	}
	
	/**
	 * Auswerten der gespeicherten Plan Datei
	 * @param file gespeicherte Plandatei
	 * @return Array mit allen Vertretungen
	 * /
	
	public ArrayList<Vertretung> auswerten(File file) throws Exception
	{
		try{
			Log.i(TAG,"Auswerten gestartet");
			//Erstellen eines DocBuilder und parsen der PlanWebsite in ein Document
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			//---------------------------------------------------------
			
			doc.getDocumentElement().normalize();
			
			NodeList font = doc.getElementsByTagName("font"); //Filtern der neun Font-Nodes
			Log.i(TAG,font.getLength()+" Font-Elemente gefunden");
			
			ArrayList<Vertretung> vertretungen=new ArrayList<Vertretung>();
			for(int j=0;j<font.getLength();j+=3){ //Durchlaufen der  Font-Nodes, wobei jede dritte Node ein Tag ist
				String tag=font.item(j).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();//Auslesen des Tags
				NodeList tr=font.item(j).getChildNodes().item(3).getChildNodes();
				Log.i(TAG,tag+": "+tr.getLength()+" tr-Elemente gefunden");
					

				for(int i=2;i<tr.getLength();i++)//Durchlaufen aller tr-Elemente
				{
					Node node = tr.item(i);
					//System.out.println(node.getNodeValue()+"---"+node.getNodeName());
					//Überprüfen von welchem Typ die Node ist und ob sie Attribute hat und dann Überprüfen der class, ob es sich im eine Vertretung handelt
					if(node.getNodeName()!="#text"){
						NamedNodeMap attr = node.getAttributes();
						//System.out.println(attr.getLength());
						if(attr.getLength()>0)
						{
							Node attrclass= attr.getNamedItem("class");
							if(attrclass!=null)
							{
								String value=attrclass.getNodeValue();
								//System.out.println(value);
								if(value.indexOf("list odd")!=-1||value.indexOf("list even")!=-1)
								{
									//Vertretung gefunden
									NodeList childnodes = node.getChildNodes();
									String klasse= childnodes.item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
									String stunde = childnodes.item(1).getChildNodes().item(0).getNodeValue();
									String art = childnodes.item(2).getChildNodes().item(0).getNodeValue();
									String fach = childnodes.item(3).getChildNodes().item(0).getNodeValue();
									String raum = childnodes.item(4).getChildNodes().item(0).getNodeValue();
									if(fach==null){fach="--";}
									vertretungen.add(new Vertretung(klasse,stunde,art,fach,raum,tag));
									
									
								}
							}
						}
				
					}
				}
			}
			Log.i(TAG,"Auswerten abgeschlo�en");
			return vertretungen;
			
			
			
		}
		catch (SAXParseException err) {
		String fehler="** Parsing error" + ", line " 
	             + err.getLineNumber () + ", uri " + err.getSystemId ()+"\n Message: "+err.getMessage ();
		Log.e(TAG,"Parsen fehlgeschlagen: ",err);
		throw new Exception(fehler);

        }catch (SAXException e) {
        Log.e(TAG,"Parsen fehlgeschlagen: ",e);
        throw new Exception("Auslesefehler");

        }catch (Exception t) {
        Log.e(TAG,"Auslesen fehlgeschlagen: ",t);
        throw t;
        }
		
	}
	
	/**
	 * Auswählen der Vertretungen und erzeugen einer HTML-Datei als String
	 * @param vertretungen Alle gefunden Vertretungen
	 * @param username Nutzername für Fehlermeldung
	 * @param klasse Klasse für Fehlermeldung
	 * @return Html-String mit Vertretungen
	 * /
	
	public String anzeigen(ArrayList<Vertretung> vertretungen,String username, String klasse) throws Exception
	{
		Log.i(TAG,"Anzeigen gestartet");
		if(vertretungen!=null&&vertretungen.size()>0)
		{
			boolean gefunden=false;
			String tag=vertretungen.get(0).tag;
			String ergebnis="<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /></head><body><div align=\"center\">Datum: "+tag+"\n<table border=\"1\"><tr><th><font size=\"-1\">Klasse</font></th>  <th><font size=\"-1\">Stunde</font></th>  <th><font size=\"-1\">Art</font></th>  <th><font size=\"-1\">Fach</font></th>  <th><font size=\"-1\">Raum</font></th></tr>\n";
			for(int i=0;i<vertretungen.size();i++){
				
				Vertretung v=vertretungen.get(i);
				if(tag!=v.tag){
				tag=v.tag;
				ergebnis+="</table>\n";
				ergebnis+=newline+"Datum: "+tag+"\n";
				ergebnis+="<table border=\"1\"><tr><th><font size=\"-1\">Klasse</font></th>  <th><font size=\"-1\">Stunde</font></th>  <th><font size=\"-1\">Art</font></th>  <th><font size=\"-1\">Fach</font></th>  <th><font size=\"-1\">Raum</font></th></tr>\n";
					
				}
				//System.out.println("Gesuchte Klasse: "+klasse+" Gefundene Klasse: "+v.klasse+"|");
				if(klasse.trim().equals("ALL")||v.klasse.trim().equals(klasse.trim())||v.klasse.trim().equals("("+klasse.trim()+")")){
					
					ergebnis+="<tr>";
					ergebnis+="<th><font size=\"-1\">" + v.klasse+"</font></th>  ";
					ergebnis+="<th><font size=\"-1\">"+v.stunde+"</th>  ";
					ergebnis+="<th><font size=\"-1\">"+v.art+"</font></th>  ";
					ergebnis+="<th><font size=\"-1\">"+v.fach+"</font></th>  ";
					ergebnis+="<th><font size=\"-1\">"+v.raum+"</font></th>  ";
					ergebnis+=("</tr>\n");
					gefunden=true;
					
				}
			}
			ergebnis+="</table></div></body></html>";
			if(!gefunden)
			{
				ergebnis="<html><body><p style=\"padding-top:40%;\"><div align=\"center\">Keine Vertretungen f�r die gew�hlte Stufe/Klasse("+klasse+")</div></p></body></html>";
			}
			Log.i(TAG,"Anzeigen abgeschlo�en");
			return ergebnis;
		
		}
		else{
			Log.e(TAG,"Keine Vertretungen angekommen");
			throw new Exception("Fehler: Vermutlich falscher Benutzername oder falsches Passwort gew&aum;lhlt:\n Nutzername: "+username+" Passwort: *****");
		}
		
	}
	
	/**
	 * Speichern eines Strings in einer angegebenen Datei
	 * @param s Zu speichernde String
	 * @param file Speicherort
	 * /
	public void save(String s,String file) throws IOException
	{
		Log.i(TAG,"Speichern der Datei: "+file+" gestartet");
        FileWriter o=new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/vertretungsplan/"+file,false);
        BufferedWriter bw=new BufferedWriter(o);
        bw.write(s);
        bw.close();
        o.close();
        Log.i(TAG,"Speichern der Datei: "+file+" abgeschlo�en");
		
	}
	/**
	 * Überprüfen der Internetverbindung
	 * return Verbindungsstatus
	 * /
	public boolean isOnline()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
			return true;
		}
		return false;
	}
	

}
