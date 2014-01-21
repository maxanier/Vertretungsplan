package de.maxgb.vertretungsplan.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
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
import android.widget.Toast;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Logger;

public class AnzeigeFragment extends Fragment {
	private final String TAG = "AnzeigeFragment";
	/**
	 * DiplaySize: 0=Normal //3 Tage 1=VerySmall //3 Tage 2=Small //3 Tage -1=Big //4 Tage -2=Very Big //7 Tage
	 */
	protected int displaySize = 0;
	protected boolean screenSizeSet = false;// Wurde die ScreenSize bereits
											// aktualisiert

	@Override
	public void onSaveInstanceState(Bundle outState) { // first saving my state,
														// so the bundle wont be
														// empty.
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}

	protected void alert(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(msg);
		builder.setPositiveButton("Ok", null);
		builder.create().show();
	}

	protected TextView newBigTextView(CharSequence text) {
		TextView temp = new TextView(getActivity());
		TableRow.LayoutParams trparams = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		trparams.setMargins(3, 3, 2, 6);
		temp.setLayoutParams(trparams);
		temp.setTextSize(Constants.TEXTSIZESCHUELER + Constants.TEXTSIZEBIGGER);
		temp.setText(text);
		return temp;
	}

	protected TextView newBoldTextView(CharSequence text) {
		SpannableString t = new SpannableString(text);
		t.setSpan(new StyleSpan(Typeface.BOLD), 0, t.length(), 0);
		return newTextView(t);
	}

	protected TableRow newTableRow() {
		TableRow row = new TableRow(getActivity());

		return row;
	}

	protected TextView newTextView(CharSequence text) {
		TextView temp = new TextView(getActivity());
		TableRow.LayoutParams trparams = new TableRow.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		trparams.setMargins(3, 3, 2, 11);
		temp.setLayoutParams(trparams);

		if (displaySize == 2) {
			temp.setTextSize(Constants.TEXTSIZESCHUELER + Constants.TEXTSIZEBIGGER);
		} else {
			temp.setTextSize(Constants.TEXTSIZESCHUELER);
		}

		temp.setText(text);
		return temp;
	}

	protected TextView newTextViewCentered(CharSequence text) {
		TextView t = newTextView(text);
		t.setGravity(Gravity.CENTER_HORIZONTAL);
		return t;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	protected void retrieveScreenSize() {

		//@formatter:off
		/*
		 * Testing purpose
		 * 
		//Show Screeninfo
	    //Determine screen size
	    if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {     
	        Toast.makeText(getActivity(), "Large screen",Toast.LENGTH_LONG).show();

	    }
	    else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {     
	        Toast.makeText(getActivity(), "Normal sized screen" , Toast.LENGTH_LONG).show();

	    } 
	    else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {     
	        Toast.makeText(getActivity(), "Small sized screen" , Toast.LENGTH_LONG).show();
	    }
	    else {
	        Toast.makeText(getActivity(), "Screen size is neither large, normal or small" , Toast.LENGTH_LONG).show();
	    }




	    //Determine density
	    DisplayMetrics metrics = new DisplayMetrics();
	    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
	   
	        int density = metrics.densityDpi;

	        if (density==DisplayMetrics.DENSITY_HIGH) {
	            Toast.makeText(getActivity(), "DENSITY_HIGH... Density is " + String.valueOf(density),  Toast.LENGTH_LONG).show();
	        }
	        else if (density==DisplayMetrics.DENSITY_MEDIUM) {
	            Toast.makeText(getActivity(), "DENSITY_MEDIUM... Density is " + String.valueOf(density),  Toast.LENGTH_LONG).show();
	        }
	        else if (density==DisplayMetrics.DENSITY_LOW) {
	            Toast.makeText(getActivity(), "DENSITY_LOW... Density is " + String.valueOf(density),  Toast.LENGTH_LONG).show();
	        }
	        else {
	            Toast.makeText(getActivity(), "Density is neither HIGH, MEDIUM OR LOW.  Density is " + String.valueOf(density),  Toast.LENGTH_LONG).show();
	        }
	        
	        
	        */
	        
         WindowManager wm = (WindowManager) getActivity().getSystemService(
                         Context.WINDOW_SERVICE);
         
         int width;
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
        	 Point p=new Point();
        	 wm.getDefaultDisplay().getSize(p);
        	 width = p.x;
         }
         else{
        	 width=wm.getDefaultDisplay().getWidth();
         }
         
         
         if (width < Constants.very_smallWidth) {
                 displaySize = 1;
         } else if (width < Constants.smallWidth) {
                 displaySize = 2;
         } else if (width < Constants.largeWidth) {
                 displaySize = 0;
         }
         else if(width <Constants.ultra_lageWidth){
        	 displaySize=-1;
         }
         else{
        	 displaySize=-2;
         }
         screenSizeSet=true;
         Logger.i(TAG, "Screen Size: " + displaySize);
 }
	
	
}
