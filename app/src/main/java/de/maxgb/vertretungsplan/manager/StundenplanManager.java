package de.maxgb.vertretungsplan.manager;

import android.content.Context;
import android.os.AsyncTask;
import de.maxgb.android.util.Logger;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Stunde;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StundenplanManager {
	public static final int BEGINN_NACHMITTAG = 8;
	public static final int ANZAHL_SAMSTAG = 4;
	public static final int ANZAHL_NACHMITTAG = 2;
	private static StundenplanManager instance;

	public static synchronized StundenplanManager getInstance(Context context) {
		if (instance == null) {
			instance = new StundenplanManager(context);
		}
		return instance;
	}
	private final String TAG = "StundenplanManager";
	private int lastResult = 0;
	private ArrayList<Stunde[]> woche;
	private Context context;
	// Listener-------------
	private ArrayList<OnUpdateListener> listener = new ArrayList<OnUpdateListener>();

	private StundenplanManager(Context context) {
		this.context = context;
		auswerten();
	}

	public void asyncAuswerten() {
		AuswertenTask task = new AuswertenTask();
		task.execute();
	}

	public void auswerten() {
		lastResult = dateiAuswerten();
		if (lastResult == -1) {

		} else {
			woche = null;
		}

	}

	public void auswertenWithNotify() {
		auswerten();
		notifyListener();
	}

	public ArrayList<Stunde[]> getClonedStundenplan() {
		if (woche == null) return null;
		ArrayList<Stunde[]> clone;
		try {
			clone = new ArrayList<Stunde[]>(woche.size());

			for (Stunde[] item : woche) {
				Stunde[] clone2 = new Stunde[item.length];
				for (int i = 0; i < item.length; i++) {
					clone2[i] = item[i].clone();
				}
				clone.add(clone2);
			}
			return clone;
		} catch (NullPointerException e) {
			Logger.e(TAG, "Failed to clone stundenplan", e);
			return null;
		}
	}

	public String getLastResult() {
		switch (lastResult) {
		case -1:
			return "Erfolgreich ausgewertet";
		case 1:
			return "Datei existiert nicht";
		case 2:
			return "Kann Datei nicht lesen";
		case 3:
			return "Zugriffsfehler";
		case 4:
			return "Parsingfehler";
		default:
			return "Noch nicht ausgewertet";
		}
	}

	public ArrayList<Stunde[]> getStundenplan() {
		return woche;
	}

	public void notifyListener() {
		for (int i = 0; i < listener.size(); i++) {
			if (listener.get(i) != null) {
				listener.get(i).onStundenplanUpdate();
			}
		}
	}

	public void registerOnUpdateListener(OnUpdateListener listener) {
		this.listener.add(listener);
	}

	public void unregisterOnUpdateListener(OnUpdateListener listener) {
		this.listener.remove(listener);
	}

	private Stunde[] convertJSONArrayToStundenArray(JSONArray tag) throws JSONException {
		Stunde[] result = new Stunde[BEGINN_NACHMITTAG - 1 + ANZAHL_NACHMITTAG];

		for (int i = 0; i < BEGINN_NACHMITTAG - 1 + ANZAHL_NACHMITTAG; i++) {
			JSONArray stunde = tag.getJSONArray(i);

			if (i >= BEGINN_NACHMITTAG - 1) {
				result[i] = new Stunde(stunde.getString(0), stunde.getString(1), i + 1, stunde.getString(2));
			} else {
				result[i] = new Stunde(stunde.getString(0), stunde.getString(1), i + 1);
			}
		}
		return result;
	}

	/**
	 * Wertet die Stundenplandatei aus
	 *
	 * @return Fehlercode -1 bei Erfolg,1 Datei existiert nicht, 2 Datei kann nicht gelesen werden,3 Fehler beim Lesen,4 Fehler
	 *         beim Parsen
	 *
	 */
	private int dateiAuswerten() {
		File loadoutFile = new File(context.getFilesDir(), Constants.SP_FILE_NAME);
		ArrayList<Stunde[]> w = new ArrayList<Stunde[]>();
		if (!loadoutFile.exists()) {
			Logger.w(TAG, "Stundenplan file doesn´t exist");
			return 1;
		}
		if (!loadoutFile.canRead()) {
			Logger.w(TAG, "Can´t read Stundenplan file");
			return 2;
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(loadoutFile));

			String line = br.readLine();
			br.close();
			JSONObject stundenplan = new JSONObject(line);
			JSONArray mo = stundenplan.getJSONArray("mo");
			JSONArray di = stundenplan.getJSONArray("di");
			JSONArray mi = stundenplan.getJSONArray("mi");
			JSONArray d = stundenplan.getJSONArray("do");
			JSONArray fr = stundenplan.getJSONArray("fr");
			JSONObject sa = stundenplan.getJSONObject("sa");

			// Samstag
			Stunde[] samstag = new Stunde[9];
			JSONArray eins = sa.getJSONArray("0");
			JSONArray zwei = sa.getJSONArray("1");
			JSONArray drei = sa.getJSONArray("2");
			JSONArray vier = sa.getJSONArray("3");
			JSONArray acht = sa.getJSONArray("7");
			JSONArray neun = sa.getJSONArray("8");

			samstag[0] = new Stunde(eins.getString(0), eins.getString(1), 1);
			samstag[1] = new Stunde(zwei.getString(0), zwei.getString(1), 2);
			samstag[2] = new Stunde(drei.getString(0), drei.getString(1), 3);
			samstag[3] = new Stunde(vier.getString(0), vier.getString(1), 4);
			samstag[4] = new Stunde("", "", 5);
			samstag[5] = new Stunde("", "", 6);
			samstag[6] = new Stunde("", "", 7);
			samstag[7] = new Stunde(acht.getString(0), acht.getString(1), 8, acht.getString(2));
			samstag[8] = new Stunde(neun.getString(0), neun.getString(1), 9, neun.getString(2));

			w.add(convertJSONArrayToStundenArray(mo));
			w.add(convertJSONArrayToStundenArray(di));
			w.add(convertJSONArrayToStundenArray(mi));
			w.add(convertJSONArrayToStundenArray(d));
			w.add(convertJSONArrayToStundenArray(fr));
			w.add(samstag);

			/*
			 * for(int i=0;i<w.size();i++){ for(int j=0;j<w.get(i).length;j++){ System.out.println(w.get(i)[j].toString()); } }
			 */

		} catch (IOException e) {
			Logger.e(TAG, "Fehler beim Lesen der Datei", e);
			return 3;
		} catch (JSONException e) {
			Logger.e(TAG, "Fehler beim Parsen der Datei", e);
			return 4;
		}
		woche = w;
		return -1;
	}

	public interface OnUpdateListener {
		void onStundenplanUpdate();
	}

	// ------------------------
	private class AuswertenTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			auswerten();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			notifyListener();

		}
	}
}
