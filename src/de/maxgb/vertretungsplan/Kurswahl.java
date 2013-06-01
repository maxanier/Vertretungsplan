package de.maxgb.vertretungsplan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Kurswahl extends FragmentActivity implements
		KursEingabeDialog.NoticeDialogListener {

	public static final String PREFS_NAME = "Einstellungen";
	public static final String TAG = "Kurswahl_Activity";
	public static final float LETZE_AENDERUNG = (float)1.5;
	public static final String ANLEITUNG = "<html><body>Füge hier deine Kurse in Form ihrer Abkürzung, wie sie auch auf dem Vertretungsplan erscheinen hinzu (z.B. 'PH1').<br>Durch langes Drücken auf einen Kurs entfernst du ihn</body></html>";
	private Set<String> kurse;
	private ArrayList<String> kurse_liste;
	private ListView liste;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kurswahl);
		
		//Settings laden sofern mindestens SDK 11, sonst beenden
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			kurse = settings.getStringSet("kurse", new TreeSet<String>());
		}
		else {
			finish();
		}
		
		//Gespeichertes Kurs-Set in eine Liste umwandeln
		kurse_liste = new ArrayList<String>();
		String[] kurse_array = kurse.toArray(new String[0]);
		for(int i=0;i<kurse_array.length;i++){
			kurse_liste.add(kurse_array[i]);
		}
		
		//ListView per Adapter befüllen
		liste = (ListView) findViewById(R.id.liste);
		final ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				kurse_liste);
		liste.setAdapter(adapter);
		
		//ListView LongClick-Eigenschaft: Item entfernen
		liste.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View v, int position, long id){
				final String item = (String) parent.getItemAtPosition(position);
				v.animate().setDuration(1000).alpha(0)
				.withEndAction(new Runnable(){
					@Override
					public void run(){
						kurse_liste.remove(item);
						adapter.notifyDataSetChanged();
						v.setAlpha(1);
					}
				});
				return true;
			}
		});
		
		new InfoBox(this,TAG,LETZE_AENDERUNG,ANLEITUNG);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kurswahl, menu);
		return true;
	}

	@Override
	public void onDialogPositiveClick(String kurs) {
		kurse_liste.add(kurs.trim());
		((ArrayAdapter)liste.getAdapter()).notifyDataSetChanged();
		
	}

	@SuppressLint("NewApi")
	@Override
	public void onPause(){
		super.onPause();
		if (android.os.Build.VERSION.SDK_INT >= 11) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putStringSet("kurse", new TreeSet<String>(kurse_liste));
		editor.commit();}
		
	}
	
	
	public void kursHinzufuegen(View v) {
		DialogFragment dialog = new KursEingabeDialog();
		dialog.show(getSupportFragmentManager(), "Kurs Eingabe");
	}
	
	
	
}
