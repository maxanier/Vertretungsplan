package de.maxgb.vertretungsplan.util;

public class LehrerVertretung extends SchuelerVertretung {
	public String vertreter, zuvertretender;

	public LehrerVertretung(String klasse, String stunde, String art,
			String fach, String raum, String tag, String klausur,
			String bemerkung, String vertreter, String zuvertretender) {
		super(klasse, stunde, art, fach, raum, tag, klausur, bemerkung);
		this.vertreter = (vertreter == null) ? "" : vertreter;
		this.zuvertretender = (zuvertretender == null) ? "" : zuvertretender;
	}
}
