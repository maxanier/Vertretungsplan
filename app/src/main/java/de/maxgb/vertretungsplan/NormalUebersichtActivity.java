package de.maxgb.vertretungsplan;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import android.os.Bundle;
import android.app.Activity;

public class NormalUebersichtActivity extends AppCompatActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_normal_uebersicht);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.normal_uebersicht, menu);
		return true;
	}

}
