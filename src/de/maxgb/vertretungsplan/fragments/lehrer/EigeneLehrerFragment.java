package de.maxgb.vertretungsplan.fragments.lehrer;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.widget.ScrollView;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.LehrerVertretung;
import de.maxgb.vertretungsplan.util.Logger;

public class EigeneLehrerFragment extends LehrerFragment {
	private final String TAG = "EigeneLehrerFragment";

	@Override
	protected void anzeigen(ScrollView s) {
		try {
			s.removeAllViews();
			SharedPreferences pref = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
			VertretungsplanManager vertretungsplan = VertretungsplanManager.getInstance(
					pref.getBoolean(Constants.SCHUELER_KEY, false), pref.getBoolean(Constants.LEHRER_KEY, false));
			ArrayList<LehrerVertretung> vertretungen = vertretungsplan.getLehrerVertretungen();

			String kuerzel = pref.getString(Constants.LEHRER_KUERZEL_KEY, "");

			ArrayList<LehrerVertretung> eigeneVertretungen = new ArrayList<LehrerVertretung>();
			if (kuerzel != null && !kuerzel.equals("")) {

				for (int i = 0; i < vertretungen.size(); i++) {
					if (vertretungen.get(i).vertreter.trim().equals(kuerzel.trim())
							|| vertretungen.get(i).zuvertretender.trim().equals(kuerzel.trim())) {
						eigeneVertretungen.add(vertretungen.get(i));
					}
				}
			}

			anzeigen(eigeneVertretungen, vertretungsplan.getLehrerStand(), s);

		} catch (Exception e) {
			Logger.e(TAG, "Auswerten und Anzeigen fehlgeschlagen", e);
		}
	}

}