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
	// Versions
	public static final String LAST_UPDATED_KEY = "last_updated";
	public static final boolean IS_BETA = false;// TODO Change for release

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
	public static final int CONNECTION_TIMEOUT = 15000;
	public static final String PLAN_DIRECTORY = Environment.getExternalStorageDirectory().getPath()
			+ "/vertretungsplan/";
	public static final String SCHUELER_PLAN_FILE_NAME = "schueler_plan_neu.html";
	public static final String LEHRER_PLAN_FILE_NAME = "lehrer_plan.html";
	public static final String SP_FILE_NAME = "stundenplan.json";
	public static final String LOG_FILE_NAME = "log.txt";
	public static final String LOG_FILE_NAME_2 = "log.old.txt";
	public static final String COOKIE_SUCH_STRING = "<input type=\"hidden\" name=\"return\" value=\"L2luZGV4LnBocC9pbnRlcm4v\" />\n      <input type=\"hidden\" name=";
	// URLS----------------------------------------------------
	public static final String LOGIN_URL = "https://ratsgymnasium-bielefeld.de/index.php/login?task=user.login";
	public static final String SCHUELER_PLAN_URL = "https://ratsgymnasium-bielefeld.de/index.php/intern/vertretungsplan-schueler";
	public static final String LEHRER_PLAN_URL = "https://ratsgymnasium-bielefeld.de/index.php/intern/vertretungsplan-lehrer";
	public static final String LOGIN_SEITE_URL = "https://ratsgymnasium-bielefeld.de/index.php/login";
	public static final String LOG_REPORT_EMAIL = "vertretungsplan@maxgb.de";
	public static final String LOG_REPORT_BETREFF = "Error/Log Report for Vertretungsplanapp Version:+ ";
	public static final String SP_GET_PLAN_URL = "http://maxgb.de/projects/stundenplan/get.php?id=";// ID muss angehängt werden

	// ScreenSize
	public static final int very_smallWidth = 450;
	public static final int smallWidth = 550;
	public static final int largeWidth = 850;
	public static final int ultra_lageWidth = 1000;
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
		map.put("vtr.", "Vertretung");
		map.put("freis.", "Freisetzung");
		//UnterstufenBezeichnung
		map.put("en", "Englisch");
		map.put("pol", "Politik");
		map.put("la", "Latein");
		map.put("de","Deutsch");
		map.put("ma", "Mathe");
		map.put("bio", "Biologie");
		map.put("phy", "Physik");
		map.put("ma-ph", "Mathe-Physik");
		return map;
	}

	/**
	 * Liefert eine HashMap mit der Vertretungsart als Keys und dem dazugehörigen String der als Fach angezeigt werden soll zurück
	 * 
	 * @return HashMap<Vertretungsart,Fach>
	 */
	public static final HashMap<String, String> getReplacementForSPVP() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Entfall", "");
		map.put("Vtr.", "Vtr.");
		map.put("Freis.", "Freis.");
		return map;
	}

	/**
	 * Liefert eine HashMap mit der Vertretungsplanart, wie sie auf der Webseite zu finden ist, als Key die stattdessen
	 * anzuzeigende kürzere Version zurück
	 * 
	 * @return HashMap<Eigentlich Veretungsart,Neue Vertretungsart>
	 */
	public static final HashMap<String, String> getReplacementForArt() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Sondereins.", "Sonder.");
		map.put("Raum-Vtr.", "R-Vtr");
		map.put("Vertretung", "Vtr.");
		map.put("Pausenaufsicht", "Aufs.");
		map.put("Betreuung", "Betreu.");
		map.put("Freisetzung", "Freis.");
		map.put("Verlegung", "Verlegt");

		return map;
	}

	public static String getCertificate(){
		String certificate="-----BEGIN CERTIFICATE-----\n" +
				"MIIC2TCCAkICCQDzbF4cN/SAsDANBgkqhkiG9w0BAQUFADCBsDELMAkGA1UEBhMC\n" +
				"REUxDDAKBgNVBAgMA05SVzESMBAGA1UEBwwJQmllbGVmZWxkMSEwHwYDVQQKDBhS\n" +
				"YXRzZ3ltbmFzaXVtIEJpZWxlZmVsZCAxJzAlBgNVBAMMHnd3dy5yYXRzZ3ltbmFz\n" +
				"aXVtLWJpZWxlZmVsZC5kZTEzMDEGCSqGSIb3DQEJARYkd2VibWFzdGVyQHJhdHNn\n" +
				"eW1uYXNpdW0tYmllbGVmZWxkLmRlMB4XDTEyMTIxNDA4MjYxNloXDTIyMTIxMjA4\n" +
				"MjYxNlowgbAxCzAJBgNVBAYTAkRFMQwwCgYDVQQIDANOUlcxEjAQBgNVBAcMCUJp\n" +
				"ZWxlZmVsZDEhMB8GA1UECgwYUmF0c2d5bW5hc2l1bSBCaWVsZWZlbGQgMScwJQYD\n" +
				"VQQDDB53d3cucmF0c2d5bW5hc2l1bS1iaWVsZWZlbGQuZGUxMzAxBgkqhkiG9w0B\n" +
				"CQEWJHdlYm1hc3RlckByYXRzZ3ltbmFzaXVtLWJpZWxlZmVsZC5kZTCBnzANBgkq\n" +
				"hkiG9w0BAQEFAAOBjQAwgYkCgYEAzc6Q7DEZJ4DPKP3OOLPiv7cXouHPeVOgRO4c\n" +
				"CDAQLQMFiZxYakLunAZTY7+DRxIc2ovt8ZBoh1yJyeCQUwyQPeZ6OhJBy5143RjR\n" +
				"Mgrg9alHbgyCkf7QXMEoGnAZbJE0FTqkomMlWqFAYnj4nEK23qHtakwg7MAvuglC\n" +
				"VtcTwAkCAwEAATANBgkqhkiG9w0BAQUFAAOBgQCi1aB4mDtXkUks3kkr+FvRSi2F\n" +
				"GAa0nD/IsZD3tjqIdjJOnghuBT+YelNUbDe9s2sL3nZ6fr98wMVD8ps/CsMAx/Cu\n" +
				"ZRCA8bRu9eKyxv5kh8k++UeB5oOxLDaqaFPnzXEkmWC1lMuPtYEt9OktUpAzpGYg\n" +
				"CIIB2Rboj2WdZAllqw==\n" +
				"-----END CERTIFICATE-----\n";
		return certificate;
	}

	public static String getDebugFiddlerCertificate(){
		String certificate="-----BEGIN CERTIFICATE-----\n" +
				"MIIDjTCCAvagAwIBAgIQTO9DL48OMKxN2lLAfC4wKzANBgkqhkiG9w0BAQsFADCB\n" +
				"izErMCkGA1UECxMiQ3JlYXRlZCBieSBodHRwOi8vd3d3LmZpZGRsZXIyLmNvbTEh\n" +
				"MB8GA1UECh4YAEQATwBfAE4ATwBUAF8AVABSAFUAUwBUMTkwNwYDVQQDHjAARABP\n" +
				"AF8ATgBPAFQAXwBUAFIAVQBTAFQAXwBGAGkAZABkAGwAZQByAFIAbwBvAHQwHhcN\n" +
				"MTQxMDIyMjIwMDAwWhcNMjUxMDIyMjE1OTU5WjCBizErMCkGA1UECxMiQ3JlYXRl\n" +
				"ZCBieSBodHRwOi8vd3d3LmZpZGRsZXIyLmNvbTEhMB8GA1UECh4YAEQATwBfAE4A\n" +
				"TwBUAF8AVABSAFUAUwBUMTkwNwYDVQQDHjAARABPAF8ATgBPAFQAXwBUAFIAVQBT\n" +
				"AFQAXwBGAGkAZABkAGwAZQByAFIAbwBvAHQwgZ8wDQYJKoZIhvcNAQEBBQADgY0A\n" +
				"MIGJAoGBAMP4cqn/669sqk17qCLVIL/b61PSqwpEQMIWfKReTB2Uwppm2sEbkbcr\n" +
				"jqHokLMIygYmmhaOU514hCRsqnlg2CsY3l35vQgwl9WZHjs2J0SZD8c6449bmlID\n" +
				"ZpC2SWe4CHSCZ/CHhAHp4YE6caHeiXQ8nosB7h7SlsagIIehKFqzAgMBAAGjge8w\n" +
				"gewwEgYDVR0TAQH/BAgwBgEB/wIBATATBgNVHSUEDDAKBggrBgEFBQcDATCBwAYD\n" +
				"VR0BBIG4MIG1gBD9W1g0FrYQcLPq5JePSlFxoYGOMIGLMSswKQYDVQQLEyJDcmVh\n" +
				"dGVkIGJ5IGh0dHA6Ly93d3cuZmlkZGxlcjIuY29tMSEwHwYDVQQKHhgARABPAF8A\n" +
				"TgBPAFQAXwBUAFIAVQBTAFQxOTA3BgNVBAMeMABEAE8AXwBOAE8AVABfAFQAUgBV\n" +
				"AFMAVABfAEYAaQBkAGQAbABlAHIAUgBvAG8AdIIQTO9DL48OMKxN2lLAfC4wKzAN\n" +
				"BgkqhkiG9w0BAQsFAAOBgQALtVmObEQ5vnJWOuUyfCAs8JCIvKdB01UZi/3f3STn\n" +
				"4i4AQYz+wOmF0jQ0mdQ5XXxisWbLlJMbHpLoM6GwlZ+okslqfx3Brhn4ajW5o65Q\n" +
				"28MUR20L86ftDkpvUKY8Vhx+nLOUlo/4+6Udx239BKLEkBF46XmCN2utaErawmN4\n" +
				"EQ==\n" +
				"-----END CERTIFICATE-----\n";
		return certificate;
	}

}
