package de.maxgb.vertretungsplan.fragments.stundenplan;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import de.maxgb.vertretungsplan.NormalUebersichtActivity;
import de.maxgb.vertretungsplan.manager.StundenplanManager;
import de.maxgb.vertretungsplan.util.Stunde;

import java.util.ArrayList;

public class NormalStundenplanFragment extends StundenplanFragment {

	protected void addUebersichtButton(TableLayout t) {
		Button uebersicht = new Button(getActivity());
		uebersicht.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), NormalUebersichtActivity.class);
				startActivity(i);

			}

		});
		uebersicht.setText("Übersicht");
		uebersicht.setHeight(15);

		t.addView(uebersicht);
	}

	@Override
	protected void anzeigen(ScrollView s) {
		s.removeAllViews();
		ArrayList<Stunde[]> stundenplan = StundenplanManager.getInstance(getContext()).getStundenplan();
		if (stundenplan == null) {
			s.addView(newTextViewCentered("Stundenplan noch nicht heruntergeladen bitte öffne das entsprechende Optionsmenu: ("
					+ StundenplanManager.getInstance(getContext()).getLastResult() + ")"));
			return;
		}

		// Neues TableLayout für 1. Stundenplan ScrollView 2. Button für Gesamtübersicht
		TableLayout t = new TableLayout(getActivity());
		ScrollView s2 = new ScrollView(getActivity());
		t.addView(s2);
		super.anzeigen(stundenplan, s2);

		addUebersichtButton(t);

		// Table zum HauptScrollView hinzufügen
		s.addView(t);

	}
}
