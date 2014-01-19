package de.maxgb.vertretungsplan.fragments.schueler;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.widget.ScrollView;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Logger;
import de.maxgb.vertretungsplan.util.SchuelerVertretung;

public class KurseSchuelerFragment extends SchuelerFragment {
	private final String TAG = "KurseSchuelerFragment";

	@SuppressLint("NewApi")
	@Override
	protected void anzeigen(ScrollView s) {
		try {
			s.removeAllViews();
			SharedPreferences pref = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

			VertretungsplanManager vertretungsplan = VertretungsplanManager.getInstance(
					pref.getBoolean(Constants.SCHUELER_KEY, false), pref.getBoolean(Constants.LEHRER_KEY, false));
			ArrayList<SchuelerVertretung> vertretungen = vertretungsplan.getSchuelerVertretungen();

			String stufe = pref.getString(Constants.STUFE_KEY, "");
			Set<String> kurse = pref.getStringSet(Constants.KURSE_KEY, null);

			ArrayList<SchuelerVertretung> eigeneVertretungen = new ArrayList<SchuelerVertretung>();
			if (stufe != null && stufe != "") {
				for (int i = 0; i < vertretungen.size(); i++) {
					if (vertretungen.get(i).klasse.trim().equals(stufe.trim())) {

						if (kurse != null) {
							if (kurse.contains(vertretungen.get(i).fach)) {
								eigeneVertretungen.add(vertretungen.get(i));
							}
						} else {
							eigeneVertretungen.add(vertretungen.get(i));
						}
					}
				}
			}

			anzeigen(eigeneVertretungen, vertretungsplan.getSchuelerStand(), s);

		} catch (Exception e) {
			Logger.e(TAG, "Auswerten und Anzeigen fehlgeschlagen", e);
		}

	}
}