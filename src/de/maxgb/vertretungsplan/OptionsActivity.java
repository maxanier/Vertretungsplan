package de.maxgb.vertretungsplan;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.manager.TabManager;
import de.maxgb.vertretungsplan.manager.TabManager.TabSelector;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.InfoBox;

/**
 * Optionsmenu Dient zur Eingabe von Nutzername,Passwort, Stufe, Kursen und Typänderungen
 * 
 * @author Max Becker
 * 
 */
public class OptionsActivity extends SherlockFragmentActivity {

	/**
	 * Select Type Dialog, allows the user to choose between Schueler,Lehrer ans Oberstufenschueler
	 * 
	 * @author Max Becker
	 * 
	 */
	@SuppressLint("ValidFragment")
	private class SelectTypeDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.select_type).setItems(R.array.type_array, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, 0);
					de.maxgb.android.util.Logger.i(TAG, "Selected " + which);
					switch (which) {
					case 0:
						prefs.edit().putBoolean(Constants.SCHUELER_KEY, true).putBoolean(Constants.LEHRER_KEY, false)
								.putBoolean(Constants.OBERSTUFE_KEY, false).commit();
						break;
					case 1:
						prefs.edit().putBoolean(Constants.SCHUELER_KEY, true).putBoolean(Constants.LEHRER_KEY, false)
								.putBoolean(Constants.OBERSTUFE_KEY, true).commit();
						break;
					case 2:
						de.maxgb.android.util.Logger.i(
								TAG,
								"Lehrer selected: "
										+ prefs.edit().putBoolean(Constants.LEHRER_KEY, true)
												.putBoolean(Constants.SCHUELER_KEY, false).commit());
						break;
					default:
						prefs.edit().putBoolean(Constants.SCHUELER_KEY, true).putBoolean(Constants.LEHRER_KEY, false)
								.commit();
						break;
					}
					prefs.edit()
							.putString(
									Constants.JSON_TABS_KEY,
									TabManager.convertToString(SelectTabsActivity.createStandardSelection(
											new ArrayList<TabSelector>(), prefs))).commit();// Creates a Standard TabSelection to
																							// make a Json string of it, which is
																							// saved as the tabselection
																							// json_string to reset the
																							// tabselection
					requestVertretungsplanUpdate();
					requestTabUpdate();
					showLayout();
				}
			});
			return builder.create();

		}

	}

	private final String TAG = "Options";
	private TextView password_eingabe;
	private TextView username_eingabe;
	private CheckBox checkbox_debug;
	private boolean type_schueler;
	private boolean type_oberstufe;
	private TextView stufe_eingabe;
	private TextView kuerzel_eingabe;

	private Button button_kurse;

	private String oldPassword;

	private String oldUsername;
	private boolean update_vertretungsplan;
	private boolean update_tabs;
	public final static int UPDATE_NOTHING = -1;
	public final static int UPDATE_ALL = 0;
	public final static int UPDATE_VP = 1;

	public final static int UPDATE_TABS = 2;

	public void abbrechen(View v) {
		de.maxgb.android.util.Logger.i(TAG, "Abbrechen");
		finish();
	}

	public void changeType(View v) {
		de.maxgb.android.util.Logger.i(TAG, "Created Type Dialog");
		DialogFragment type_fragment = new SelectTypeDialogFragment();
		type_fragment.show(getSupportFragmentManager(), "select_type");
		de.maxgb.android.util.Logger.i(TAG, "Finished Type Dialog");

	}

	public void chooseTabs(View v) {

		Intent i = new Intent(this, SelectTabsActivity.class);
		startActivity(i);
		requestTabUpdate();
	}

	@Override
	public void finish() {
		Logger.i(TAG, "Finishing");
		if (update_vertretungsplan && update_tabs) {
			setResult(UPDATE_ALL);
		} else if (update_vertretungsplan) {
			setResult(UPDATE_VP);
		} else if (update_tabs) {
			setResult(UPDATE_TABS);
		} else {
			setResult(UPDATE_NOTHING);
		}
		super.finish();
	}

	public void kurswahlAnzeigen(View v) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			Intent i = new Intent(this, KurswahlActivity.class);
			startActivity(i);
			update_tabs = true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.options, menu);
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

	public void sendLog(View v) {
		PackageInfo pInfo;
		String version = "X";
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// Send the email
		Intent mailIntent = new Intent(Intent.ACTION_SEND);
		mailIntent.setType("text/Message");
		mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { Constants.LOG_REPORT_EMAIL });
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, Constants.LOG_REPORT_BETREFF + version);
		mailIntent.putExtra(Intent.EXTRA_TEXT, "");
		ArrayList<Uri> uris=new ArrayList<Uri>();
		uris.add(Uri.fromFile(Logger.getLogFile()));
	
		uris.add(Uri.fromFile(Logger.getOldLogFile()));
		mailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		// Send, if possible
		try {
			startActivity(Intent.createChooser(mailIntent, "Send logs ..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}

	public void showLayout() {
		SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, 0);

		if (prefs.getBoolean(Constants.LEHRER_KEY, false)) {
			de.maxgb.android.util.Logger.i(TAG, "Loading Lehrer Layout");
			setContentView(R.layout.activity_options_lehrer);
			type_schueler = false;
			kuerzel_eingabe = (TextView) findViewById(R.id.edit_kuerzel);
			kuerzel_eingabe.setText(prefs.getString(Constants.LEHRER_KUERZEL_KEY, ""));
			InfoBox.showAnleitungBox(this, InfoBox.Anleitungen.OPTIONSLEHRER);
		} else {
			de.maxgb.android.util.Logger.i(TAG, "Loading Schueler LAyout");
			setContentView(R.layout.activity_options_schueler);
			type_schueler = true;
			type_oberstufe = prefs.getBoolean(Constants.OBERSTUFE_KEY, false);
			button_kurse = (Button) findViewById(R.id.button_kurse);
			stufe_eingabe = (TextView) findViewById(R.id.edit_stufe);
			stufe_eingabe.setText(prefs.getString(Constants.STUFE_KEY, ""));

			if (!(android.os.Build.VERSION.SDK_INT >= 11) || !type_oberstufe) {
				button_kurse.setVisibility(4);
				InfoBox.showAnleitungBox(this, InfoBox.Anleitungen.OPTIONSSCHUELEROHNEKURSE);
			} else {
				button_kurse.setVisibility(0);
				InfoBox.showAnleitungBox(this, InfoBox.Anleitungen.OPTIONSSCHUELERMITKURSE);
			}
		}
		// Variablen für einzelne Elemente befüllen
		username_eingabe = (TextView) findViewById(R.id.edit_username);
		password_eingabe = (TextView) findViewById(R.id.edit_password);
		checkbox_debug = (CheckBox) findViewById(R.id.checkBox_debug);

		// Einstellungen laden
		oldUsername = prefs.getString(Constants.USERNAME_KEY, "");
		oldPassword = prefs.getString(Constants.PASSWORD_KEY, "");
		checkbox_debug.setChecked(prefs.getBoolean(Constants.DEBUG_KEY, false));
		username_eingabe.setText(oldUsername);
		password_eingabe.setText(oldPassword);

		de.maxgb.android.util.Logger.i(TAG, "LAyout loaded");
	}

	public void stundenplan(View c) {
		Intent i = new Intent(this, StundenplanOptionsActivity.class);
		startActivity(i);
	}

	//@formatter:off
	/**
	 * Alert Boc falls nicht alle Felder ausgefüllt sind
	 */
	/* Not used
	private void alertBox() {
		de.maxgb.android.util.Logger.i(TAG, "Einstellungen nicht ausreichend ausgefüllt");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Bitte alle Felder ausfüllen").setTitle("Fehler");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	*/
	//@formatter:on

	private void requestTabUpdate() {
		update_tabs = true;
		Logger.i(TAG, "Tab Update requestet");
	}

	private void requestVertretungsplanUpdate() {
		update_vertretungsplan = true;
		Logger.i(TAG, "Vertretungsplan Update requestet");
	}

	private void resetUpdateRequests() {
		update_vertretungsplan = false;
		update_tabs = false;
		Logger.i(TAG, "Update Request resetet");
	}

	/**
	 * Set up the {@link com.actionbarsherlock.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar() {

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void speichern() {
		Logger.i(TAG, "Speichervorgang");
		String temp_username = username_eingabe.getText().toString().trim().toLowerCase();
		String temp_password = password_eingabe.getText().toString().trim();
		// Schueler
		if (type_schueler) {
			String temp_klasse = stufe_eingabe.getText().toString().trim();

			if (temp_klasse.toUpperCase().equals("UI")) {
				temp_klasse = "UI";
			} else if (temp_klasse.toUpperCase().equals("II")) {
				temp_klasse = "II";
			} else if (temp_klasse.toUpperCase().equals("OI")) {
				temp_klasse = "OI";
			} else if (temp_klasse.toUpperCase().equals("OIIIA")) {
				temp_klasse = "OIIIa";
			} else if (temp_klasse.toUpperCase().equals("OIIIB")) {
				temp_klasse = "OIIIb";
			} else if (temp_klasse.toUpperCase().equals("OIIIC")) {
				temp_klasse = "OIIIc";
			} else if (temp_klasse.toUpperCase().equals("OIIID")) {
				temp_klasse = "OIIId";
			}

			Logger.i(TAG, "Speichern Schueler");
			SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			if (!temp_username.equals(oldUsername) || !temp_password.equals(oldPassword)) {
				editor.putString(Constants.USERNAME_KEY, temp_username);
				editor.putString(Constants.PASSWORD_KEY, temp_password);
				editor.putString(Constants.STUFE_KEY, temp_klasse);
				editor.putBoolean(Constants.DEBUG_KEY, checkbox_debug.isChecked());
				editor.commit();
				de.maxgb.android.util.Logger.i(TAG, "Einstellungen gespeichert");
				requestVertretungsplanUpdate();// Update anfordern
			} else {
				editor.putString(Constants.STUFE_KEY, temp_klasse);
				editor.putBoolean(Constants.DEBUG_KEY, checkbox_debug.isChecked());
				editor.commit();
				de.maxgb.android.util.Logger.i(TAG, "Einstellungen gespeichert. Nutzername und Passwort unverändert");

			}
		}
		// Lehrer
		else {
			String temp_kuerzel = kuerzel_eingabe.getText().toString().trim();
			Logger.i(TAG, "Speichern Lehrer");
			SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();

			if (!temp_username.equals(oldUsername) || !temp_password.equals(oldPassword)) {
				editor.putString(Constants.USERNAME_KEY, temp_username);
				editor.putString(Constants.PASSWORD_KEY, temp_password);
				editor.putString(Constants.LEHRER_KUERZEL_KEY, temp_kuerzel);
				editor.putBoolean(Constants.DEBUG_KEY, checkbox_debug.isChecked());
				editor.commit();
				de.maxgb.android.util.Logger.i(TAG, "Einstellungen gespeichert");
				requestVertretungsplanUpdate();// Update anfordern
			} else {
				editor.putString(Constants.LEHRER_KUERZEL_KEY, temp_kuerzel);
				editor.putBoolean(Constants.DEBUG_KEY, checkbox_debug.isChecked());
				editor.commit();
				de.maxgb.android.util.Logger.i(TAG, "Einstellungen gespeichert. Nutzername und Passwort unverändert");

			}
			Logger.setDebugMode(checkbox_debug.isChecked());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.init(Constants.PLAN_DIRECTORY);
		SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		Logger.setDebugMode(prefs.getBoolean(Constants.DEBUG_KEY, false));
		resetUpdateRequests();
		if (!prefs.getBoolean(Constants.SCHUELER_KEY, false) && !prefs.getBoolean(Constants.LEHRER_KEY, false)) {
			de.maxgb.android.util.Logger.i(TAG, "Created Type Dialog");
			DialogFragment type_fragment = new SelectTypeDialogFragment();
			type_fragment.show(getSupportFragmentManager(), "select_type");
			de.maxgb.android.util.Logger.i(TAG, "Finished Type Dialog");
		} else {
			showLayout();
		}

		// Show the Up button in the action bar.
		setupActionBar();
		de.maxgb.android.util.Logger.i(TAG, "Options created");
	}

	/**
	 * Pauses activity and saves data;
	 */
	protected void onPause() {
		super.onPause();
		speichern();
	}

}
