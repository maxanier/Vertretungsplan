package com.example.vertretungsplan;

public class Vertretung {
	public String klasse,art,fach,stunde,raum,tag;
	public Vertretung(String klasse,String stunde, String art, String fach, String raum,String tag)
	{
		this.klasse=klasse;
		this.stunde=stunde;
		this.art=art;
		this.fach=fach;
		this.raum=raum;
		this.tag=tag;
	}

}
