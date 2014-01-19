package de.maxgb.vertretungsplan;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import de.maxgb.vertretungsplan.manager.StundenplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Logger;

public class StundenplanOptionsActivity extends Activity {
	
	private IInAppBillingService mService;
	private final String TAG="Stundenplan_options";
	private final int STUNDENPLAN_KAUF_REQUEST_CODE=551;
	private ProgressDialog progressDialog;
	private EditText edit_id;
	private CheckBox checkBox_kurse_with_namen;
	private boolean old_kurse_mit_namen;
	private boolean billingService_bound=false;

	private final ServiceConnection mServiceConn = new ServiceConnection() {
		   @Override
		   public void onServiceDisconnected(ComponentName name) {
		       mService = null;
		   }

		   @Override
		   public void onServiceConnected(ComponentName name, 
		      IBinder service) {
		       mService = IInAppBillingService.Stub.asInterface(service);
		   }
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    
	    SharedPreferences pref=getSharedPreferences(Constants.PREFS_NAME,0);
	    if(pref.getBoolean(Constants.SP_GEKAUFT,false)||true){ //TODO REMOVE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	    	loadLayout(true);
	    }
	    else{
			bindService(new 
			        Intent("com.android.vending.billing.InAppBillingService.BIND"),
			                mServiceConn, Context.BIND_AUTO_CREATE);
			billingService_bound=true;
	    	new IstGekauftTask().execute();
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stundenplan_options, menu);
		return true;
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (mServiceConn != null&&billingService_bound) {
	        unbindService(mServiceConn);
	    }   
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	   if (requestCode == STUNDENPLAN_KAUF_REQUEST_CODE) {           
	      int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
	      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
	      String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
	        
	      if (resultCode == RESULT_OK) {
	         SharedPreferences pref= getSharedPreferences(Constants.PREFS_NAME,0);
	         pref.edit().putBoolean(Constants.SP_GEKAUFT,true).commit();
	         loadLayout(true);
	      }
	      else if(resultCode == 1){
	    	  alert("Kauf abgebrochen");
	      }
	      else if(resultCode== 4){
	    	  alert("Item nicht verfügbar");
	    	  
	      }
	      else if(resultCode == 7){
	    	  alert("Bereits gekauft");
	    	  SharedPreferences pref= getSharedPreferences(Constants.PREFS_NAME,0);
		      pref.edit().putBoolean(Constants.SP_GEKAUFT,true).commit();
		      loadLayout(true);
	      }
	      else{
	    	  alert("Kauf mit resultCode: "+resultCode+" fehlgeschlagen");
	      }
	   }
	}
	
	private void loadLayout(boolean gekauft){
		if(gekauft){
			setContentView(R.layout.activity_stundenplan_options_gekauft);
			SharedPreferences pref=getSharedPreferences(Constants.PREFS_NAME,0);
			edit_id=(EditText)findViewById(R.id.edit_stundenplan_id);
			checkBox_kurse_with_namen=(CheckBox)findViewById(R.id.checkBox_stundenplan_with_name);
			edit_id.setText(Integer.toString(pref.getInt(Constants.SP_ID,0)));
			checkBox_kurse_with_namen.setChecked(pref.getBoolean(Constants.SP_KURSE_MIT_NAMEN,false));
			old_kurse_mit_namen=pref.getBoolean(Constants.SP_KURSE_MIT_NAMEN,false);
			if(!pref.getBoolean(Constants.OBERSTUFE_KEY, false)){
				checkBox_kurse_with_namen.setVisibility(View.INVISIBLE);
				checkBox_kurse_with_namen.setEnabled(false);
				
			}
		}
		else{
			setContentView(R.layout.activity_stundenplan_options_nicht_gekauft);
			
		}
	}
	public void stundenplanKaufen(View v){
		stundenplanKaufen();
	}
	
	public void fertig(View v){
		SharedPreferences.Editor editor=getSharedPreferences(Constants.PREFS_NAME,0).edit();
		editor.putBoolean(Constants.SP_KURSE_MIT_NAMEN,checkBox_kurse_with_namen.isChecked());
		try {
			editor.putInt(Constants.SP_ID, Integer.parseInt(edit_id.getText().toString()));
		} catch (NumberFormatException e) {
			Logger.w(TAG, "Id Feld fehlerhaft oder leer");
		}
		editor.putBoolean(Constants.SP_KURSE_MIT_NAMEN, checkBox_kurse_with_namen.isChecked());
		editor.commit();
		if(old_kurse_mit_namen!=checkBox_kurse_with_namen.isChecked()){
			StundenplanManager.getInstance().notifyListener();
		}
		finish();
	}
	
	public void sp_herunterladen(View v){
		DownloadPlanTask task=new DownloadPlanTask();
		try {
			task.execute(Integer.parseInt(edit_id.getText().toString()));
		} catch (NumberFormatException e) {
			alert("Bitte eine ID(nur aus Zahlen bestehend) eingeben");
		}
	}
	
