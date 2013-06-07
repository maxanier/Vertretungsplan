package de.maxgb.vertretungsplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class Options extends Activity {
	public static final String PREFS_NAME = "Einstellungen";
	public static final String TAG = "Options_Activity";
	public static final float LETZE_AENDERUNG = (float) 1.51;
	public static String ANLEITUNG = "Gebe hier deinen Benutzernamen und dein Passwort aus der Schule ein.";

	private String username = "";
	private String password = "";
	private String klasse = "";
	private boolean direkt = false;
	private boolean kurse_anzeigen = false;
	private TextView klasse_eingabe;
	private TextView password_eingabe;
	private TextView username_eingabe;
	private CheckBox direkt_box;
	private CheckBox box_kurse;
	private Button button_kurse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Kontrolle zur Zeit nicht benötigt
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_options);
			Log.i(TAG, "Landscape");
		} else {
			setContentView(R.layout.activity_options);
			Log.i(TAG, "Portrait");
		}
		username_eingabe = (TextView) findViewById(R.id.edit_username);
		password_eingabe = (TextView) findViewById(R.id.edit_password);
		klasse_eingabe = (TextView) findViewById(R.id.edit_klasse);
		direkt_box = (CheckBox) findViewById(R.id.box_direkt);
		box_kurse = (CheckBox) findViewById(R.id.box_kurse);
		button_kurse = (Button) findViewById(R.id.button_kurse);

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		username = settings.getString("username", "");
		password = settings.getString("password", "");
		klasse = settings.getString("klasse", "");
		direkt = settings.getBoolean("direkt", false);
		kurse_anzeigen = settings.getBoolean("kurse_anzeigen", false);

		username_eingabe.setText(username);
		password_eingabe.setText(password);
		klasse_eingabe.setText(klasse);
		direkt_box.setChecked(direkt);
		box_kurse.setChecked(kurse_anzeigen);

		if (!(android.os.Build.VERSION.SDK_INT >= 11)) {
			box_kurse.setVisibility(4);

		} else {
			ANLEITUNG += "<br>'Nur eigene Kurse anzeigen' sorgt dafür, dass nur die von dir gewählten Kurse auf dem Vertretungsplan erscheinen (Nur für die Oberstufe nötig).";
		}
		onBoxKurseClick(box_kurse);

		new InfoBox(this, TAG, LETZE_AENDERUNG, "Anleitung", ANLEITUNG);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        Intent upIntent = NavUtils.getParentActivityIntent(this);
	        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	            // This activity is NOT part of this app's task, so create a new task
	            // when navigating up, with a synthesized back stack.
	            TaskStackBuilder.create(this)
	                    // Add all of this activity's parents to the back stack
	                    .addNextIntentWithParentStack(upIntent)
	                    // Navigate up to the closest parent
	                    .startActivities();
	        } else {
	            // This activity is part of this app's task, so simply
	            // navigate up to the logical parent activity.
	            NavUtils.navigateUpTo(this, upIntent);
	        }
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void onBoxKurseClick(View v) {
		if (((CheckBox) v).isChecked()) {
			button_kurse.setVisibility(0);
		} else {
			button_kurse.setVisibility(4);
		}

	}

	public void kurswahlAnzeigen(View v) {
		Intent i = new Intent(this, Kurswahl.class);
		startActivity(i);
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
			editor.putBoolean("kurse_anzeigen", box_kurse.isChecked());
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
