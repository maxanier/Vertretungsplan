package de.maxgb.vertretungsplan.fragments.stundenplan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.maxgb.vertretungsplan.R;
import de.maxgb.vertretungsplan.fragments.AnzeigeFragment;
import de.maxgb.vertretungsplan.manager.StundenplanManager;
import de.maxgb.vertretungsplan.util.Constants;
import de.maxgb.vertretungsplan.util.Stunde;

public abstract class StundenplanFragment extends AnzeigeFragment implements StundenplanManager.OnUpdateListener{
	
	private final String TAG="StundenplanFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		StundenplanManager.getInstance().registerOnUpdateListener(this);

		View rootView = inflater.inflate(R.layout.fragment_scroll_view,
				container, false);
		super.onCreateView(inflater, container, savedInstanceState);
		
		ScrollView s=(ScrollView)rootView.findViewById(R.id.standard_scroll_view);
		anzeigen(s);
		
		
		return rootView;
	}
	@Override
	public void onDestroy(){
		StundenplanManager.getInstance().unregisterOnUpdateListener(this);
		super.onDestroy();
	}
	@Override
	public void onStundenplanUpdate() {
		ScrollView s=(ScrollView)this.getView().findViewById(R.id.standard_scroll_view);
		anzeigen(s);
		
	}
	
	protected abstract void anzeigen(ScrollView s);
	
	protected void anzeigen(ArrayList<Stunde[]> stundenplan,ScrollView s){
		
		TableLayout table = new TableLayout(getActivity());
		table.addView(newHeadline());
		for(int i=0;i<(StundenplanManager.BEGINN_NACHMITTAG-1+StundenplanManager.ANZAHL_NACHMITTAG);i++){
			View border=new View(getActivity());
			border.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,1));
			border.setBackgroundColor(Color.GRAY);
			table.addView(border);
			
			TableRow stunde=newTableRow();

			
			stunde.addView(newTextView(Integer.toString(i+1)));

			for(int j=0;j<getVisibleDayCount();j++){
				int day=((getDayOfWeek()-1+j)%7)+1;
				if(day!=Calendar.SUNDAY){
					final Stunde st=stundenplan.get(day-1-1)[i];//day(1-7[So-Sa])-1(Sonntag fällt weg)-1(Liste beginnt bei 0)
					
					//Fach
					String f;
					if(getActivity().getSharedPreferences(Constants.PREFS_NAME,0).getBoolean(Constants.SP_KURSE_MIT_NAMEN, false)){
						f=st.getName();
					}
					else{
						f=st.getKurs();
					}
					TextView vf=newTextViewCentered(f);
					
					//Raum
					
					SpannableString r=new SpannableString(st.getRaum());
					r.setSpan(new StyleSpan(Typeface.ITALIC),0,r.length(),0);
					TextView vr=newTextViewCentered(r);
					
					TableRow.LayoutParams params=new TableRow.LayoutParams();
					params.setMargins(3, 3, 2, 5);
					vr.setLayoutParams(params);
					
					TableLayout kurs = new TableLayout(getActivity());
					kurs.setGravity(Gravity.CENTER_HORIZONTAL);
					
					
					

					kurs.addView(vf);
					kurs.addView(vr);
					
					//Uhrzeit
					TextView vz=new TextView(getActivity());
					if(i>=StundenplanManager.BEGINN_NACHMITTAG-1){
						

						vz.setText(st.getUhrzeit());
						
						vz.setLayoutParams(params);
						vz.setTextSize(Constants.TEXTSIZESCHUELER-Constants.TEXTSIZESMALLER);
						
						kurs.addView(vz);
					}
					
					if(st.isModified()){
						vf.setTextColor(Color.RED);
						vr.setTextColor(Color.RED);
						vz.setTextColor(Color.RED);
					
					
						kurs.setOnClickListener(new OnClickListener(){
	
							@Override
							public void onClick(View v) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										getActivity());
								TableLayout t = new TableLayout(
										getActivity());
								TableRow labels = newTableRow();
								TableRow stunde = newTableRow();
								TableRow vertretung = newTableRow();
								String tag=st.getTag();
								try {
									int day = getWeekDayFromString(tag);
									tag=tag.replace(convertToDayString(day), "");
								} catch (IllegalArgumentException e) {
									
								}
								
								labels.addView(newBoldTextView(tag));
								labels.addView(newBoldTextView("Kurs "));
								labels.addView(newBoldTextView("Raum "));
								labels.addView(newBoldTextView("Uhrzeit "));
								labels.addView(newBoldTextView("Art "));
								labels.addView(newBoldTextView("Bemerk."));
								stunde.addView(newTextView("Eigentlich "));
								stunde.addView(newTextView(st.getOldKurs()));
								stunde.addView(newTextView(st.getOldRaum()));
								stunde.addView(newTextView(st.getUhrzeit()));
								vertretung.addView(newTextView("Vertretung "));
								vertretung.addView(newTextView(st.getKurs()));
								vertretung.addView(newTextView(st.getRaum()));
								vertretung.addView(newTextView(""));
								vertretung.addView(newTextView(st.getArt()));
								vertretung.addView(newTextView(st.getKlausur()+" "+st.getBemerkung()));
								
								t.addView(labels);
								t.addView(stunde);
								t.addView(vertretung);
								LinearLayout layout=new LinearLayout(getActivity());
								t.setGravity(Gravity.CENTER_HORIZONTAL);
								layout.setGravity(Gravity.CENTER_HORIZONTAL);
								layout.addView(t);
								if (!(android.os.Build.VERSION.SDK_INT >= 11)) {
									t.setBackgroundColor(Color.WHITE);
									layout.setBackgroundColor(Color.WHITE);

								}
								builder.setView(layout);
								builder.setPositiveButton("Ok", null);
								builder.create().show();
								
							}
							
						});
					}
					
					stunde.addView(kurs);
				}
				
			}
			table.addView(stunde);

		}
		
		
		s.addView(table);
	}
	
	private TableRow newHeadline(){
		TableRow headline = newTableRow();
		SpannableString stunde=new SpannableString("Stunde ");
		stunde.setSpan(new StyleSpan(Typeface.BOLD),0,stunde.length(),0);
		headline.addView(newTextView(stunde));
		
		for(int i=0;i<getVisibleDayCount();i++){
			int day=((getDayOfWeek()-1+i)%7)+1;
			if(day!=Calendar.SUNDAY){
				SpannableString tag;
				if(i==0){
					tag=new SpannableString(" Heute ");
					tag.setSpan(new StyleSpan(Typeface.BOLD),0,tag.length(),0);
				}
				else if(i==1){
					tag=new SpannableString(" Morgen ");
					tag.setSpan(new StyleSpan(Typeface.BOLD),0,tag.length(),0);
				}
				else{
					tag=new SpannableString(" "+convertToDayString(day)+" ");
					tag.setSpan(new StyleSpan(Typeface.BOLD),0,tag.length(),0);
				}
				headline.addView(newTextViewCentered(tag));
			}
		}
		return headline;
	}
	

	
	
	protected int getDayOfWeek(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK); 
	}
	
	protected int getVisibleDayCount(){
		//TODO Abhängig von ScreenSize
		return 4;
	}
	
	protected String convertToDayString(int i){
		switch(i){
		case Calendar.SUNDAY: return "Sonntag";
		case Calendar.MONDAY: return "Montag";
		case Calendar.TUESDAY: return "Dienstag";
		case Calendar.WEDNESDAY: return "Mittwoch";
		case Calendar.THURSDAY: return "Donnerstag";
		case Calendar.FRIDAY: return "Freitag";
		case Calendar.SATURDAY: return "Samstag";
		default: return "";
		}
	}
	protected int getWeekDayFromString(String s) throws IllegalArgumentException{
		s=s.toLowerCase();
		if(s.contains("montag")){
			return Calendar.MONDAY;
		}
		if(s.contains("dienstag")){
			return Calendar.TUESDAY;
		}
		if(s.contains("mittwoch")){
			return Calendar.WEDNESDAY;
		}
		if(s.contains("donnerstag")){
			return Calendar.THURSDAY;
		}
		if(s.contains("freitag")){
			return Calendar.FRIDAY;
		}
		if(s.contains("samstag")){
			return Calendar.SATURDAY;
		}
		throw new IllegalArgumentException("Kein bekannter Tag");
	}
	
	protected int getCurrentStunde(){
		Date current=Calendar.getInstance().getTime();
		Date first=new Date();
		
		
	}
	
	
	
	
}
