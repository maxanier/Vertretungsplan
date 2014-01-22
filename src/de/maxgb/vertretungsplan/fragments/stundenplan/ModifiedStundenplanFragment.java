package de.maxgb.vertretungsplan.fragments.stundenplan;

import java.util.ArrayList;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.manager.StundenplanManager;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.SchuelerVertretung;
import de.maxgb.vertretungsplan.util.Stunde;

public class ModifiedStundenplanFragment extends StundenplanFragment implements VertretungsplanManager.OnUpdateListener {

	private final String TAG = "ModifiedStundenplanFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try {
			VertretungsplanManager.getCreatedInstance().registerOnUpdateListener(this);
		} catch (NullPointerException e) {
			Logger.w(TAG, "Could not register listener, since VertretungsplanManager was not instatiated yet");
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			VertretungsplanManager.getCreatedInstance().unregisterOnUpdateListener(this);
		} catch (NullPointerException e) {
			// Sowieso schon gelöscht
		}
		
	}

	@Override
	public void onVertretungsplanUpdate() {
		update();// Neu anzeigen

	}

	@Override
	protected void anzeigen(ScrollView s) {
		s.removeAllViews();

		// Vertretungen und Stundenplan holen
		SharedPreferences pref = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
		ArrayList<SchuelerVertretung> vertretungen = VertretungsplanManager.getInstance(
				pref.getBoolean(Constants.SCHUELER_KEY, false), pref.getBoolean(Constants.LEHRER_KEY, false))
				.getSchuelerVertretungen();
		ArrayList<SchuelerVertretung> eigeneVertretungen = new ArrayList<SchuelerVertretung>();
		ArrayList<Stunde[]> stundenplan = StundenplanManager.getInstance().getClonedStundenplan();

		// Neues TableLayout für: 1. Experimentell Hinweis 2. Stand 3. StundenplanScrollView
		TableLayout t = new TableLayout(getActivity());
		// Experimentell Hinweis
		TextView ex = newBigTextView("Experimentell");
		ex.setTextColor(Color.RED);
		t.addView(ex);
		// Stand
		TableRow stand = newTableRow();
		stand.addView(newTextView(VertretungsplanManager.getCreatedInstance().getSchuelerStand()));
		stand.setGravity(Gravity.CENTER_HORIZONTAL);
		t.addView(stand);

		// Stundenplan, wobei s2 später gefüllt wird
		ScrollView s2 = new ScrollView(getActivity());
		t.addView(s2);
		s.addView(t);
		// TODO Remove Experimentell status

		if (stundenplan == null) {
			alert("Stundenplan noch nicht oder fehlerhaft heruntergeladen: "
					+ StundenplanManager.getInstance().getLastResult());
			return;
		}
		if (vertretungen == null) {
			alert("Vertretungen noch nicht heruntergeladen");
			return;
		}
		String stufe = pref.getString(Constants.STUFE_KEY, "");
		boolean oberstufe = pref.getBoolean(Constants.OBERSTUFE_KEY, false);

		if (stufe != null && !stufe.equals("")) {
			for (int i = 0; i < vertretungen.size(); i++) {
				String klasse = vertretungen.get(i).klasse.trim();
				if (klasse.equals(stufe.trim()) || klasse.equals("(" + stufe.trim() + ")")) {
					eigeneVertretungen.add(vertretungen.get(i));
				}
			}
		}

		for (int i = 0; i < eigeneVertretungen.size(); i++) {
			SchuelerVertretung v = eigeneVertretungen.get(i);
			Logger.i(TAG, "Vertretung: " + v.stunde + v.fach + v.klasse);
			try {

				int day = getWeekDayFromString(v.tag);
				Stunde[] tag = stundenplan.get(day - 1 - 1);// Sonntag fällt weg: -1,Liste beginnt bei 0: -1
				int stunde = Integer.parseInt(v.stunde);
				Stunde st = tag[stunde - 1];
				Logger.i(TAG, "Entsprechende Stunde: " + st.toString() + " am: " + this.convertToDayString(day));
				
				//Je nach Art unterschiedliche Fächer anzeigen
				String fach = Constants.getReplacementForSPVP().get(v.art);
				if(fach==null){
					fach=v.fach;
				}
				
				if (!oberstufe) {
					
					
					st.vertreteten(fach, v.raum, v.bemerkung, v.klausur, v.art, v.tag);
				} else {
					if (st.getKurs().trim().equals(v.fach.trim())) {
						st.vertreteten(fach, v.raum, v.bemerkung, v.klausur, v.art, v.tag);
						Logger.i(TAG, "Vertretende Stunde gefunden: " + st.toString());
					}
				}
			} catch (NumberFormatException e) {
				Logger.w(TAG, "Stunde konnte nicht als int geparsed werden");
			} catch (IllegalArgumentException e) {
				Logger.w(TAG, "Tag enthält keinen Wochentag: " + v.tag);
			}

		}
		super.anzeigen(stundenplan, s2);

	}

}
