package de.maxgb.vertretungsplan.util;

public class LehrerVertretung extends SchuelerVertretung {
	public String zuvertretender;

	public LehrerVertretung(String neu, String klasse, String stunde, String art, String fach, String raum, String tag,
			String klausur, String bemerkung, String vertreter, String zuvertretender) {
		super(klasse, stunde, art, fach, raum, tag, klausur, bemerkung,vertreter);
		this.neu = (neu == null) ? "" : neu;
		this.zuvertretender = (zuvertretender == null) ? "" : zuvertretender;
	}
}
