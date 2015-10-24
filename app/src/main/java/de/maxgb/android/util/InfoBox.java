package de.maxgb.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;
import android.webkit.WebView;
import de.maxgb.vertretungsplan.R;

/**
 * Allows to easily show an InfoBox/AlertDialog. Designed to show instructions,
 * but only once or when they changed again
 * 
 * @author Max
 * 
 */
public class InfoBox {

	/**
	 * Shows an InfoBox (AlertDialog) with the given instruction if it wasn´t
	 * shown yet
	 * @param settings SharedPref to check if already shown, if null it shown always
	 * @param context
	 *            Context of the application
	 * @param instruction
	 *            Instruction to be shown
	 */
	public static synchronized void showInstructionBox(SharedPreferences settings,
			Context context, Instruction instruction) {
		// get settings to check if instruction was already shown

		if (settings==null||settings.getInt(instruction.activity_name, 0) < instruction.last_change) {
			// instruction is newer than the last shown -> Create a AlertDialog
			// containing an Webview
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(context.getResources().getString(
					R.string.info_box_title));

			builder.setPositiveButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface i, int a) {
				}

			});
			WebView v = new WebView(context);
			v.loadData(instruction.text, "text/html; charset=UTF-8", null);
			builder.setView(v);

			if(android.os.Build.VERSION.SDK_INT<VERSION_CODES.HONEYCOMB){
				builder.setInverseBackgroundForced(true);
			}
			
			AlertDialog dialog = builder.create();
			dialog.show();
			if(settings!=null){
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt(instruction.activity_name, instruction.last_change);
				editor.commit();
			}
		}
	}

	/**
	 * Shows an InfoBox (AlertDialog) with the given title and text
	 * 
	 * @param context
	 *            Context of the application
	 * @param title
	 *            Title of the dialog
	 * @param text
	 *            Text to be shown
	 */
	public static void showInfoBox(Context context, String title, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setPositiveButton("OK", new OnClickListener() {
			@Override
			public void onClick(DialogInterface i, int a) {
			}

		});
		WebView v = new WebView(context);
		v.loadData(text, "text/html; charset=UTF-8", null);
		builder.setView(v);
		
		if(android.os.Build.VERSION.SDK_INT<VERSION_CODES.HONEYCOMB){
			builder.setInverseBackgroundForced(true);
		}
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * Instruction class to store the instructions information, which can be
	 * shown in an InfoBox. Contain title, last change and instruction(in HTML
	 * format)
	 */
	public static class Instruction {
		public final String activity_name;
		public final int last_change;
		public final String text;

		/**
		 * 
		 * @param name Activityname
		 * @param last_change Last change version
		 * @param text text
		 */
		public Instruction(String name, int last_change, String text) {
			this.activity_name = name;
			this.last_change = last_change;
			this.text = text;
		}

	}

}