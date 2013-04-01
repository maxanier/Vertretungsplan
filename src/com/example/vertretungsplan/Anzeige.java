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

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.webkit.WebView;

public class Anzeige extends Activity {
	private static final String login_url="https://www.ratsgymnasium-bielefeld.de/index.php/intern?task=user.login";
	private static final String plan_url="https://www.ratsgymnasium-bielefeld.de/index.php/intern/vertretungsplan-schueler";
	private static final String loginsite_url="https://www.ratsgymnasium-bielefeld.de/index.php/intern";
	public static final String newline = System.getProperty("line.separator");
	public static final String PREFS_NAME = "Einstellungen";
	private WebView webview=null;
	private String username;
	private String password;
	private String klasse;
	
	public String cookie="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anzeige);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		username = settings.getString("username","");
		password = settings.getString("password","");
		klasse = settings.getString("klasse","");
		System.out.println("Nutzername: "+username+" Passwort: "+password+" Klasse: "+klasse);
		webview = (WebView) findViewById(R.id.webView1);
		if(username!=""&&password!=""&&klasse!=""){
			
		planAnzeigen(username, password,klasse);
		}
		else
		{
			webview.loadData("Bitte Nutzernamen, Passwort und Klasse einstellen","text/html",null);
		}
		
		

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.anzeige, menu);
		return true;
	}
	
	
	public void planAnzeigen(String username,String password,String klasse)
	{
		File dir = new File(Environment.getExternalStorageDirectory().getPath( )+"/vertretungsplan/");
		dir.mkdirs();
		System.out.println("Anfrage gestartet");
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,5000);
		HttpConnectionParams.setSoTimeout(httpParams,5000);
		HttpClient httpclient = new MyHttpsClient(getApplicationContext(),httpParams);
		
		if(abrufen(httpclient,username,password)){
			if(login(httpclient,username,password)){
				auslesen(httpclient);
			}
		}
		File f=new File(Environment.getExternalStorageDirectory().getPath()+"/vertretungsplan/plan.html");
		if(f.exists()){
			anzeigen(auswerten(f),klasse);
		}
		else{
			System.out.println("Keine Datei gefunden");
		}

	
	}
	
	public boolean abrufen(HttpClient httpclient,String username,String password)
	{
		try{
			
			HttpResponse response = httpclient.execute(new HttpGet(loginsite_url));
			System.out.println("Anfrage abgeschloﬂen");
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				System.out.println("Erfolgreicher Loginseiten Abruf");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		        response.getEntity().writeTo(out);
		        out.close();
		        String responseString = out.toString();
		        
		        
		        save(responseString,"debug_login.html");
		        
		        String gesucht="<input type=\"hidden\" name=\"return\" value=\"L2luZGV4LnBocC9pbnRlcm4v\" />\n      <input type=\"hidden\" name=";
		        int index = responseString.indexOf(gesucht);
		        System.out.println(index);
		        char[] chars=responseString.toCharArray();
		        cookie=String.copyValueOf(chars,index+gesucht.length()+1,32);
		        System.out.println(cookie);
		        
		        return true;
		        
			}
			else
			{
				StatusLine statusLine=response.getStatusLine();
				response.getEntity().getContent().close();
		        throw new IOException(statusLine.getReasonPhrase());
			}	
		
		}
		catch (Exception e)
		{
			System.out.println("Fehlgeschlagener Loginseiten Abruf:");
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	public boolean login(HttpClient httpclient,String username,String password)
	{
		
		try{
			
			HttpPost httppost = new HttpPost(login_url);
		
			List<NameValuePair> paare = new ArrayList<NameValuePair>(2);
			paare.add(new BasicNameValuePair("username",username));
			paare.add(new BasicNameValuePair("password",password));
			paare.add(new BasicNameValuePair("return","L2luZGV4LnBocC9pbnRlcm4v"));
			paare.add(new BasicNameValuePair(cookie,"1"));
			httppost.setEntity(new UrlEncodedFormEntity(paare));
			
			HttpResponse response = httpclient.execute(httppost);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				System.out.println("Erfolgreicher Loginseiten Abruf2");
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
			
			
		}
		catch(Exception e)
		{
			System.out.println("Fehlgeschlagener Loginseiten Abruf2:");
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	
	public void auslesen(HttpClient client)
	{
		try{
			HttpResponse response = client.execute(new HttpGet(plan_url));
			
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				System.out.println("Erfolgreicher Plan Abruf");
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
			System.out.println("Fehlgeschlagener Plan Abruf");
			System.out.println(e.getMessage());
		}

	}
	
	
	public ArrayList<Vertretung> auswerten(File file)
	{
		try{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			
			doc.getDocumentElement().normalize();
			
			NodeList font = doc.getElementsByTagName("font");
			System.out.println(font.getLength()+" Font-Elemente");
			
			ArrayList<Vertretung> vertretungen=new ArrayList<Vertretung>();
			for(int j=0;j<font.getLength();j+=3){
				String tag=font.item(j).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				System.out.println("Tag: "+tag);
				NodeList tr=font.item(j).getChildNodes().item(2).getChildNodes();
				//TODO 
				System.out.println(tr.getLength()+" tr-Elemente");
				for(int i=0;i<tr.getLength();i++)
				{
					Node node = tr.item(i);
					System.out.println(node.getNodeValue()+"---"+node.getNodeName());
					NamedNodeMap attr = node.getAttributes();
					System.out.println(attr.getLength());
					if(attr.getLength()>0)
					{
						Node attrclass= attr.getNamedItem("class");
						if(attrclass!=null)
						{
							String value=attrclass.getNodeValue();
							System.out.println(value);
							if(value.indexOf("list odd")!=-1||value.indexOf("list even")!=-1)
							{
								NodeList childnodes = node.getChildNodes();
								String klasse= childnodes.item(0).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
								String stunde = childnodes.item(1).getChildNodes().item(0).getNodeValue();
								String art = childnodes.item(2).getChildNodes().item(0).getNodeValue();
								String fach = childnodes.item(3).getChildNodes().item(0).getNodeValue();
								String raum = childnodes.item(4).getChildNodes().item(0).getNodeValue();
								vertretungen.add(new Vertretung(klasse,stunde,art,fach,raum,tag));
								
							}
						}
					}
				
				}
			}
			return vertretungen;
			
			
			
		}
		catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        t.printStackTrace ();
        }
		
		return null;
		
	}
	
	
	public void anzeigen(ArrayList<Vertretung> vertretungen,String klasse)
	{
		if(vertretungen!=null&&vertretungen.size()>0)
		{
			boolean gefunden=false;
			String ergebnis="<html><body><table border=\"1\"><tr><th><font size=\"-1\">Klasse</font></th>  <th><font size=\"-1\">Stunde</font></th>  <th><font size=\"-1\">Art</font></th>  <th><font size=\"-1\">Fach</font></th>  <th><font size=\"-1\">Raum</font></th></tr>\n";
			for(int i=0;i<vertretungen.size();i++){
				
				Vertretung v=vertretungen.get(i);
				System.out.println("Gesuchte Klasse: "+klasse+" Gefundene Klasse: "+v.klasse+"|");
				if(v.klasse.trim().equals(klasse.trim())||v.klasse.trim().equals("("+klasse.trim()+")")){
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
			ergebnis+="</table></body></html>";
			if(!gefunden)
			{
				ergebnis="Keine Vertretungen";
			}

			try{
				webview.loadData(ergebnis,"text/html","utf-8");
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
			System.out.println(ergebnis);
		
		}
		else{
			System.out.println("keine Vertretungen gefunden");
		}
	}
	
	public void save(String s,String file) throws IOException
	{
        FileWriter o=new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/vertretungsplan/"+file,false);
        BufferedWriter bw=new BufferedWriter(o);
        bw.write(s);
        bw.close();
        o.close();
		
	}
	

}
