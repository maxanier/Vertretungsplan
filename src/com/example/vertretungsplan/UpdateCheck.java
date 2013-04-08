package com.example.vertretungsplan;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateCheck extends AsyncTask<Double,Void,Boolean> {
	private final String updateUrl="http://dl.dropbox.com/u/10820678/Vertretungsplan/version";
	public final static String TAG="UpdateCheck";
	public Activity main=null;
	
	public UpdateCheck(Activity a){
		main=a;
	}
	@Override
	protected Boolean doInBackground(Double... version){
		Log.i(TAG,"Update Check");
		if(version.length==1){
			Double aktuelleVersion=version[0];
			try{
				DefaultHttpClient client=new DefaultHttpClient();
				HttpGet httpget = new HttpGet(updateUrl);
				HttpResponse response= client.execute(httpget);
				InputStream content = response.getEntity().getContent();
				
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String ergebnis="";
				if((ergebnis = buffer.readLine()) != null) {
					Log.i(TAG,"Aktuellste Version: "+ergebnis);
		            if(aktuelleVersion<Double.parseDouble(ergebnis))
		            {
		            	return true;
		            }
		          }
				else{
					throw new Exception("Datei leer");
				}
				
			}
			catch(Exception e){
				Log.e(TAG,"Fehler bei Versionskontrolle: ",e);
			}
			
			
			return false;	
		}
		else{
			return false;
		}
		
		
	}
	@Override
	protected void onPostExecute(Boolean result){
		if(result){
			updateHinweis();
		}
		
	}
	public void updateHinweis(){
		AlertDialog.Builder builder = new AlertDialog.Builder(main);
		builder.setMessage("Eine neue Version ist verfügbar").setTitle("Update");
		builder.setPositiveButton("Herunterladen",new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maxgb.de/vertretungsplan"));
				main.startActivity(browserIntent);
				
			}
		});
		builder.setNegativeButton("Abbrechen",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		});
		AlertDialog dialog=builder.create();
		dialog.show();
	}

}
