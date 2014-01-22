package de.maxgb.vertretungsplan.fragments.stundenplan;

import java.util.ArrayList;

import android.widget.ScrollView;
import de.maxgb.vertretungsplan.manager.StundenplanManager;
import de.maxgb.vertretungsplan.util.Stunde;

public class NormalStundenplanFragment extends StundenplanFragment {

	@Override
	protected void anzeigen(ScrollView s) {
		s.removeAllViews();
		ArrayList<Stunde[]> stundenplan = StundenplanManager.getInstance().getStundenplan();
		if (stundenplan == null) {
			s.addView(newTextViewCentered("Stundenplan noch nicht heruntergeladen bitte öffne das entsprechende Optionsmenu: ("+StundenplanManager.getInstance().getLastResult()+")"));
			return;
		}
		super.anzeigen(stundenplan, s);

	}
}
