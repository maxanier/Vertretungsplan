package de.maxgb.vertretungsplan.fragments.stundenplan;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.R;
import de.maxgb.vertretungsplan.fragments.AnzeigeFragment;
import de.maxgb.vertretungsplan.manager.StundenplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Stunde;

public abstract class StundenplanFragment extends AnzeigeFragment implements StundenplanManager.OnUpdateListener {

	private final String TAG = "StundenplanFragment";
	/**
	 * Stores the currently highlighted Lesson
	 */
	private int currentlyMarkedLesson = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		StundenplanManager.getInstance().registerOnUpdateListener(this);

		View rootView = inflater.inflate(R.layout.fragment_scroll_view, container, false);
		super.onCreateView(inflater, container, savedInstanceState);

		ScrollView s = (ScrollView) rootView.findViewById(R.id.standard_scroll_view);
		anzeigen(s);

		return rootView;
	}

	@Override
	public void onDestroy() {
		StundenplanManager.getInstance().unregisterOnUpdateListener(this);
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * Falls die als aktuell markierte Stunde nicht mehr der aktuellen entspricht, neu anzeigen
		 */
		if (currentlyMarkedLesson != getCurrentLesson(getCurrentDayOfWeek() == Calendar.SATURDAY)) {
			update();
		}
	}

	@Override
	public void onStundenplanUpdate() {
		update();

	}

	private TableRow newHeadline() {
		TableRow headline = newTableRow();
		SpannableString stunde = new SpannableString("Stunde ");
		stunde.setSpan(new StyleSpan(Typeface.BOLD), 0, stunde.length(), 0);
		headline.addView(newTextView(stunde));

		for (int i = 0; i < getVisibleDayCount(); i++) {
			int day = ((getCurrentDayOfWeek() - 1 + i) % 7) + 1;
			if (day != Calendar.SUNDAY) {
				SpannableString tag;
				if (i == 0) {
					tag = new SpannableString(" Heute ");
					tag.setSpan(new StyleSpan(Typeface.BOLD), 0, tag.length(), 0);
				} else if (i == 1) {
					tag = new SpannableString(" Morgen ");
					tag.setSpan(new StyleSpan(Typeface.BOLD), 0, tag.length(), 0);
				} else {
					tag = new SpannableString(" " + convertToDayString(day) + " ");
					tag.setSpan(new StyleSpan(Typeface.BOLD), 0, tag.length(), 0);
				}
				headline.addView(newTextViewCentered(tag));
			}
		}
		return headline;
	}

	protected void anzeigen(ArrayList<Stunde[]> stundenplan, ScrollView s) {

		TableLayout table = new TableLayout(getActivity());
		table.addView(newHeadline());
		currentlyMarkedLesson = getCurrentLesson(getCurrentDayOfWeek() == Calendar.SATURDAY);
		for (int i = 0; i < (StundenplanManager.BEGINN_NACHMITTAG - 1 + StundenplanManager.ANZAHL_NACHMITTAG); i++) {
			View border = new View(getActivity());
			border.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, 1));
			border.setBackgroundColor(Color.GRAY);
			table.addView(border);

			TableRow stunde = newTableRow();
			
			//Vormittags die Stunde in die erste Spalte schreiben, Nachmittags ein leeres TextView (da die Stunden nicht mehr definiert sind)
			if(i+1>=StundenplanManager.BEGINN_NACHMITTAG){
				stunde.addView(newTextView(""));
			}
			else{
				stunde.addView(newTextView(Integer.toString(i + 1)));
			}

			for (int j = 0; j < getVisibleDayCount(); j++) {
				int day = ((getCurrentDayOfWeek() - 1 + j) % 7) + 1;
				if (day != Calendar.SUNDAY) {
					final Stunde st = stundenplan.get(day - 1 - 1)[i];// day(1-7[So-Sa])-1(Sonntag fällt weg)-1(Liste beginnt bei
																		// 0)

					// Fach
					String f;
					if (getActivity().getSharedPreferences(Constants.PREFS_NAME, 0).getBoolean(
							Constants.SP_KURSE_MIT_NAMEN, false)) {
						f = st.getName();
					} else {
						f = st.getKurs();
					}
					TextView vf = newTextViewCentered(f);

					// Raum

					SpannableString r = new SpannableString(st.getRaum());
					r.setSpan(new StyleSpan(Typeface.ITALIC), 0, r.length(), 0);
					TextView vr = newTextViewCentered(r);

					TableRow.LayoutParams params = new TableRow.LayoutParams();
					params.setMargins(3, 3, 2, 5);
					vr.setLayoutParams(params);

					TableLayout kurs = new TableLayout(getActivity());
					kurs.setGravity(Gravity.CENTER_HORIZONTAL);

					// Falls "Heute" aktuelle Stunde gelb hinterlegen
					if (j == 0) {
						// Falls die gerade behandelte Stunde der aktuellen entspricht
						// Oder aktuell Nachmittag ist und gerade die Nachmittagsstunden behandelt werden
						if (currentlyMarkedLesson == i + 1
								|| (currentlyMarkedLesson == -1 && i + 1 >= StundenplanManager.BEGINN_NACHMITTAG)) {
							// Den Hintergrund des Kurses gelb färben
							kurs.setBackgroundColor(Color.YELLOW);
						}

					}

					kurs.addView(vf);
					kurs.addView(vr);

					// Uhrzeit
					TextView vz = new TextView(getActivity());
					if (i >= StundenplanManager.BEGINN_NACHMITTAG - 1) {

						vz.setText(st.getUhrzeit());

						vz.setLayoutParams(params);
						vz.setTextSize(Constants.TEXTSIZESCHUELER - Constants.TEXTSIZESMALLER);
						vz.setGravity(Gravity.CENTER_HORIZONTAL);

						kurs.addView(vz);
					}

					if (st.isModified()) {
						vf.setTextColor(Color.RED);
						vr.setTextColor(Color.RED);
						vz.setTextColor(Color.RED);

						kurs.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
								TableLayout t = new TableLayout(getActivity());
								TableRow labels = newTableRow();
								TableRow stunde = newTableRow();
								TableRow vertretung = newTableRow();
								String tag = st.getTag();
								try {
									int day = getWeekDayFromString(tag);
									tag = tag.replace(convertToDayString(day), "");
								} catch (IllegalArgumentException e) {

								}

								labels.addView(newBoldTextView(tag));
								labels.addView(newBoldTextView("Kurs "));
								labels.addView(newBoldTextView("Raum "));
								labels.addView(newBoldTextView("Uhrzeit "));
								labels.addView(newBoldTextView("Art "));
								labels.addView(newBoldTextView("Bemerk."));
								stunde.addView(newTextView("Eigentlich "));
								stunde.addView(newTextView(st.getOldKurs()));
								stunde.addView(newTextView(st.getOldRaum()));
								stunde.addView(newTextView(st.getUhrzeit()));
								vertretung.addView(newTextView("Vertretung "));
								vertretung.addView(newTextView(st.getKurs()));
								vertretung.addView(newTextView(st.getRaum()));
								vertretung.addView(newTextView(""));
								vertretung.addView(newTextView(st.getArt()));
								vertretung.addView(newTextView(st.getKlausur() + " " + st.getBemerkung()));

								t.addView(labels);
								t.addView(stunde);
								t.addView(vertretung);
								LinearLayout layout = new LinearLayout(getActivity());
								t.setGravity(Gravity.CENTER_HORIZONTAL);
								layout.setGravity(Gravity.CENTER_HORIZONTAL);
								layout.addView(t);
								if (!(android.os.Build.VERSION.SDK_INT >= 11)) {
									t.setBackgroundColor(Color.WHITE);
									layout.setBackgroundColor(Color.WHITE);

								}
								builder.setView(layout);
								builder.setPositiveButton("Ok", null);
								builder.create().show();

							}

						});
					}

					stunde.addView(kurs);
				}

			}
			table.addView(stunde);

		}

		s.addView(table);
	}

	protected abstract void anzeigen(ScrollView s);

	protected String convertToDayString(int i) {
		switch (i) {
		case Calendar.SUNDAY:
			return "Sonntag";
		case Calendar.MONDAY:
			return "Montag";
		case Calendar.TUESDAY:
			return "Dienstag";
		case Calendar.WEDNESDAY:
			return "Mittwoch";
		case Calendar.THURSDAY:
			return "Donnerstag";
		case Calendar.FRIDAY:
			return "Freitag";
		case Calendar.SATURDAY:
			return "Samstag";
		default:
			return "";
		}
	}

	protected int getCurrentDayOfWeek() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Liefert die aktuelle Schulstunde zurück, wobei z.B. um 5:00 Uhr bereits 1 zurückgeliefert wird und während der Pausen
	 * bereits die folgende Stunde. Nachmittags(nach 14:00) wird -1 zurückgeliefert
	 * 
	 * @return Aktuelle Stunde
	 */
	protected int getCurrentLesson(boolean samstag) {
		int v = 0;// Verschiebung in Minuten
		if (samstag) {
			v = 10;// Samstag alles 10 min spaeter
		}
		Calendar current = Calendar.getInstance();
		int hour = current.get(Calendar.HOUR_OF_DAY);
		int minute = current.get(Calendar.MINUTE);
		int time = hour*60 + minute;
		Logger.i(TAG, "Current time is: "+hour+":"+minute+" -> m: "+time);
		if (time < (8*60 + 35 + v)) {
			// Vor Ende der ersten Stunde
			return 1;
		} else if (time < (9*60 + 25+ v)) {
			// Vor Ende der zweiten Stunde
			return 2;
		} else if (time < (10 *60+ 25+ v)) {
			return 3;
		} else if (time < (11 *60+ 15+ v)) {
			return 4;
		} else if (time < (12 *60+ 15 + v)) {
			return 5;
		} else if (time < (13 *60+ 5 + v)) {
			return 6;
		} else if (time < (14 *60+ v)) {
			return 7;
		}
		return -1;

	}

	protected int getVisibleDayCount() {
		if (!screenSizeSet) {
			retrieveScreenSize();
		}
		switch (displaySize) {
		case 0:
			return 3;
		case 1:
			return 3;
		case 2:
			return 3;
		case -1:
			return 4;
		case -2:
			return 7;
		default:
			return 3;
		}
	}

	protected int getWeekDayFromString(String s) throws IllegalArgumentException {
		s = s.toLowerCase();
		if (s.contains("montag")) {
			return Calendar.MONDAY;
		}
		if (s.contains("dienstag")) {
			return Calendar.TUESDAY;
		}
		if (s.contains("mittwoch")) {
			return Calendar.WEDNESDAY;
		}
		if (s.contains("donnerstag")) {
			return Calendar.THURSDAY;
		}
		if (s.contains("freitag")) {
			return Calendar.FRIDAY;
		}
		if (s.contains("samstag")) {
			return Calendar.SATURDAY;
		}
		throw new IllegalArgumentException("Kein bekannter Tag");
	}

	protected void update() {
		ScrollView s = (ScrollView) this.getView().findViewById(R.id.standard_scroll_view);
		anzeigen(s);
	}

}
