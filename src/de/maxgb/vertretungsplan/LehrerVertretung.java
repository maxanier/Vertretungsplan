package de.maxgb.vertretungsplan;

public class LehrerVertretung extends Vertretung{
  public String vertreter, art, stunde,klasse,vgplan, raum,vstatt,bemerkungen,klausur;
  
  public LehrerVertretung(String vertreter, String art, String stunde, String klasse,
  String vgplan, String raum, String vstatt,String bemerkungen, String klausur) {
    this.vertreter = vertreter;
    this.art = art;
    this.stunde = stunde;
    this.klasse = klasse;
    this.vgplan = vgplan;
    this.raum = raum;
    this.vstatt = vstatt;
    this.bemerkungen = bemerkungen;
    this.klausur = klausur;
  }
  
  public boolean typSchueler(){
    return false;
  }
}