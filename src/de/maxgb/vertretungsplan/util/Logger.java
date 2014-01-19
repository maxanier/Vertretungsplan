package de.maxgb.vertretungsplan.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.text.format.DateFormat;
import android.util.Log;

public class Logger {

	private static boolean debug=false;
	private static File logFile;
	
	public static void e(String tag,String msg){
		Log.e(tag, msg);
		log("ERROR "+tag+": "+msg);
	}
	public static void e(String tag,String msg,Throwable t){
		Log.e(tag, msg, t);
		log("ERROR "+tag+": "+msg+ "\nERROR-MESSAGE: "+t.getMessage());
	}
	
	
	public static boolean getDebugMode(){
		return debug;
	}
	
	public static File getLogFile(){
		return logFile;
	}
	public static void i(String tag,String msg){
		Log.i(tag,msg);
		log("INFO "+tag+": "+msg);
	}
	public static void init(){
		if(logFile==null){
			(new File(Constants.PLAN_DIRECTORY)).mkdir();
			logFile=new File(Constants.PLAN_DIRECTORY+Constants.LOG_FILE_NAME);
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log("\n Neustart \n");
		}
	}
	private static void log(String msg){
		if(debug){
			try {
				
				BufferedWriter output=new BufferedWriter(new FileWriter(logFile,true));

				output.append(DateFormat.format("MM-dd hh:mm:ss", new java.util.Date())+" "+msg+"\n");
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static  void setDebugMode(boolean mode){
		debug=mode;
	}
	
	public static void w(String tag,String msg){
		Log.w(tag,msg);
		log("WARNING "+tag+": "+msg);
	}
	private Logger(){

		
	}
	

}
