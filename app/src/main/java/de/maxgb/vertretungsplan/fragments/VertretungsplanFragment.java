package de.maxgb.vertretungsplan.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.R;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager.OnUpdateListener;
import de.maxgb.vertretungsplan.util.Constants;

import java.io.File;

/**
 * Standard Fragment für Vertretungen
 * 
 * @author Max Becker
 * 
 */
public abstract class VertretungsplanFragment extends AnzeigeFragment implements OnUpdateListener {
	private final String TAG = "VertretungsplanFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (!screenSizeSet) {
			retrieveScreenSize();
		}
		try {
			VertretungsplanManager.getCreatedInstance().registerOnUpdateListener(this);
		} catch (NullPointerException e) {
			Logger.e(TAG, "VertretungsplanManager was not instantiated, when creating Fragment", e);
		}

		View rootView = inflater.inflate(R.layout.fragment_scroll_view, container, false);

		Logger.i(TAG, "Creating VertretungsplanFragmentView");
		ScrollView s = (ScrollView) rootView.findViewById(R.id.standard_scroll_view);

		if (new File(getActivity().getFilesDir(), Constants.SCHUELER_PLAN_FILE_NAME).exists()
				|| new File(getActivity().getFilesDir(), Constants.LEHRER_PLAN_FILE_NAME).exists()) {
			anzeigen(s);
		} else {
			SharedPreferences pref = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
			if (pref.getString(Constants.USERNAME_KEY, null) == null
					|| (pref.getString(Constants.STUFE_KEY, null) == null && pref.getString(
							Constants.LEHRER_KUERZEL_KEY, null) == null)) {
				s.addView(newTextViewCentered("Bitte aktualisiere den Vertretungsplan. Gebe dafür deinen Nutzernamen, dein Passwort und deine Stufe in den Optionen ein"));
			} else {
				s.addView(newTextViewCentered("Bitte aktualisiere den Vertretungsplan"));
			}

		}

		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			VertretungsplanManager.getCreatedInstance().unregisterOnUpdateListener(this);
		} catch (NullPointerException e) {
			// Manager was already deleted, then no need to unregister
		}

	}

	@Override
	public void onVertretungsplanUpdate() {
		ScrollView s = (ScrollView) this.getView().findViewById(R.id.standard_scroll_view);
		anzeigen(s);

	}

	protected abstract void anzeigen(ScrollView s);

}
