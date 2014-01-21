package de.maxgb.vertretungsplan.util;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.manager.TabManager;
import de.maxgb.vertretungsplan.manager.TabManager.TabSelector;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private List<TabSelector> tabList;
	private TabManager tabManager;
	private final String TAG = "SectionsPagerAdapter";

	public SectionsPagerAdapter(FragmentManager fm, String json_tabs) {
		super(fm);
		tabManager = new TabManager();
		setTabs(json_tabs);

	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		FragmentManager manager = ((Fragment) object).getFragmentManager();
		FragmentTransaction trans = manager.beginTransaction();
		trans.remove((Fragment) object);
		trans.commitAllowingStateLoss();
	}

	@Override
	public int getCount() {
		return tabList.size();
	}

	@Override
	public Fragment getItem(int position) {

		Fragment fragment = null;

		try {
			fragment = (Fragment) tabManager.getTabClass(tabList.get(position)).newInstance(); // Creates a new instance of the
																								// specific tab, by getting the
																								// right class from the TabManager
																								// instance
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (fragment == null) {
			fragment = new Fragment();
		}
		return fragment;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabManager.getTabTitle(tabList.get(position));
	}

	public void setTabs(String json_tabs) {
		ArrayList<TabSelector> all_tabs = TabManager.convertToArrayList(json_tabs);
		tabList = new ArrayList<TabSelector>();
		for (int i = 0; i < all_tabs.size(); i++) {
			if (all_tabs.get(i).isEnabled()) {
				tabList.add(all_tabs.get(i));
			}
		}
		if (tabList.size() == 0) {
			tabList.add(new TabSelector("InfoFragment.class", true));
		}
		Logger.i(TAG, "JsonString: " + json_tabs + " TabList: " + tabList.toString());
		notifyDataSetChanged();
	}
}