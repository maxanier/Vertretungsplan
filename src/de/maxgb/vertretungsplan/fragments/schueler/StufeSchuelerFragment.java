package de.maxgb.vertretungsplan.fragments.schueler;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.widget.ScrollView;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Logger;
import de.maxgb.vertretungsplan.util.SchuelerVertretung;

public class StufeSchuelerFragment extends SchuelerFragment {
	private final String TAG = "StufeSchuelerFragment";

	
	@Override
	protected void anzeigen(ScrollView s) {
		try {
			s.removeAllViews();
			SharedPreferences pref = getActivity().getSharedPreferences(
					Constants.PREFS_NAME, 0);
			VertretungsplanManager vertretungsplan = VertretungsplanManager.getInstance(
					pref.getBoolean(Constants.SCHUELER_KEY, false),
					pref.getBoolean(Constants.LEHRER_KEY, false));
			ArrayList<SchuelerVertretung> vertretungen = vertretungsplan
					.getSchuelerVertretungen();

			String stufe = pref.getString(Constants.STUFE_KEY, "");

			ArrayList<SchuelerVertretung> eigeneVertretungen = new ArrayList<SchuelerVertretung>();
			if (stufe != null && !stufe.equals("")) {
				for (int i = 0; i < vertretungen.size(); i++) {
					String klasse = vertretungen.get(i).klasse.trim();
					if (klasse.equals(stufe.trim())
							|| klasse.equals("(" + stufe.trim() + ")")) {
						eigeneVertretungen.add(vertretungen.get(i));
					}
				}
			}

			anzeigen(eigeneVertretungen, vertretungsplan.getSchuelerStand(), s);

		} catch (Exception e) {
			Logger.e(TAG, "Auswerten und Anzeigen fehlgeschlagen", e);
		}
		
	}
}