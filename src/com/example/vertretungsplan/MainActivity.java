package com.example.vertretungsplan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	public static final String TAG = "Main_Acticity";
	public static final String PREFS_NAME = "Einstellungen";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_main_land);
			Log.i(TAG, "Landscape");
		} else {
			setContentView(R.layout.activity_main);
			Log.i(TAG, "Portrait");
		}
		if (isOnline()) {
			UpdateCheck check = new UpdateCheck(this);
			try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(
						getPackageName(), 0);
				Double version = Double.parseDouble(pInfo.versionName);
				Log.i(TAG, "Version: " + version);
				check.execute(version);
			} catch (Exception e) {
			}
		}
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (settings.getBoolean("direkt", false)) {
			Log.i(TAG, "Direkt Plan anzeigen");
			Intent i = new Intent();
			i.setClass(this, Anzeige.class);
			startActivity(i);
		}

		// final Button plan_button = (Button) findViewById(R.id.button1);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Startet neue Plan-Anzeige
	 */

	public void plan(View v) {
		Log.i(TAG, "Plan anzeigen");
		Intent i = new Intent();
		i.setClass(this, Anzeige.class);
		startActivity(i);
	}

	/**
	 * Startet neue Optionen-Anzeige
	 */

	public void optionen(View v) {
		Log.i(TAG, "Optionen anzeigen");
		Intent i = new Intent();
		i.setClass(this, Options.class);
		startActivity(i);
	}

	/**
	 * Startet neue Credits-Anzeige
	 */
	public void credits(View v) {
		Log.i(TAG, "Credits anzeigen");
		Intent i = new Intent();
		i.setClass(this, Credits.class);
		startActivity(i);
	}

	/**
	 * Überprüft die Internetverbindung
	 * 
	 * @return Verbindungsstatus
	 */

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		return false;
	}

}
