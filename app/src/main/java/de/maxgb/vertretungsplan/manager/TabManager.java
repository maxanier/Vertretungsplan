package de.maxgb.vertretungsplan.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import de.maxgb.vertretungsplan.fragments.InfoFragment;
import de.maxgb.vertretungsplan.fragments.lehrer.AllesLehrerFragment;
import de.maxgb.vertretungsplan.fragments.lehrer.EigeneLehrerFragment;
import de.maxgb.vertretungsplan.fragments.schueler.AllesSchuelerFragment;
import de.maxgb.vertretungsplan.fragments.schueler.KurseSchuelerFragment;
import de.maxgb.vertretungsplan.fragments.schueler.StufeSchuelerFragment;
import de.maxgb.vertretungsplan.fragments.stundenplan.ModifiedStundenplanFragment;
import de.maxgb.vertretungsplan.fragments.stundenplan.NormalStundenplanFragment;
import de.maxgb.vertretungsplan.fragments.stundenplan.NormalUebersichtFragment;

public class TabManager {

	public static class TabSelector {
		private String typ;
		private boolean enabled;

		public TabSelector(String typ, boolean enabled) {
			this.typ = typ;
			this.enabled = enabled;
		}

		public String getTyp() {
			return typ;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void toogleEnabled() {
			enabled = !enabled;
		}

		@Override
		public String toString() {
			return "Typ: " + typ + "-" + enabled;
		}

	}

	private enum FragmentTab {
		LEHRERALLE(AllesLehrerFragment.class, "Alle", "Alle Vertretungen"), LEHREREIGENE(EigeneLehrerFragment.class,
				"Eigene Kurse", "Vertretungen für eigene Kurse"), SCHUELERALLE(AllesSchuelerFragment.class, "Alle",
				"Alle Vertretungen"), SCHUELERSTUFE(StufeSchuelerFragment.class, "Stufe", "Vertretungen für Stufe"), SCHUELEREIGENE(
				KurseSchuelerFragment.class, "Eigene Kurse", "Vertretungen für eigene Kurse"), INFO(InfoFragment.class,
				"Info", ""), SPNORMAL(NormalStundenplanFragment.class, "Stundenplan", "Normaler Studenplan"), SPMODIFIED(
				ModifiedStundenplanFragment.class, "SP+VP", "Stundenplan incl. Vertretungen");

		private Class fragmentClass;

		private String title;

		private String description;

		FragmentTab(Class fragmentClass, String title, String description) {
			this.fragmentClass = fragmentClass;
			this.title = title;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public Class getFragmentClass() {
			return fragmentClass;
		}

		public String getTitle() {
			return title;
		}
	}

	public static ArrayList<TabSelector> convertToArrayList(String json_tabs_string) {
		ArrayList<TabSelector> tabs = new ArrayList<TabSelector>();
		try {
			JSONArray json_tabs = new JSONArray(json_tabs_string);
			for (int i = 0; i < json_tabs.length(); i++) {
				JSONArray tab = json_tabs.getJSONArray(i);
				tabs.add(new TabSelector(tab.getString(0), tab.getBoolean(1)));
			}

		} catch (JSONException e) {
		}
		return tabs;
	}

	public static String convertToString(ArrayList<TabSelector> tabs) {
		JSONArray json_tabs = new JSONArray();
		for (int i = 0; i < tabs.size(); i++) {
			JSONArray tab = new JSONArray();
			tab.put(tabs.get(i).getTyp());
			tab.put(tabs.get(i).isEnabled());
			json_tabs.put(tab);
		}
		return json_tabs.toString();
	}

	private HashMap<String, FragmentTab> map;

	public TabManager() {
		map = new HashMap<String, FragmentTab>();
		map.put("AllesLehrerFragment.class", FragmentTab.LEHRERALLE);
		map.put("EigeneLehrerFragment.class", FragmentTab.LEHREREIGENE);
		map.put("AllesSchuelerFragment.class", FragmentTab.SCHUELERALLE);
		map.put("StufeSchuelerFragment.class", FragmentTab.SCHUELERSTUFE);
		map.put("KurseSchuelerFragment.class", FragmentTab.SCHUELEREIGENE);
		map.put("InfoFragment.class", FragmentTab.INFO);
		map.put("NormalStundenplanFragment.class", FragmentTab.SPNORMAL);
		map.put("ModifiedStundenplanFragment.class", FragmentTab.SPMODIFIED);
	}

	public Class getTabClass(TabSelector tab) {
		return map.get(tab.getTyp()).getFragmentClass();
	}

	public String getTabDescription(TabSelector tab) {
		return map.get(tab.getTyp()).getDescription();
	}

	public String getTabTitle(TabSelector tab) {
		return map.get(tab.getTyp()).getTitle();
	}
}
