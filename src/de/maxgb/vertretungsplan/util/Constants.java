package de.maxgb.vertretungsplan.util;

import java.util.HashMap;

import android.os.Environment;

/**
 * Konstanten
 * 
 * @author Max Becker
 * 
 */
public class Constants {
	public static final String PREFS_NAME = "Settings";
	//Versions
	public static final String LAST_UPDATED_KEY="last_updated";
	public static final boolean IS_BETA=true;//TODO Change for release
	
	// Keys----------------------------------------------------
	public static final String SCHUELER_KEY = "Schueler";
	public static final String LEHRER_KEY = "Lehrer";
	public static final String STUFE_TAB_KEY = "Stufe";
	public static final String LEHRER_KUERZEL_KEY = "";
	public static final String EIGENE_KURSE_SCHUELER_TAB_KEY = "EigeneKurs_Schueler";
	public static final String EIGENE_LEHRER_TAB_KEY = "Eigene_Lehrer";
	public static final String ALLE_TAB_KEY = "Alle";
	public static final String REFRESH_TIME_KEY = "Refresh_Time";
	public static final String USERNAME_KEY = "username";
	public static final String PASSWORD_KEY = "password";
	public static final String STUFE_KEY = "klasse";
	public static final String KURSE_KEY = "kurse";
	public static final String OBERSTUFE_KEY = "oberstufe";
	public static final String DEBUG_KEY = "debugmode";
	public static final String SP_KURSE_MIT_NAMEN_KEY = "sp_kurse_mit_nummern";
	public static final String SP_ID_KEY = "sp_id";
	public static final String JSON_TABS_KEY = "json_tabs";

	// --------------------------------------------------------
	public static final long REFRESH_DIFF = 1000 * 60 * 60; // Zeit in der nicht
															// neu Aktuallisiert
															// wird in
															// Millisekunden
	public static final int CONNECTION_TIMEOUT = 10000;
	public static final String PLAN_DIRECTORY = Environment.getExternalStorageDirectory().getPath()
			+ "/vertretungsplan/";
	public static final String SCHUELER_PLAN_FILE_NAME = "schueler_plan.html";
	public static final String LEHRER_PLAN_FILE_NAME = "lehrer_plan.html";
	public static final String SP_FILE_NAME = "stundenplan.json";
	public static final String LOG_FILE_NAME = "log.txt";
	public static final String LOG_FILE_NAME_2 = "log.old.txt";
	public static final String COOKIE_SUCH_STRING = "<input type=\"hidden\" name=\"return\" value=\"L2luZGV4LnBocC9pbnRlcm4v\" />\n      <input type=\"hidden\" name=";
	// URLS----------------------------------------------------
	public static final String LOGIN_URL = "https://www.ratsgymnasium-bielefeld.de/index.php/login/login?task=user.login";
	public static final String SCHUELER_PLAN_URL = "https://www.ratsgymnasium-bielefeld.de/index.php/intern/vertretungsplan-schueler";
	public static final String LEHRER_PLAN_URL = "https://www.ratsgymnasium-bielefeld.de/index.php/intern/vertretungsplan-lehrer";
	public static final String LOGIN_SEITE_URL = "https://www.ratsgymnasium-bielefeld.de/index.php/login";
	public static final String LOG_REPORT_EMAIL = "app@maxgb.de";
	public static final String LOG_REPORT_BETREFF = "Error/Log Report for Vertretungsplanapp Version:+ ";
	public static final String SP_GET_PLAN_URL = "http://sp.maxgb.de/get.php?id=";// ID muss angehängt werden

	// ScreenSize
	public static final int very_smallWidth = 450;
	public static final int smallWidth = 550;
	public static final int largeWidth = 700;
	public static final int ultra_lageWidth = 900;
	public static final int TEXTSIZELEHRER = 12;
	public static final int TEXTSIZESCHUELER = 15;
	public static final int TEXTSIZEBIGGER = 3;
	public static final int TEXTSIZESMALLER = 5;

	// HashMaps
	public static final HashMap<String, String> getKursnamen() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("ge", "Geschichte");
		map.put("sw", "SoWi");
		map.put("m", "Mathe");
		map.put("if", "Info");
		map.put("er", "Reli");
		map.put("d", "Deutsch");
		map.put("l", "Latein");
		map.put("bi", "Bio");
		map.put("ch", "Chemie");
		map.put("e", "Englisch");
		map.put("gr", "Griechisch");
		map.put("pl", "Philo");
		map.put("kr", "Reli");
		map.put("sp", "Sport");
		map.put("gee", "Geschichte");
		map.put("swe", "SoWi");
		map.put("ku", "Kunst");
		map.put("mu", "Musik");
		map.put("ek", "Erdkunde");
		map.put("s", "Spanisch");
		map.put("z-la", "Latein");
		map.put("z-ru", "Russisch");
		map.put("la", "Latein");
		map.put("f", "Französisch");
		map.put("fr", "Französisch");
		map.put("ru", "Russisch");
		map.put("cn", "Chinesisch");
		map.put("z-cn", "Chinesisch");
		map.put("ph", "Physik");
		map.put("vtr.","Vertretung");
		map.put("freis.", "Freisetzung");
		return map;
	}
	
	/**
	 * Liefert eine HashMap mit der Vertretungsart als Keys und dem dazugehörigen String der als Fach angezeigt werden soll zurück
	 * @return HashMap<Vertretungsart,Fach>
	 */
	public static final HashMap<String,String> getReplacementForSPVP(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("Entfall","");
		map.put("Vtr.","Vtr.");
		map.put("Freis.","Freis.");
		return map;
	}
	
	/**
	 * Liefert eine HashMap mit der Vertretungsplanart, wie sie auf der Webseite zu finden ist, als Key die stattdessen anzuzeigende kürzere Version zurück
	 * @return HashMap<Eigentlich Veretungsart,Neue Vertretungsart>
	 */
	public static final HashMap<String,String> getReplacementForArt(){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("Sondereins.", "Sonder.");
		map.put("Raum-Vtr.", "R-Vtr");
		map.put("Vertretung", "Vtr.");
		map.put("Pausenaufsicht","Aufs.");
		map.put("Betreuung", "Betreu.");
		map.put("Freisetzung", "Freis.");
		map.put("Verlegung", "Verlegt");
		
		return map;
	}

}
