package de.maxgb.vertretungsplan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.com.DownloadTask;
import de.maxgb.vertretungsplan.manager.VertretungsplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.InfoBox;
import de.maxgb.vertretungsplan.util.SectionsPagerAdapter;

/**
 * Hauptklasse, mit einer SherlockActionbar Dient zum Fragmentmanagment und zur Verarbeitung von Nutzereingaben
 * 
 * @author Max Becker
 * 
 * 
 */
public class AnzeigeActivity extends SherlockFragmentActivity implements ActionBar.TabListener,
		VertretungsplanManager.OnUpdateFinishedListener, DownloadTask.DownloadFinishedListener {

	private static final String STATE_SELECTED_NAVIGATION_TAB = "selected_navigation_tab";
	private static final String TAG = "Anzeige";
	private static final int SHOW_OPTIONS_REQUEST = 0;
	private ActionBar actionBar;
	private Menu optionsMenu;
	ViewPager mViewPager;
	SectionsPagerAdapter mSectionsPagerAdapter;

	public void fehler(Exception e) {

		Logger.e(TAG, "Fehler", e);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(e.getMessage()).setTitle("Fehler");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.optionsMenu = menu;
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.anzeige, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			updateAll();

			break;
		case R.id.action_settings:
			Intent i = new Intent();
			i.setClass(this, OptionsActivity.class);
			Logger.i(TAG, "Options started");
			startActivityForResult(i, SHOW_OPTIONS_REQUEST);
			Logger.i(TAG, "Options closed");
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_TAB) && savedInstanceState.containsKey("TabCount")) {
			if (savedInstanceState.getInt("TabCount") == getSupportActionBar().getTabCount()
					&& getSupportActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS) {
				getSupportActionBar().setSelectedNavigationItem(
						savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_TAB));
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt("TabCount", getSupportActionBar().getTabCount());
		outState.putInt(STATE_SELECTED_NAVIGATION_TAB, getSupportActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	/**
	 * Implements OptionsActivity.OnUpdateFinishedListener Setzt den Status des Refresh-Icons auf false, wenn ein Update
	 * abgeschlossen ist
	 */
	@Override
	public void onVertretungsplanUpdateFinished(boolean update) {
		Logger.i(TAG, "Received OnVertretungsplanUpdateFinished Notification");
		setRefreshActionButtonState(false);
		if (update) {
			Toast.makeText(this, "Aktualisiert", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Keine Änderung", Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Setzt Status des Refresh-Icons
	 * 
	 * @param refreshing
	 *            active or inactive
	 * @see http://www.michenux.net/android-refresh-item-action-bar-circular-progressbar-578.html
	 */
	public void setRefreshActionButtonState(final boolean refreshing) {

		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu.findItem(R.id.action_refresh);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.progressbar);
					refreshItem.expandActionView();
				} else {
					refreshItem.setActionView(null);
				}
			}
		}
	}

	// Update Methoden
	public void updateAll() {
		setRefreshActionButtonState(true);
		DownloadTask task = new DownloadTask(getSharedPreferences(Constants.PREFS_NAME, 0), this, this);
		task.execute();
	}

	public void updateTabs() {
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, 0);
		// int count = actionBar.getTabCount();
		// int selected = actionBar.getSelectedNavigationIndex();

		actionBar.removeAllTabs();
		mSectionsPagerAdapter.setTabs(pref.getString(Constants.JSON_TABS_KEY, ""));

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
		if (actionBar.getTabCount() == 1) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		setRefreshActionButtonState(false);
	}

	public void updateVertretungsplanAuswertung() {
		setRefreshActionButtonState(true);
		SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, 0);
		VertretungsplanManager.getInstance(pref.getBoolean(Constants.SCHUELER_KEY, false),
				pref.getBoolean(Constants.LEHRER_KEY, false)).asyncAuswerten();
		InfoBox.showAnleitungBox(this, InfoBox.Anleitungen.ANZEIGEINFO);
	}

	// Sonstige

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Logger.i(TAG, "Receiving result " + resultCode);
		if (requestCode == SHOW_OPTIONS_REQUEST) {
			if (resultCode == OptionsActivity.UPDATE_TABS) {
				updateTabs();
			} else if (resultCode == OptionsActivity.UPDATE_VP) {
				updateAll();
			} else if (resultCode == OptionsActivity.UPDATE_ALL) {
				updateTabs();
				updateAll();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anzeige);

		Logger.init(Constants.PLAN_DIRECTORY);

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, 0);
		Logger.setDebugMode(pref.getBoolean(Constants.DEBUG_KEY, false));

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), pref.getString(
				Constants.JSON_TABS_KEY, ""));

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (actionBar.getTabCount() > 0) {
					actionBar.setSelectedNavigationItem(position);
				}
			}
		});

		// Register Listener
		VertretungsplanManager.getInstance(pref.getBoolean(Constants.SCHUELER_KEY, false),
				pref.getBoolean(Constants.LEHRER_KEY, false)).registerOnUpdateFinishedListener(this);
		updateTabs();

	}

	@Override
	protected void onDestroy() {

		try {
			VertretungsplanManager.getCreatedInstance().unregisterOnUpdateFinishedListener(this);
		} catch (NullPointerException e) {

		}
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();

		// Check if Stundenplan is too old
		if (System.currentTimeMillis()
				- getSharedPreferences(Constants.PREFS_NAME, 0).getLong(Constants.REFRESH_TIME_KEY, 0) > Constants.REFRESH_DIFF) {
			Logger.i(TAG, "Last refresh is to old -> Refreshing");
			updateAll();
		}
	}

	@Override
	public void onDownloadSuccesfullyFinished() {
		updateVertretungsplanAuswertung();

	}

	@Override
	public void onDownloadFailed(String errortext) {
		Toast.makeText(this, errortext, Toast.LENGTH_SHORT).show();
		setRefreshActionButtonState(false);

	}

}