	private class DownloadPlanTask extends AsyncTask<Integer,Void,String>{
		@Override
		protected void onPreExecute(){
			if(!isOnline()){
				alert("Keine Internetverbindung");
			}
			Log.i(TAG,"Show ProgressDialog");
			progressDialog = new ProgressDialog(StundenplanOptionsActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle("Saving");
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}
		
		/**
		 * Downloads stundenplan
		 * @param params the stundenplan id
		 * @return Error message, null if no error occured
		 */
		@Override
		protected String doInBackground(Integer... params) {
			int id=params[0];
			Logger.i(TAG, "Anfrage gestartet");
			final HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					Constants.CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams,
					Constants.CONNECTION_TIMEOUT);
			HttpClient httpclient = new DefaultHttpClient(
					httpParams); // neuer Httpclient mit definierter Timeout Zeit
																	
			try {
				HttpResponse response = httpclient.execute(new HttpGet(
						Constants.SP_GET_PLAN_URL+id));
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					Logger.i(TAG, "Stundenplan Anfrage erfolgreich");
					
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out); // Schreiben der Loginseite
														// in den
														// ByteArrayOutputStream
					out.close();
					String responseString = out.toString(); // Umwandlung des
															// ByteArrayOutputStream(Loginseite)
															// in einen String
					if(responseString.contains("ERROR")){
						return responseString;
						
					}
					JSONObject json=new JSONObject(responseString);
					save(json.toString(),Constants.SP_FILE_NAME);
					
					return null;
					
				}
				else{
					Logger.w(TAG,"Fehler beim Herunterladen Http Status: "+response.getStatusLine().toString());
					return ("Fehler beim Herunterladen Http StatusCode: "+response.getStatusLine().getStatusCode()+" Url:" +Constants.SP_GET_PLAN_URL+id);
					
				}
			} catch (ClientProtocolException e) {
				Logger.e(TAG,"Fehler beim Plan herunterladen",e);
			} catch (IOException e) {
				Logger.e(TAG,"Fehler beim Plan herunterladen",e);
			} catch (JSONException e) {
				Logger.e(TAG,"Fehler beim Plan herunterladen. Konnte Plan nicht Parsen",e);
				return ("Fehler beim Herunterladen. Konnte Plan nicht parsen");
				
			}													
			
			return "Fehler beim Herunterladen";
		}
		@Override
		protected void onPostExecute(String result){
			if(result!=null){
				alert(result);
			}
			else{
				Toast.makeText(getApplicationContext(), "Erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
				StundenplanManager.getInstance().auswertenWithNotify();
			}
			progressDialog.dismiss();
			progressDialog=null;
			
		}
	}
	
	//Kauf Methoden
	private class IstGekauftTask extends AsyncTask<Void,Void,Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			while(mService==null){
				
			}
			return gekauft();
		}
		
		
		@Override
		protected void onPreExecute() {
			// Neuer progress dialog
			progressDialog = new ProgressDialog(StundenplanOptionsActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setTitle("Lädt...");
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();

		}
		
		@Override
		protected void onPostExecute(Boolean result){
			
			if(result){
				SharedPreferences pref= getSharedPreferences(Constants.PREFS_NAME,0);
			    pref.edit().putBoolean(Constants.SP_GEKAUFT,true).commit();
			    loadLayout(true);
			}
			else{
				loadLayout(false);
			}
			progressDialog.dismiss();
		}
	}
	
	private boolean gekauft(){
		
		//refer to http://developer.android.com/google/play/billing/billing_integrate.html
		
		Bundle ownedItems;
		try {
			ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
		} catch (RemoteException e) {
			
			Logger.e(TAG, "Failed to receive owned Items from Google Play",e);
			return false;
		}
		
		int response = ownedItems.getInt("RESPONSE_CODE");
		if (response == 0) {
		   ArrayList<String> ownedSkus = 
		      ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
		  
		   
		   if(ownedSkus.contains("stundenplan")){
			   return true;
		   }
		}

		
		return false;
	}
	
	private void stundenplanKaufen(){

		String sku ="android.test.purchased";
		try {
			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					   sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
			startIntentSenderForResult(pendingIntent.getIntentSender(),
					   STUNDENPLAN_KAUF_REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
					   Integer.valueOf(0));
			
		} catch (Exception e) {
			Logger.e(TAG, "Faield to get buyIntent",e);
			alert("Kann keine Verbindung zu Google Play aufbauen");
		}
		
	}
	
	
	//------------------------------------------------------------------------
	private void alert(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setPositiveButton("Ok", null);
		builder.create().show();
	}
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) 
				getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		return false;
	}
	public void save(String s, String file) throws IOException {
		Logger.i(TAG, "Speichern der Datei: " + file + " gestartet");
		FileWriter o = new FileWriter(Environment.getExternalStorageDirectory()
				.getPath() + "/vertretungsplan/" + file, false);
		BufferedWriter bw = new BufferedWriter(o);
		bw.write(s);
		bw.close();
		o.close();
		Logger.i(TAG, "Speichern der Datei: " + file + " abgeschloï¿½en");

	}
}
