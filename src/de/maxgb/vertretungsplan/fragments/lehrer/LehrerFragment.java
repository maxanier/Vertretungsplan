package de.maxgb.vertretungsplan.fragments.lehrer;

import java.util.ArrayList;

import android.R.color;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.maxgb.vertretungsplan.fragments.VertretungsplanFragment;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.LehrerVertretung;
import de.maxgb.vertretungsplan.util.Logger;

public abstract class LehrerFragment extends VertretungsplanFragment {
	private final String TAG = "LehrerFragment";

	private TableLayout neuerTag(String tag, LinearLayout l) {
		LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		TableRow.LayoutParams trparams = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		trparams.setMargins(3, 3, 2, 6);

		if (l.getChildCount() != 1) {
			TextView leer = new TextView(getActivity());
			leer.setText("  ");
			TableRow leereReihe = new TableRow(getActivity());
			leereReihe.setLayoutParams(trparams);
			leereReihe.addView(leer);
			l.addView(leereReihe);
		}

		SpannableString spanString = new SpannableString(tag);
		spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		TextView tv1 = new TextView(getActivity());
		tv1.setText(spanString);
		tv1.setTextSize(Constants.TEXTSIZELEHRER + 2);
		TableRow tr1 = new TableRow(getActivity());
		// tr.setLayoutParams(trparams);
		tr1.addView(tv1);
		l.addView(tr1);

		TableLayout layoutTable = new TableLayout(getActivity());
		layoutTable.setLayoutParams(params);
		layoutTable.setBackgroundColor(color.black);

		TableRow tr2 = new TableRow(getActivity());
		tr2.setLayoutParams(trparams);

		// Definition der Spannabel Strings----------------------
		SpannableString spanVertreter = new SpannableString("Ver. ");
		spanVertreter.setSpan(new StyleSpan(Typeface.BOLD), 0, spanVertreter.length(), 0);

		SpannableString spanArt = new SpannableString("Art ");
		spanArt.setSpan(new StyleSpan(Typeface.BOLD), 0, spanArt.length(), 0);

		SpannableString spanStunde = new SpannableString("St.");
		spanStunde.setSpan(new StyleSpan(Typeface.BOLD), 0, spanStunde.length(), 0);

		SpannableString spanKlasse = new SpannableString("Kl.");
		spanKlasse.setSpan(new StyleSpan(Typeface.BOLD), 0, spanKlasse.length(), 0);

		SpannableString spanZuVertretender = new SpannableString("statt");
		spanZuVertretender.setSpan(new StyleSpan(Typeface.BOLD), 0, spanZuVertretender.length(), 0);

		SpannableString spanFach = new SpannableString("Fach ");
		spanFach.setSpan(new StyleSpan(Typeface.BOLD), 0, spanFach.length(), 0);

		SpannableString spanRaum = new SpannableString("Raum ");
		spanRaum.setSpan(new StyleSpan(Typeface.BOLD), 0, spanRaum.length(), 0);

		SpannableString spanKlausur = new SpannableString("Kl. ");
		spanKlausur.setSpan(new StyleSpan(Typeface.BOLD), 0, spanKlausur.length(), 0);

		SpannableString spanBemerkung = new SpannableString("Bemerk. ");
		spanBemerkung.setSpan(new StyleSpan(Typeface.BOLD), 0, spanBemerkung.length(), 0);

		tr2.setLayoutParams(params);
		tr2.setLayoutParams(params);
		tr2.addView(newTextView(spanVertreter));
		tr2.addView(newTextView(spanArt));
		tr2.addView(newTextView(spanStunde));
		tr2.addView(newTextView(spanKlasse));
		tr2.addView(newTextView(spanZuVertretender));
		tr2.addView(newTextView(spanFach));
		tr2.addView(newTextView(spanRaum));

		if (displaySize == -1) {
			tr2.addView(newTextView(spanKlausur));
			tr2.addView(newTextView(spanBemerkung));
		} else if (displaySize == 0) {
			tr2.addView(newTextView(spanKlausur));
		}

		tr2.setBackgroundColor(color.white);
		layoutTable.addView(tr2);
		return layoutTable;

	}

