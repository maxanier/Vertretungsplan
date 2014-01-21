package de.maxgb.vertretungsplan.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.R;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager.OnUpdateListener;

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

		ScrollView s = (ScrollView) rootView.findViewById(R.id.standard_scroll_view);
		anzeigen(s);
		return rootView;
	}

	@Override
	public void onDestroy() {
		try {
			VertretungsplanManager.getCreatedInstance().unregisterOnUpdateListener(this);
		} catch (NullPointerException e) {
			// Manager was already deleted, then no need to unregister
		}
		super.onDestroy();
	}

	@Override
	public void onVertretungsplanUpdate() {
		ScrollView s = (ScrollView) this.getView().findViewById(R.id.standard_scroll_view);
		anzeigen(s);

	}

	protected abstract void anzeigen(ScrollView s);

}
