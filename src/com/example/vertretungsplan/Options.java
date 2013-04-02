package com.example.vertretungsplan;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.app.AlertDialog;

public class Options extends Activity {
	public static final String PREFS_NAME = "Einstellungen";
	public static final String TAG = "Options_Activity";
	private String username="";
	private String password="";
	private String klasse="";
	private TextView klasse_eingabe;
	private TextView password_eingabe;
	private TextView username_eingabe;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		username_eingabe = (TextView)findViewById(R.id.editText1);
		password_eingabe = (TextView)findViewById(R.id.editText2);
		klasse_eingabe = (TextView)findViewById(R.id.editText3);
		
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
		username=settings.getString("username","");
		password=settings.getString("password","");
		klasse=settings.getString("klasse", "");
		
		username_eingabe.setText(username);
		password_eingabe.setText(password);
		klasse_eingabe.setText(klasse);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
		return true;
	}
	public void speichern(View v)
	{
		System.out.println("Speichervorgang");
		String temp_username=username_eingabe.getText().toString().trim().toLowerCase();
		String temp_password=password_eingabe.getText().toString().trim();
		String temp_klasse=klasse_eingabe.getText().toString().trim().toUpperCase();
		
		if(!temp_username.trim().isEmpty()&&!temp_password.trim().isEmpty()&&!temp_klasse.trim().isEmpty())
		{
			System.out.println("speichern");
			SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("username",temp_username);
			editor.putString("password",temp_password);
			editor.putString("klasse",temp_klasse);
			editor.commit();
			Log.i(TAG,"Einstellungen gespeichert");
			finish();
		}
		else{
			Log.i(TAG,"Einstellungen nicht ausreichend ausgefüllt");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Bitte alle Felder ausfï¿½llen").setTitle("Fehler");
			builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id){}});
			
			
			AlertDialog dialog = builder.create();
			dialog.show();
			
		}
	}
	public void abbrechen(View v){
		System.out.println("Abbrechen");
		finish();
	}

}
