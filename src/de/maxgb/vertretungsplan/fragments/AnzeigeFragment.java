package de.maxgb.vertretungsplan.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TableRow;
import android.widget.TextView;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Logger;

public class AnzeigeFragment extends Fragment {
	private final String TAG = "AnzeigeFragment";
	protected int displaySize = 0;// 0=normal,1=very small,2=small,-1=huge
	protected boolean screenSizeSet = false;// Wurde die ScreenSize bereits
											// aktualisiert
	
	protected TextView newBigTextView(CharSequence text) {
		TextView temp = new TextView(getActivity());
		TableRow.LayoutParams trparams = new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		trparams.setMargins(3, 3, 2, 6);
		temp.setLayoutParams(trparams);
		temp.setTextSize(Constants.TEXTSIZESCHUELER+Constants.TEXTSIZEBIGGER);
		temp.setText(text);
		return temp;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) { // first saving my state,
														// so the bundle wont be
														// empty.
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY",
				"WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}
	
	 protected void retrieveScreenSize() {
         WindowManager wm = (WindowManager) getActivity().getSystemService(
                         Context.WINDOW_SERVICE);
         Display display = wm.getDefaultDisplay();
         int width = display.getWidth();
         if (width < Constants.very_smallWidth) {
                 displaySize = 1;
         } else if (width < Constants.smallWidth) {
                 displaySize = 2;
         } else if (width > Constants.largeWidth) {
                 displaySize = -1;
         }
         Logger.i(TAG, "Screen Size: " + displaySize);
 }
	
	protected  void alert(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(msg);
		builder.setPositiveButton("Ok", null);
		builder.create().show();
	}
	
	protected TextView newTextView(CharSequence text) {
		TextView temp = new TextView(getActivity());
		TableRow.LayoutParams trparams = new TableRow.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		trparams.setMargins(3, 3, 2, 11);
		temp.setLayoutParams(trparams);
		
		if (displaySize == 2) {
			temp.setTextSize(Constants.TEXTSIZESCHUELER
					+ Constants.TEXTSIZEBIGGER);
		} else {
			temp.setTextSize(Constants.TEXTSIZESCHUELER);
		}

		temp.setText(text);
		return temp;
	}
	
	protected TextView newTextViewCentered(CharSequence text){
		TextView t = newTextView(text);
		t.setGravity(Gravity.CENTER_HORIZONTAL);
		return t;
	}
	
	protected TextView newBoldTextView(CharSequence text){
		SpannableString t=new SpannableString(text);
		t.setSpan(new StyleSpan(Typeface.BOLD), 0, t.length(), 0);
		return newTextView(t);
	}
	
	protected TableRow newTableRow(){
		TableRow row=new TableRow(getActivity());
		
		
		return row;
	}
	
	
}
