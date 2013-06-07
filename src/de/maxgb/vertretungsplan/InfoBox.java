package de.maxgb.vertretungsplan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.webkit.WebView;


public class InfoBox {
	public static final String PREFS_NAME = "InfoBox";
	
	public InfoBox(Context context, String activity_name, float letzte_aenderung, String titel, String anleitung){
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		if(settings.getFloat(activity_name, 0)<letzte_aenderung){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(titel);
			builder.setPositiveButton("Ok",new OnClickListener(){
				@Override
				public void onClick(DialogInterface i, int a){}
				
			});
			WebView v = new WebView(context);
			v.loadData(anleitung, "text/html; charset=UTF-8", null);
			builder.setView(v);
			
			AlertDialog dialog = builder.create();
			dialog.show();
			SharedPreferences.Editor editor = settings.edit();
			editor.putFloat(activity_name, letzte_aenderung);
			editor.commit();
		}
	}

}
