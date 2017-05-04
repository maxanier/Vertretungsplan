package de.maxgb.vertretungsplan.util;

import de.maxgb.android.util.Logger;

public class LehrerVertretung extends SchuelerVertretung {
	public String zuvertretender;
	public boolean neu;

	public LehrerVertretung(String neu, String klasse, String stunde, String art, String fach, String raum, String tag,
			String klausur, String bemerkung, String vertreter, String zuvertretender) {
		super(klasse, stunde, art, fach, raum, tag, klausur, bemerkung,vertreter);
		this.neu = neu!=null&&!neu.isEmpty();
		this.zuvertretender = (zuvertretender == null) ? "" : zuvertretender;
	}
}
