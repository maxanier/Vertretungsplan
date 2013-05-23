package de.maxgb.vertretungsplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class Options extends Activity {
	public static final String PREFS_NAME = "Einstellungen";
	public static final String TAG = "Options_Activity";
	private String username = "";
	private String password = "";
	private String klasse = "";
	private boolean direkt = false;
	private TextView klasse_eingabe;
	private TextView password_eingabe;
	private TextView username_eingabe;
	private CheckBox direkt_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_options_land);
			Log.i(TAG, "Landscape");
		} else {
			setContentView(R.layout.activity_options);
			Log.i(TAG, "Portrait");
		}
		username_eingabe = (TextView) findViewById(R.id.edit_username);
		password_eingabe = (TextView) findViewById(R.id.edit_password);
		klasse_eingabe = (TextView) findViewById(R.id.edit_klasse);
		direkt_box = (CheckBox) findViewById(R.id.direkt_box);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		username = settings.getString("username", "");
		password = settings.getString("password", "");
		klasse = settings.getString("klasse", "");
		direkt = settings.getBoolean("direkt", false);

		username_eingabe.setText(username);
		password_eingabe.setText(password);
		klasse_eingabe.setText(klasse);
		direkt_box.setChecked(direkt);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}

	public void speichern(View v) {
		System.out.println("Speichervorgang");
		String temp_username = username_eingabe.getText().toString().trim()
				.toLowerCase();
		String temp_password = password_eingabe.getText().toString().trim();
		String temp_klasse = klasse_eingabe.getText().toString().trim();
		if (temp_klasse.toUpperCase().equals("ALL")) {
			temp_klasse = "ALL";
		}
		if (temp_klasse.toUpperCase().equals("UI")) {
			temp_klasse = "UI";
		}
		if (temp_klasse.toUpperCase().equals("II")) {
			temp_klasse = "II";
		}
		if (temp_klasse.toUpperCase().equals("OI")) {
			temp_klasse = "OI";
		}
		if (temp_klasse.toUpperCase().equals("OIIIA")) {
			temp_klasse = "OIIIa";
		}
		if (temp_klasse.toUpperCase().equals("OIIIB")) {
			temp_klasse = "OIIIb";
		}
		if (temp_klasse.toUpperCase().equals("OIIIC")) {
			temp_klasse = "OIIIc";
		}
		if (temp_klasse.toUpperCase().equals("OIIID")) {
			temp_klasse = "OIIId";
		}

		if (!temp_username.trim().isEmpty() && !temp_password.trim().isEmpty()
				&& !temp_klasse.trim().isEmpty()) {
			System.out.println("speichern");
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("username", temp_username);
			editor.putString("password", temp_password);
			editor.putString("klasse", temp_klasse);
			editor.putBoolean("direkt", direkt_box.isChecked());
			editor.commit();
			Log.i(TAG, "Einstellungen gespeichert");
			finish();
		} else {
			Log.i(TAG, "Einstellungen nicht ausreichend ausgefüllt");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Bitte alle Felder ausfüllen")
					.setTitle("Fehler");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
						}
					});

			AlertDialog dialog = builder.create();
			dialog.show();

		}
	}

	public void abbrechen(View v) {
		System.out.println("Abbrechen");
		finish();
	}

}
