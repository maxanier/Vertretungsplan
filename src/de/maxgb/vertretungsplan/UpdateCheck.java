package de.maxgb.vertretungsplan;

/* Überflüßig wegen PlayStore Updates
 package de.maxgb.vertretungsplan;

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
 import android.webkit.WebView;

 public class UpdateCheck extends AsyncTask<Double, Void, Boolean> {
 private final String updateUrl = "http://dl.dropbox.com/u/10820678/Vertretungsplan/version";
 public final static String TAG = "UpdateCheck";
 public Activity main = null;
 private String changelog = "";

 public UpdateCheck(Activity a) {
 main = a;
 }

 @Override
 protected Boolean doInBackground(Double... version) {
 Log.i(TAG, "Update Check");
 if (version.length == 1) {
 Double aktuelleVersion = version[0];
 try {
 DefaultHttpClient client = new DefaultHttpClient();
 HttpGet httpget = new HttpGet(updateUrl);
 HttpResponse response = client.execute(httpget);
 InputStream content = response.getEntity().getContent();

 BufferedReader buffer = new BufferedReader(
 new InputStreamReader(content));
 String ergebnis = "";
 if ((ergebnis = buffer.readLine()) != null) {
 Log.i(TAG, "Aktuellste Version: " + ergebnis);
 if (aktuelleVersion < Double.parseDouble(ergebnis)) {
 changelog = "<html><body><b>Neue Version</b><p>Changelog:";
 while ((ergebnis = buffer.readLine()) != null) {
 changelog += "<br>" + ergebnis;
 Log.i(TAG, "Neue Zeile");
 }
 return true;
 }
 } else
 throw new Exception("Datei leer");

 } catch (Exception e) {
 Log.e(TAG, "Fehler bei Versionskontrolle: ", e);
 }

 return false;
 } else
 return false;

 }

 @Override
 protected void onPostExecute(Boolean result) {
 if (result) {
 updateHinweis();
 }

 }


 //Zeigt Update Hinweis mit Changelog an
 public void updateHinweis() {
 AlertDialog.Builder builder = new AlertDialog.Builder(main);

 WebView view = new WebView(main.getApplicationContext());
 view.loadData(changelog, "text/html; charset=UTF-8", null);
 builder.setView(view);
 builder.setTitle("Update");
 // Erstellt zwei Optionsbuttons
 builder.setPositiveButton("Herunterladen",
 new DialogInterface.OnClickListener() {

 @Override
 public void onClick(DialogInterface dialog, int which) {
 Intent browserIntent = new Intent(Intent.ACTION_VIEW,
 Uri.parse("http://maxgb.de/vertretungsplan"));
 main.startActivity(browserIntent);

 }
 });
 builder.setNegativeButton("Abbrechen",
 new DialogInterface.OnClickListener() {

 @Override
 public void onClick(DialogInterface dialog, int which) {

 }
 });
 // ------------------------------------
 AlertDialog dialog = builder.create();
 dialog.show();
 }

 }
 */
