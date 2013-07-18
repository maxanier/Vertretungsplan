package de.maxgb.vertretungsplan;

public class SchuelerVertretung extends Vertretung{
  public String klasse, art, fach, stunde, raum;
  
  public SchuelerVertretung(String klasse, String stunde, String art, String fach,
  String raum, String tag) {
    this.klasse = klasse;
    this.stunde = stunde;
    this.art = art;
    this.fach = fach;
    this.raum = raum;
    this.tag = tag;
  }
  
  public boolean typSchueler(){
    return true;
    }
}