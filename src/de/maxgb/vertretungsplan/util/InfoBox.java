package de.maxgb.vertretungsplan.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.webkit.WebView;

/**
 * Infobox für Infotexte und Anleitungen
 * 
 * @author Max Becker
 * 
 */
public class InfoBox {
	/**
	 * Enum zum erstellen von Anleitungen.
	 * 
	 * @author Max
	 * 
	 */
	public enum Anleitungen {
		KURSWAHL(
				"Kurswahl",
				14,
				"<html><body>Füge hier deine Kurse in Form ihrer Abkürzung, wie sie auch auf dem Vertretungsplan erscheinen hinzu (z.B. 'PH1').<br>Durch langes Drücken auf einen Kurs entfernst du ihn</body></html>"), OPTIONSLEHRER(
				"Options_Lehrer",
				14,
				"<html><body>Geben Sie hier Nutzernamen und Passwort aus der Schule und ihr Kürzel wie es auf dem Vertretungsplan erscheint ein.<p>Unter 'Anzeige Tabs festlegen' können Sie entscheiden welche Tabs angezeigt werden sollen. Unterschiedliche Tabs filtern die Vertretungen jeweils anders.</body></html>"), OPTIONSSCHUELEROHNEKURSE(
				"Options_Schueler",
				14,
				"<html><body>Gebe hier Nutzername und Passwort aus der Schule und deine Stufe/Klasse (z.B. OI oder VIa) ein.<p>Unter 'Anzeige Tabs festlegen' kannst du entscheiden welche Tabs angezeigt werden sollen. Unterschiedliche Tabs filtern die Vertretungen  anders.</body></html>"), OPTIONSSCHUELERMITKURSE(
				"Options_Schueler",
				14,
				"<html><body>Gebe hier Nutzername und Passwort aus der Schule und deine Stufe/Klasse (z.B. OI oder VIa) ein.<p>Unter 'Kurswahl' kannst du deine eigenen Kurse einstellen, damit nur Vertretungen für deine eigenen Kurse angezeigt werden.<br>Unter 'Anzeige Tabs festlegen' kannst du entscheiden welche Tabs angezeigt werden sollen. Unterschiedliche Tabs filtern die Vertretungen anders.</body></html>"), ANZEIGEINFO(
				"Anzeige",
				14,
				"<html><body>Je nach Display Größe werden Bemerkungen und/oder Klasurmarkierungen in einem gesonderten Fenster angezeigt. Wenn dies der Fall ist, wird hinter der Vertretung ein X angezeigt. Zum Anzeigen der Informationen, einfach auf die Vertretung klicken.<p>Sollten die angezeigten Vertretungen auf ihrem Gerät zu klein oder nicht mehr lesbar sein, dann sagen sie mir bitte Bescheid und teilen mir die Bildschirmgröße und Auflösung mit (app@maxgb.de).<br>Danke</body></html>",
				3), FEATURE_USED_INFO(
				"Anzeige_Features",
				24,
				"<html><body>Schon die neuen Features genutzt:<br>Die angezeigten Tabs lassen sich jetzt beliebig auswählen und anordnen. Habe zum Beispiel deinen Stundenplan mit integrierter Vertretung vorne und die Vertretungen für alle ganz hinten<p>Oder den Stundenplan genutzt(Schüler exklusiv)? Es sind zwei Varianten verfügbar: Mit Vertretungen und ohne Vertretungen</body></html>",
				6);

		public String activity_name;
		public int letzte_aenderung;
		public String text;
		public int verspaetung = 0;

		/**
		 * Anleitung
		 * 
		 * @param name
		 *            Name zum Speichern der Aufrufe, am besten den namen der zugehörigen Activity, darf aber nicht mehrfach der gleiche sein
		 * @param letzte_aenderung
		 *            Letzte Aktualisierung der Activity
		 * @param text
		 *            Anleitungstext
		 */
		Anleitungen(String name, int letzte_aenderung, String text) {
			this.activity_name = name;
			this.letzte_aenderung = letzte_aenderung;
			this.text = text;
		}

		/**
		 * Anleitung
		 * 
		 * @param name
		 *            Name zum Speichern der Aufrufe, am besten den namen der zugehörigen Activity, darf aber nicht mehrfach der gleiche sein
		 * @param letzte_aenderung
		 *            Letzte Aktualisierung der Activity
		 * @param text
		 *            Anleitungstext
		 * @param verspaetung
		 *            Anzahl der aufrufe nachdenen die Anleitung gezeigt werden soll
		 */
		Anleitungen(String name, int letzte_aenderung, String text, int verspaetung) {
			this.activity_name = name;
			this.letzte_aenderung = letzte_aenderung;
			this.text = text;
			this.verspaetung = Math.abs(verspaetung);
		}

	}

	// Name der SharedPreferences in denen die Infobox sachen gespeichert werden
	private static final String PREFS_NAME = "InfoBox";
	private static final String TITLE = "Erklärung";

	/**
	 * Zeigt vordefinierte Anleitungen einmalig und optional mit Verzögerung an
	 * 
	 * @param context
	 *            Context in dem der Dialog angezeigt werden soll
	 * @param anleitung
	 *            Zu zeigende Anleitung
	 */
	public static void showAnleitungBox(Context context, Anleitungen anleitung) {

		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		// Falls die Anleitung in einer neueren Version vorhanden ist, als zuletzt angezeigt wurde (bzw. noch garnicht)
		if (settings.getInt(anleitung.activity_name, 0) < anleitung.letzte_aenderung) {
			// Falls eine Verspätung aktiviert ist erst diese abwarten
			if (anleitung.verspaetung != 0) {
				int i = settings.getInt(anleitung.activity_name + "_times_ignored", 0);
				if (i < anleitung.verspaetung) {
					settings.edit().putInt(anleitung.activity_name + "_times_ignored", i + 1).commit();
					return;
				}
			}

			// Anzeige Dialog bauen
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(TITLE);
			builder.setPositiveButton("Ok", new OnClickListener() {
				@Override
				public void onClick(DialogInterface i, int a) {
				}

			});
			WebView v = new WebView(context);
			v.loadData(anleitung.text, "text/html; charset=UTF-8", null);
			builder.setView(v);

			AlertDialog dialog = builder.create();
			dialog.show();

			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(anleitung.activity_name, anleitung.letzte_aenderung);
			editor.commit();

		}
	}

	/**
	 * Zeigt eine einfache (HTML)InfoBox mit OK Button
	 * 
	 * @param context
	 *            Context in dem der Dialog angezeigt werden soll
	 * @param title
	 *            Box Titel
	 * @param text
	 *            Box Text im HTML Format
	 */
	public static void showInfoBox(Context context, String title, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface i, int a) {
			}

		});
		WebView v = new WebView(context);
		v.loadData(text, "text/html; charset=UTF-8", null);
		builder.setView(v);

		AlertDialog dialog = builder.create();
		dialog.show();
	}

}