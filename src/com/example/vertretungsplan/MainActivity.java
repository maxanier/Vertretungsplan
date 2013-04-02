package com.example.vertretungsplan;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	public static final String TAG = "Main_Acticity";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		UpdateCheck check = new UpdateCheck(this);
		try{PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		Double version = Double.parseDouble(pInfo.versionName);
		Log.i(TAG,"Version: "+version);
		check.execute(version);}
		catch(Exception e){}
		
		//final Button plan_button = (Button) findViewById(R.id.button1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void plan(View v)
	{
		Log.i(TAG,"Plan anzeigen");
		Intent i=new Intent();
		i.setClass(this, Anzeige.class);
		startActivity(i);
	}
	public void optionen(View v)
	{
		Log.i(TAG,"Optionen anzeigen");
		Intent i=new Intent();
		i.setClass(this, Options.class);
		startActivity(i);
	}


}
