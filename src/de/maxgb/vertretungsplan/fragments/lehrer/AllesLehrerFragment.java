package de.maxgb.vertretungsplan.fragments.lehrer;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.widget.ScrollView;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.LehrerVertretung;
import de.maxgb.vertretungsplan.util.Logger;

public class AllesLehrerFragment extends LehrerFragment {
	private final String TAG = "AllesLehrerFragment";

	@Override
	protected void anzeigen(ScrollView s) {
		try {
			s.removeAllViews();
			SharedPreferences pref = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
			VertretungsplanManager vertretungsplan = VertretungsplanManager.getInstance(
					pref.getBoolean(Constants.SCHUELER_KEY, false), pref.getBoolean(Constants.LEHRER_KEY, false));
			ArrayList<LehrerVertretung> vertretungen = vertretungsplan.getLehrerVertretungen();

			anzeigen(vertretungen, vertretungsplan.getLehrerStand(), s);

		} catch (Exception e) {
			Logger.e(TAG, "Auswerten und Anzeigen fehlgeschlagen", e);
		}
	}

}
