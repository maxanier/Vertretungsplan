package de.maxgb.vertretungsplan;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mobeta.android.dslv.DragSortListView;

import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.manager.TabManager;
import de.maxgb.vertretungsplan.manager.TabManager.TabSelector;
import de.maxgb.vertretungsplan.util.Constants;

public class SelectTabsActivity extends SherlockListActivity {

	private class TabSelectorAdapter extends ArrayAdapter<TabSelector> {
		private List<TabSelector> objects;

		public TabSelectorAdapter(Context context, int resource, int textViewResourceId, List<TabSelector> objects) {
			super(context, resource, textViewResourceId, objects);
			this.objects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.list_item_checkable, parent, false);
			}

			CheckedTextView checkedTextView = (CheckedTextView) row.findViewById(R.id.select_tabs_item_text);
			checkedTextView.setText(tabManager.getTabDescription(objects.get(position)));
			((ListView) parent).setItemChecked(position, objects.get(position).isEnabled());

			return row;
		}

		public void toogleChecked(int position) {
			objects.get(position).toogleEnabled();
		}
	}

	public static ArrayList<TabSelector> createStandardSelection(ArrayList<TabSelector> tabs, SharedPreferences pref) {
		Logger.i(TAG, "Erstelle neue Tabauswahl");

		boolean schueler = pref.getBoolean(Constants.SCHUELER_KEY, false);
		boolean lehrer = pref.getBoolean(Constants.LEHRER_KEY, false);
		boolean oberstufe = pref.getBoolean(Constants.OBERSTUFE_KEY, false);
		boolean stundenplan = true;

		if (schueler) {
			tabs.add(new TabSelector("AllesSchuelerFragment.class", true));
			tabs.add(new TabSelector("StufeSchuelerFragment.class", true));
			if ((android.os.Build.VERSION.SDK_INT >= 11) && oberstufe) {
				tabs.add(new TabSelector("KurseSchuelerFragment.class", false));
			}
			if (stundenplan) {
				tabs.add(new TabSelector("NormalStundenplanFragment.class", false));
				tabs.add(new TabSelector("ModifiedStundenplanFragment.class", true));
			}

		} else if (lehrer) {
			tabs.add(new TabSelector("AllesLehrerFragment.class", true));
			tabs.add(new TabSelector("EigeneLehrerFragment.class", true));
		}
		return tabs;
	}

	private TabSelectorAdapter adapter;

	private TabManager tabManager;

	private final static String TAG = "SelectTabsActivity";

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				DragSortListView list = getListView();
				TabSelector item = adapter.getItem(from);
				adapter.remove(item);
				adapter.insert(item, to);
				list.moveCheckState(from, to);
			}
		}
	};

	@Override
	public DragSortListView getListView() {
		return (DragSortListView) super.getListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.select_tabs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.reset_tab_selection:
			/*
			 * Erstelle neuen Adapter mit Standardauswahl, welcher dann als Listadapter gesetzt wird, also angezeigt wird
			 */
			SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, 0);
			adapter = new TabSelectorAdapter(this, R.layout.list_item_checkable, R.id.select_tabs_item_text,
					createStandardSelection(new ArrayList<TabSelector>(), pref));
			setListAdapter(adapter);
			return true;
		case android.R.id.home:
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_tabs);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, 0);
		tabManager = new TabManager();
		ArrayList<TabSelector> tabs = TabManager.convertToArrayList(pref.getString(Constants.JSON_TABS_KEY, ""));
		/*
		 * Falls noch keine Tabauswahl gespeichert ist oder das auslesen fehlgeschlagen ist, wird anhand der Einstellungen eine
		 * neue Tabauswahl erstellt
		 */
		if (tabs.size() == 0) {
			tabs = createStandardSelection(tabs, pref);

		}

		Logger.i(TAG, "Loading Tabs: " + tabs.toString());
		adapter = new TabSelectorAdapter(this, R.layout.list_item_checkable, R.id.select_tabs_item_text, tabs);

		setListAdapter(adapter);

		OnItemClickListener clickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				adapter.toogleChecked(position);

			}

		};
		getListView().setOnItemClickListener(clickListener);
		getListView().setDropListener(onDrop);

	}

	@Override
	protected void onPause() {
		super.onPause();
		Logger.i(TAG, "Speichere Tabsettings");
		ArrayList<TabSelector> tabs = new ArrayList<TabSelector>();
		for (int i = 0; i < adapter.getCount(); i++) {

			tabs.add(adapter.getItem(i));
		}
		Logger.i(TAG, "Speichere Tabauswahl: " + tabs.toString());
		SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS_NAME, 0).edit();
		editor.putString(Constants.JSON_TABS_KEY, TabManager.convertToString(tabs));// Save the current selection via the
																					// TabManger in SharedPreferences
		editor.commit();

	}

}
