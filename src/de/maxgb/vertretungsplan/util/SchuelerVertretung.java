package de.maxgb.vertretungsplan.util;

public class SchuelerVertretung {
	public String klasse, art, fach, stunde, raum, tag, klausur, bemerkung;

	public SchuelerVertretung(String klasse, String stunde, String art, String fach, String raum, String tag,
			String klausur, String bemerkung) {
		this.klasse = (klasse == null) ? "" : klasse;
		this.stunde = (stunde == null) ? "" : stunde;

		String replaceArt = Constants.getReplacementForArt().get(art);
		art = (replaceArt != null) ? replaceArt : art;

		this.art = (art == null) ? "" : art;
		this.fach = (fach == null) ? "" : fach;
		this.raum = (raum == null) ? "" : raum;
		this.tag = (tag == null) ? "" : tag;
		this.klausur = (klausur == null) ? "" : klausur;
		this.bemerkung = (bemerkung == null) ? "" : bemerkung;
	}
}