	protected void anzeigen(ArrayList<LehrerVertretung> vertretungen, String stand, ScrollView s) {

		// Layout erstellen
		LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		TableRow.LayoutParams trparams = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		if (vertretungen != null && vertretungen.size() > 0) {
			Logger.i(TAG, "Anzeigen gestartet");

			LinearLayout l = new LinearLayout(getActivity());
			l.setLayoutParams(params);
			l.setOrientation(LinearLayout.VERTICAL);
			// ------------------------------

			// Stand anzeigen
			SpannableString spanStand = new SpannableString(stand);
			spanStand.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanStand.length(), 0);
			TextView tv_stand = new TextView(getActivity());
			tv_stand.setLayoutParams(new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			tv_stand.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			tv_stand.setText(spanStand);
			tv_stand.setTextSize(Constants.TEXTSIZELEHRER + 1);

			TableRow tr_stand = new TableRow(getActivity());
			tr_stand.setLayoutParams(new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			tr_stand.addView(tv_stand);

			l.addView(tr_stand);
			// -------------------------------------------
			String tag = vertretungen.get(0).tag;
			TableLayout layoutTable = neuerTag(tag, l);
			for (int i = 0; i < vertretungen.size(); i++) {

				LehrerVertretung v = vertretungen.get(i);
				if (tag != v.tag) {
					l.addView(layoutTable);
					tag = vertretungen.get(i).tag;
					layoutTable = neuerTag(tag, l);
				}

				TextView vertreter = newTextView(v.vertreter.trim() + " ");
				TextView art = newTextView(v.art.trim() + " ");
				TextView stunde = newTextView(v.stunde.trim() + " ");
				TextView klasse = newTextView(v.klasse.trim() + " ");
				TextView zuVertretender = newTextView(v.zuvertretender + " ");
				TextView fach = newTextView(v.fach.trim() + " ");
				TextView raum = newTextView(v.raum);

				TableRow tr = new TableRow(getActivity());
				tr.setLayoutParams(params);

				tr.addView(vertreter);
				tr.addView(art);
				tr.addView(stunde);
				tr.addView(klasse);
				tr.addView(zuVertretender);
				tr.addView(fach);
				tr.addView(raum);

				if (displaySize == 1 || displaySize == 2) {
					if (!v.klausur.equals("") || !v.bemerkung.equals("")) {
						final String bemerkung = v.bemerkung;
						final String klausur = v.klausur;
						tr.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
								TableLayout table = new TableLayout(getActivity());
								TableRow row1 = new TableRow(getActivity());
								TableRow row2 = new TableRow(getActivity());

								if (klausur.equals("K")) {
									row1.addView(newBigTextView("Klausur"));
									table.addView(row1);
								} else if (!klausur.equals("")) {
									row1.addView(newBigTextView("Klausur: "));
									row1.addView(newBigTextView(klausur));
									table.addView(row1);
								}
								if (!bemerkung.equals("")) {
									row2.addView(newTextView("Bemerkung: "));
									row2.addView(newTextView(bemerkung));
									table.addView(row2);
								}
								table.setGravity(Gravity.CENTER_HORIZONTAL);
								LinearLayout l = new LinearLayout(getActivity());
								l.setGravity(Gravity.CENTER_HORIZONTAL);
								l.addView(table);
								if (!(android.os.Build.VERSION.SDK_INT >= 11)) {
									table.setBackgroundColor(Color.WHITE);
									l.setBackgroundColor(Color.WHITE);

								}
								builder.setView(l);
								builder.setPositiveButton("Ok", null);
								builder.create().show();
							}
						});
						tr.addView(newTextView("X"));
					}
				} else if (displaySize == 0) {
					tr.addView(newTextView(v.klausur));

					if (!v.bemerkung.equals("")) {
						final String bemerkung = v.bemerkung;
						tr.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
								TableLayout table = new TableLayout(getActivity());
								TableRow row1 = new TableRow(getActivity());
								if (!bemerkung.equals("")) {
									row1.addView(newBigTextView("Bemerkung: "));
									row1.addView(newBigTextView(bemerkung));
									table.addView(row1);
								}
								table.setGravity(Gravity.CENTER_HORIZONTAL);
								LinearLayout l = new LinearLayout(getActivity());
								l.setGravity(Gravity.CENTER_HORIZONTAL);
								l.addView(table);
								if (!(android.os.Build.VERSION.SDK_INT >= 11)) {
									table.setBackgroundColor(Color.WHITE);
									l.setBackgroundColor(Color.WHITE);

								}
								builder.setView(l);
								builder.setPositiveButton("Ok", null);
								builder.create().show();
							}
						});
						tr.addView(newTextView("X"));
					}
				} else {
					tr.addView(newTextView(v.klausur));
					tr.addView(newTextView(v.bemerkung));
				}

				tr.setBackgroundColor(color.white);

				layoutTable.addView(tr);
			}
			l.addView(layoutTable);
			s.addView(l);
		} else {

			LinearLayout l = new LinearLayout(getActivity());
			l.setLayoutParams(params);
			l.setGravity(Gravity.CENTER_HORIZONTAL);
			l.setOrientation(LinearLayout.VERTICAL);

			TableLayout table = new TableLayout(getActivity());
			table.setGravity(Gravity.CENTER_HORIZONTAL);
			SpannableString spanStand = new SpannableString(stand + ": ");
			spanStand.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanStand.length(), 0);
			TextView tv_stand = new TextView(getActivity());
			tv_stand.setLayoutParams(trparams);
			tv_stand.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			tv_stand.setText(spanStand);

			TableRow tr_stand = new TableRow(getActivity());
			tr_stand.setLayoutParams(trparams);
			tr_stand.addView(tv_stand);

			TextView leer = new TextView(getActivity());
			leer.setText("  ");
			TableRow leereReihe = new TableRow(getActivity());
			leereReihe.addView(leer);

			SpannableString spanNachricht = new SpannableString("Keine entsprechenden Vertretungen gefunden");
			spanNachricht.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanNachricht.length(), 0);
			TextView tv_nachricht = new TextView(getActivity());
			tv_nachricht.setLayoutParams(trparams);
			tv_nachricht.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
			tv_nachricht.setText(spanNachricht);

			TableRow tr_nachricht = new TableRow(getActivity());
			tr_nachricht.setLayoutParams(trparams);
			tr_nachricht.addView(tv_nachricht);

			table.addView(tr_stand);
			table.addView(leereReihe);
			table.addView(tr_nachricht);

			l.addView(table);
			s.addView(l);

		}
	}

	@Override
	protected TextView newTextView(CharSequence text) {
		TextView temp = new TextView(getActivity());
		TableRow.LayoutParams trparams = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		trparams.setMargins(3, 3, 2, 12);
		temp.setLayoutParams(trparams);
		if (displaySize == 2) {
			temp.setTextSize(Constants.TEXTSIZELEHRER + Constants.TEXTSIZEBIGGER);
		} else {
			temp.setTextSize(Constants.TEXTSIZELEHRER);
		}
		temp.setText(text);
		return temp;
	}

}
