/**
 * 
 */
package de.maxgb.vertretungsplan.util;

import java.util.ArrayList;

import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.SelectTabsActivity;
import de.maxgb.vertretungsplan.manager.TabManager;
import de.maxgb.vertretungsplan.manager.TabManager.TabSelector;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author Max Updater Klasse, die zum Beispiel Einstellung oder Tabauswahl bei einem App-Update, fall nötig, updated. Stellt
 *         außerdem ein Changelog zur Verfügung
 */
public class Updater {
	private static final String TAG = "Updater";

	/**
	 * Führt fall ein App-Update geschehen ist, die benötigten updates durch und liefert ein Changelog zurück.
	 * 
	 * @param pref
	 *            SharedPreferences zum Bearbeiten und zum Speichern des letzten Updates
	 * @return Changelog, null wenn kein Changelog vorhanden
	 */
	public static String update(SharedPreferences pref, Context context) {
		boolean updated = false;
		String changelog = "<html><body>";
		int last = pref.getInt(Constants.LAST_UPDATED_KEY, 19);
		int version;

		try {
			version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Logger.e(TAG, "Holen der aktuellen Versionsnummer fehlgeschlagen", e);
			return null;
		}

		if (last <= 19) {
			ArrayList<TabSelector> tabs = SelectTabsActivity
					.createStandardSelection(new ArrayList<TabSelector>(), pref);

			SharedPreferences.Editor editor = pref.edit();
			editor.putString(Constants.JSON_TABS_KEY, TabManager.convertToString(tabs));// Save the current selection via the
																						// TabManger in SharedPreferences
			editor.commit();

			updated = true;
			changelog += "Version 2.1:<br><ul><li>Erneuertes Tabmanagment(Tabs jetzt beliebig sortierbar)</li><li>Stundenplan für Schüler (siehe Optionen)</li><li>Stundenplan mit integrierten Vertretungen</li><li>Kleine Verbesserungen</li></ul><p>";
		}
		if(last <=28){
			updated= true;
			changelog += "Version 2.1.1:<br><ul><li>Bugfix</li></ul><p>";
		}
		if(last <=29){
			updated=true;
			changelog += "Version 2.1.2:<br><ul><li>Weitere Namensersetzungen</li></ul><p>";
		}
		// Aktuelle Version als Last Updated speichern
		pref.edit().putInt(Constants.LAST_UPDATED_KEY, version).commit();

		if (updated) {
			Logger.i(TAG, "Updating stuff from Version: " + last + " to " + version);
			changelog += "</body></html>";
			return changelog;
		} else {
			Logger.i(TAG, "No updates neccessary (old: " + last + "; new: " + version + ")");
			return null;
		}

	}
}
