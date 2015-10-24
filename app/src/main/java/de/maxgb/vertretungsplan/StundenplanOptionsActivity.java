package de.maxgb.vertretungsplan;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.manager.StundenplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.InfoBox;

/**
 * Options Activity zum Konfigurieren der Stundenplanfunktion
 * 
 * @author Max Becker
 * 
 */
public class StundenplanOptionsActivity extends AppCompatActivity {

	private class DownloadPlanTask extends AsyncTask<Integer, Void, String> {
		/**
		 * Downloads stundenplan
		 * 
		 * @param params
		 *            the stundenplan id
		 * @return Error message, null if no error occured
		 */
		@Override
		protected String doInBackground(Integer... params) {
			int id = params[0];
			Logger.i(TAG, "Anfrage gestartet");

			OkHttpClient httpclient = new OkHttpClient(); // neuer Httpclient mit definierter Timeout Zeit
			httpclient.setConnectTimeout(Constants.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
			httpclient.setReadTimeout(Constants.CONNECTION_TIMEOUT,TimeUnit.MILLISECONDS);

			try {
				Request request =new Request.Builder().url(Constants.SP_GET_PLAN_URL + id).build();
				Response response = httpclient.newCall(request).execute();

				if (response.isSuccessful()) {
					Logger.i(TAG, "Stundenplan Anfrage erfolgreich");

					String responseString=response.body().string(); // Schreiben der Loginseite
														// in den
														// ByteArrayOutputStream

					if (responseString.contains("ERROR")) {
						return responseString;

					}
					JSONObject json = new JSONObject(responseString);
					save(json.toString(), Constants.SP_FILE_NAME);

					return null;

				} else {
					Logger.w(TAG, "Fehler beim Herunterladen Http Status: " + response.message());
					return ("Fehler beim Herunterladen Http StatusCode: " + response.code()
							+ " Url:" + Constants.SP_GET_PLAN_URL + id);

				}
			}catch (IOException e) {
				Logger.e(TAG, "Fehler beim Plan herunterladen", e);
			} catch (JSONException e) {
				Logger.e(TAG, "Fehler beim Plan herunterladen. Konnte Plan nicht Parsen", e);
				return ("Fehler beim Herunterladen. Konnte Plan nicht parsen");

			}

			return "Fehler beim Herunterladen";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				alert(result);
			} else {
				Toast.makeText(getApplicationContext(), "Erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
				StundenplanManager.getInstance().auswertenWithNotify();
			}
			progressDialog.dismiss();
			progressDialog = null;

		}

		@Override
		protected void onPreExecute() {
			if (!isOnline()) {
				alert("Keine Internetverbindung");
			}
			Log.i(TAG, "Show ProgressDialog");
			progressDialog = new ProgressDialog(StundenplanOptionsActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle("Saving");
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}
	}

	private final String TAG = "Stundenplan_options";
	private ProgressDialog progressDialog;
	private EditText edit_id;
	private CheckBox checkBox_kurse_with_namen;

	private boolean old_kurse_mit_namen;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stundenplan_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void save(String s, String file) throws IOException {
		Logger.i(TAG, "Speichern der Datei: " + file + " gestartet");
		FileWriter o = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/vertretungsplan/" + file,
				false);
		BufferedWriter bw = new BufferedWriter(o);
		bw.write(s);
		bw.close();
		o.close();
		Logger.i(TAG, "Speichern der Datei: " + file + " abgeschlo�en");

	}

	public void sp_herunterladen(View v) {
		DownloadPlanTask task = new DownloadPlanTask();
		try {
			task.execute(Integer.parseInt(edit_id.getText().toString()));
		} catch (NumberFormatException e) {
			alert("Bitte eine ID(nur aus Zahlen bestehend) eingeben");
		}
	}

	// ------------------------------------------------------------------------
	private void alert(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setPositiveButton("Ok", null);
		builder.create().show();
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		return false;
	}

	private void speichern() {
		Logger.i(TAG, "Speichere Stundenplanoptions");
		SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS_NAME, 0).edit();
		editor.putBoolean(Constants.SP_KURSE_MIT_NAMEN_KEY, checkBox_kurse_with_namen.isChecked());
		try {
			editor.putInt(Constants.SP_ID_KEY, Integer.parseInt(edit_id.getText().toString()));
		} catch (NumberFormatException e) {
			Logger.w(TAG, "Id Feld fehlerhaft oder leer");
		}
		editor.putBoolean(Constants.SP_KURSE_MIT_NAMEN_KEY, checkBox_kurse_with_namen.isChecked());
		editor.commit();
		if (old_kurse_mit_namen != checkBox_kurse_with_namen.isChecked()) {
			StundenplanManager.getInstance().notifyListener();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// Load Layout
		setContentView(R.layout.activity_stundenplan_options);
		SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, 0);
		edit_id = (EditText) findViewById(R.id.edit_stundenplan_id);
		checkBox_kurse_with_namen = (CheckBox) findViewById(R.id.checkBox_stundenplan_with_name);
		edit_id.setText(Integer.toString(pref.getInt(Constants.SP_ID_KEY, 0)));
		checkBox_kurse_with_namen.setChecked(pref.getBoolean(Constants.SP_KURSE_MIT_NAMEN_KEY, false));
		old_kurse_mit_namen = pref.getBoolean(Constants.SP_KURSE_MIT_NAMEN_KEY, false);
		/*if (!pref.getBoolean(Constants.OBERSTUFE_KEY, false)) {
			checkBox_kurse_with_namen.setVisibility(View.INVISIBLE);
			checkBox_kurse_with_namen.setEnabled(false);

		}*/

		InfoBox.showAnleitungBox(this, InfoBox.Anleitungen.STUNDENPLAN_INFO);
	}

	@Override
	protected void onPause() {
		super.onPause();
		speichern();
	}
}
