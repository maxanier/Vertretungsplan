package de.maxgb.vertretungsplan.util;

public class SchuelerVertretung {
	public String klasse, art, fach, stunde, raum, tag, klausur, bemerkung;

	public SchuelerVertretung(String klasse, String stunde, String art, String fach, String raum, String tag,
			String klausur, String bemerkung) {
		this.klasse = (klasse == null) ? "" : klasse;
		this.stunde = (stunde == null) ? "" : stunde;
		art = (art == null) ? "" : art;
		if (art.equals("Sondereins.")) {
			art = "Sonder.";
		}
		if (art.equals("Raum-Vtr.")) {
			art = "R-Vtr.";
		}
		if (art.equals("Vertretung")) {
			art = "Vtr.";
		}
		if (art.equals("Pausenaufsicht")) {
			art = "Aufs.";
		}
		if (art.equals("Betreuung")) {
			art = "Betreu.";
		}
		if (art.equals("Freisetzung")) {
			art = "Freis.";
		}
		if (art.equals("Verlegung")) {
			art = "Verlegt";
		}
		this.art = art;
		this.fach = (fach == null) ? "" : fach;
		this.raum = (raum == null) ? "" : raum;
		this.tag = (tag == null) ? "" : tag;
		this.klausur = (klausur == null) ? "" : klausur;
		this.bemerkung = (bemerkung == null) ? "" : bemerkung;
	}
}
