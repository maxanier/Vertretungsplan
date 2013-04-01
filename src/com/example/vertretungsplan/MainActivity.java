package com.example.vertretungsplan;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		Intent i=new Intent();
		i.setClass(this, Anzeige.class);
		startActivity(i);
	}
	public void optionen(View v)
	{
		Intent i=new Intent();
		i.setClass(this, Options.class);
		startActivity(i);
	}

}
