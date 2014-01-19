package de.maxgb.vertretungsplan.com;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import de.maxgb.vertretungsplan.AnzeigeActivity;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Logger;

/**
 * Async Download Task
 * Lädt den Vertretungsplan herunter
 * @author Max Becker
 *
 */
public class DownloadTask extends AsyncTask<Void, Void, Integer> {
	private SharedPreferences pref;
	private final String TAG = "DownloadTask";
	private AnzeigeActivity anzeige;
	private final int SUCCESS = 0;
	private final int MISSINGLOGININFO = 1;
	private final int NOCONNECTION = 2;
	private final int OTHEREXCEPTION = -1;

	public DownloadTask(SharedPreferences pref, AnzeigeActivity anzeige) {
		this.pref = pref;
		this.anzeige = anzeige;
	}

	/*
	 * Downloads the Plan in background
	 * 
	 * @return 0=Success,1=Missing login info,2=No Internetconnection,-1=Other
	 * Exception
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Integer doInBackground(Void... voids) {
		// Laden der Einstellungen
		String username = pref.getString(Constants.USERNAME_KEY, "");
		String password = pref.getString(Constants.PASSWORD_KEY, "");
		boolean schuelerplan = pref.getBoolean(Constants.SCHUELER_KEY, false);
		boolean lehrerplan = pref.getBoolean(Constants.LEHRER_KEY, false);

		// Überprüfen der Einstellungen
		if (username.equals("") || password.equals("")) {
			Logger.w(TAG, "Kein Nutzername oder Passwort eingestellt");
			return MISSINGLOGININFO;
		}
		// Überprüfen der Internetverbindung
		if (!isOnline()) {
			Logger.w(TAG, "No InternetConnection");
			return NOCONNECTION;
		}
		// Abrufen des Plans

		File dir = new File(Constants.PLAN_DIRECTORY);
		dir.mkdirs(); // Erstellen des Verzeichnises falls noch nicht vorhanden

		Logger.i(TAG, "Anfrage gestartet");
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				Constants.CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams,
				Constants.CONNECTION_TIMEOUT);
		MyHttpsClient httpsclient = new MyHttpsClient(
				anzeige.getApplicationContext(), httpParams); // neuer
																// HttpsClient
																// mit eigener
																// Timeoutzeit

		try {
			String cookie = getLoginCookie(httpsclient);
			login(httpsclient, cookie, username, password);
			planSpeichern(httpsclient, schuelerplan, lehrerplan);
			Logger.i(TAG, "Anfrage abgeschlossen");
			return SUCCESS;
		} catch (Exception e) {
			Logger.e(TAG, "Abrufen des Plans fehlgeschlagen", e);
			return OTHEREXCEPTION;
		}
	}

	protected String getLoginCookie(MyHttpsClient client)
			throws ClientProtocolException, IOException {

		Logger.i(TAG, "Abrufen der Loginseite gestartet");
		HttpResponse response = client.execute(new HttpGet(
				Constants.LOGIN_SEITE_URL)); // Abruf der Seite
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // Erfolg
																			// der
																			// Anfrage
			Logger.i(TAG, "Erfolgreicher Loginseiten Abruf");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out); // Schreiben der Loginseite
												// in den
												// ByteArrayOutputStream
			out.close();
			String responseString = out.toString(); // Umwandlung des
													// ByteArrayOutputStream(Loginseite)
													// in einen String

			if (Logger.getDebugMode()) {
				try {
					save(responseString, "debug_login.html");
				} catch (IOException e) {
					Logger.e(TAG, "Debug Login Speicherung fehlgeschlagen", e);
				}
			}// Speichern der Seite zum Debuggen

			int index = responseString.indexOf(Constants.COOKIE_SUCH_STRING);// Sucht
																				// mit
																				// dem
			// Suchstring nach
			// Anfang des
			// "Cookies"
			// System.out.println(index);
			char[] chars = responseString.toCharArray();
			String cookie = String.copyValueOf(chars, index
					+ Constants.COOKIE_SUCH_STRING.length() + 1, 32);// Auslesen
																		// des
			// "Cookies"(LÃ¤nge:
			// 32) und speichern
			// in der globalen
			// Variable cookie
			Logger.i(TAG, "Cookie ausgelesen. Wert: " + cookie);

			return cookie;

		} else {
			StatusLine statusLine = response.getStatusLine();
			response.getEntity().getContent().close();
			throw new IOException("Abfrage Fehlgeschlagen: "
					+ statusLine.getReasonPhrase());// Fehlermeldung
			// werfen
			// wenn
			// Abfrage
			// fehlgeschlagen
		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) anzeige
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		return false;
	}

	protected void login(MyHttpsClient client, String cookie, String username,
			String password) throws IOException, ClientProtocolException {
		Logger.i(TAG, "Loginvorgang gestartet");

		// Erstellen des Postrequest
		HttpPost httppost = new HttpPost(Constants.LOGIN_URL);

		List<NameValuePair> paare = new ArrayList<NameValuePair>(2); // Post-Parameter
		paare.add(new BasicNameValuePair("username", username));
		paare.add(new BasicNameValuePair("password", password));
		paare.add(new BasicNameValuePair("return", "L2luZGV4LnBocC9pbnRlcm4v")); // Unbekannte
																					// Funktion
		paare.add(new BasicNameValuePair(cookie, "1"));
		httppost.setEntity(new UrlEncodedFormEntity(paare));
		// -----------------------------------------------
		HttpResponse response = client.execute(httppost);// Login mit
															// Http-Post
		// Siehe abrufen()
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Logger.i(TAG,
					"Loginvorgang erfolgreich abgeschloï¿½en. Status aber unbekannt");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			response.getEntity().writeTo(out);
			out.close();
			if (Logger.getDebugMode()) {
				try {
					String responseString = out.toString();
					save(responseString, "debug_login.html");
				} catch (IOException e) {
					Logger.e(TAG, "Debug Login Speicherung fehlgeschlagen", e);
				}
			}// Speichern der Seite zum Debuggen
			return;
		} else {
			StatusLine statusLine = response.getStatusLine();
			response.getEntity().getContent().close();
			throw new IOException("Login fehlsgeschlagen "
					+ statusLine.getReasonPhrase());
		}

	}

	@Override
	protected void onPostExecute(Integer result) {

		switch (result) {
		case SUCCESS:
			SharedPreferences.Editor editor = pref.edit();
			editor.putLong(Constants.REFRESH_TIME_KEY,
					System.currentTimeMillis());
			editor.commit();
			anzeige.updateVertretungsplan();//TODO directly invoke update
			break;
		case MISSINGLOGININFO:
			Toast.makeText(anzeige.getApplicationContext(),
					"Kein Nutzername oder Passwort eingestellt",
					Toast.LENGTH_SHORT).show();
			anzeige.setRefreshActionButtonState(false);
			break;
		case NOCONNECTION:
			Toast.makeText(anzeige.getApplicationContext(),
					"Keine Internetverbindung", Toast.LENGTH_SHORT).show();
			anzeige.setRefreshActionButtonState(false);
			break;
		case OTHEREXCEPTION:
			Toast.makeText(anzeige.getApplicationContext(),
					"Fehler beim Aktualisieren", Toast.LENGTH_SHORT).show();
			anzeige.setRefreshActionButtonState(false);
			break;
		}

	}

	protected void planSpeichern(MyHttpsClient client, boolean schuelerplan,
			boolean lehrerplan) throws ClientProtocolException, IOException {
		Logger.i(TAG, "Abrufen...");
		if (schuelerplan) {
			Logger.i(TAG, "Abrufen des Schueler Plans gestartet");
			HttpResponse response = client.execute(new HttpGet(
					Constants.SCHUELER_PLAN_URL));// Planseite
			// abrufen
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Logger.i(TAG, "Abrufen des Plans erfolgreich abgeschloï¿½en");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();

				save(responseString, Constants.SCHUELER_PLAN_FILE_NAME);

			} else {
				StatusLine statusLine = response.getStatusLine();
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		}
		if (lehrerplan) {
			Logger.i(TAG, "Abrufen des Lehrer Plans gestartet");
			HttpResponse response = client.execute(new HttpGet(
					Constants.LEHRER_PLAN_URL));// Planseite
			// abrufen
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Logger.i(TAG, "Abrufen des Plans erfolgreich abgeschloï¿½en");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				String responseString = out.toString();

				save(responseString, Constants.LEHRER_PLAN_FILE_NAME);

			} else {
				StatusLine statusLine = response.getStatusLine();
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		}
	}

	private void save(String s, String file) throws IOException {
		Logger.i(TAG, "Speichern der Datei: " + file + " gestartet");
		FileWriter o = new FileWriter(Environment.getExternalStorageDirectory()
				.getPath() + "/vertretungsplan/" + file, false);
		BufferedWriter bw = new BufferedWriter(o);
		bw.write(s);
		bw.close();
		o.close();
		Logger.i(TAG, "Speichern der Datei: " + file + " abgeschloï¿½en");

	}
}