package de.maxgb.vertretungsplan.fragments.stundenplan;

import java.util.Calendar;

import de.maxgb.vertretungsplan.R;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.TableLayout;
import android.widget.TableRow;

public class NormalUebersichtFragment extends NormalStundenplanFragment {

	/*
	 * In der Headline soll nur Wochentage und nicht "Heute" oder "Morgen" angezeigt werden
	 * @see de.maxgb.vertretungsplan.fragments.stundenplan.StundenplanFragment#newHeadline()
	 */
	@Override
	protected TableRow newHeadline() {
		TableRow headline = newTableRow();
		SpannableString stunde = new SpannableString("Stunde ");
		stunde.setSpan(new StyleSpan(Typeface.BOLD), 0, stunde.length(), 0);
		headline.addView(newTextView(stunde));

		for (int i = 0; i < getVisibleDayCount(); i++) {
			int day = ((getCurrentDayOfWeek() - 1 + i) % 7) + 1;
			if (day != Calendar.SUNDAY) {
				SpannableString tag;

				tag = new SpannableString(" " + convertToDayString(day) + " ");
				tag.setSpan(new StyleSpan(Typeface.BOLD), 0, tag.length(), 0);

				headline.addView(newTextViewCentered(tag));
			}
		}
		return headline;
	}

	/*
	 * Die Übersicht soll Montags beginnen
	 * @see de.maxgb.vertretungsplan.fragments.stundenplan.StundenplanFragment#getCurrentDayOfWeek()
	 */
	@Override
	protected int getCurrentDayOfWeek() {
		return Calendar.MONDAY;
	}

	/*
	 * In der Übersicht sollen alle Tage gezeigt werden
	 * @see de.maxgb.vertretungsplan.fragments.stundenplan.StundenplanFragment#getVisibleDayCount()
	 */
	@Override
	protected int getVisibleDayCount() {
		return 7;
	}

	/*
	 * In der Übersicht wird kein Übersichtsbutton benötigt
	 * @see
	 * de.maxgb.vertretungsplan.fragments.stundenplan.NormalStundenplanFragment#addUebersichtButton(android.widget.TableLayout)
	 */
	@Override
	protected void addUebersichtButton(TableLayout t) {
	}

	/*
	 * Es soll keine Stunde markiert werden
	 * @see de.maxgb.vertretungsplan.fragments.stundenplan.StundenplanFragment#getCurrentLesson(boolean)
	 */
	@Override
	protected int getCurrentLesson(boolean samstag) {
		return -2;
	}
}
