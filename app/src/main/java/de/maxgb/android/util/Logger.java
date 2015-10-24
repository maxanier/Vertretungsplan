package de.maxgb.android.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * Android Logging class which should replace android.util.Log, respectively puts itself between the application and Log.
 * It provides a Debug-Logging, which, if enabled, also saves the log in a file and not only to logcat.
 * The logfile is renamed into log.old after each app start and deleted after the second one. So there always exist two files.
 * 
 * @author Max Becker
 *
 */
public class Logger {

	private static boolean debug=false;
	private static File logFile;
	private static File logFile2;
	private static File directory;
	private static final String log_file_name="log.txt";
	private static final String log_file_name2="log.old.txt";
	private static final String TAG="Logger";
	
	private static HashMap<Pattern,String> regex=new HashMap<Pattern,String>();
	
	private Logger(){

		
	}
	/**
	 * Inits the logger and creates the necessary files.
	 * Must be called before logging
	 * @param pdirectory Directory where the logs shall be saved
	 */
	public static void init(String pdirectory){
		if(logFile==null){
			directory=new File(pdirectory);
			directory.mkdirs();
			logFile=new File(directory,log_file_name);
			logFile2=new File(directory,log_file_name2);
			logFile2.delete();
			logFile.renameTo(logFile2);
			logFile=new File(directory,log_file_name);
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			log("\nNeustart\n");
			
		}
	}
	
	/**
	 * Sets extended Debugmode
	 * @param mode
	 */
	public static  void setDebugMode(boolean mode){
		debug=mode;
		Logger.i(TAG, "Device info: device "+Build.DEVICE+" build "+Build.MANUFACTURER+" Android "+Build.VERSION.CODENAME+":"+Build.VERSION.SDK_INT);
	}
	
	public static boolean getDebugMode(){
		return debug;
	}
	public static void i(String tag,String msg){
		msg=applyRegex(msg);
		Log.i(tag,msg);
		log("INFO "+tag+": "+msg);
	}
	public static void w(String tag,String msg){
		msg=applyRegex(msg);
		Log.w(tag,msg);
		log("WARNING "+tag+": "+msg);
	}
	public static void e(String tag,String msg){
		msg=applyRegex(msg);
		Log.e(tag, msg);
		log("ERROR "+tag+": "+msg);
	}
	public static void e(String tag,String msg,Throwable t){
		msg=applyRegex(msg);
		Log.e(tag, msg, t);
		String stacktrace="";
		PrintStream p;
		try {
			p = new PrintStream(stacktrace);
			t.printStackTrace(p);
		} catch (FileNotFoundException e1) {
			stacktrace=t.getMessage();
		}
		
		log("ERROR "+tag+": "+msg+ "\n"+stacktrace);
	}
	
	/**
	 * Writes the message to the log file and adds the current time
	 * @param msg Message
	 */
	private static void log(String msg){
		if(debug&&logFile!=null){
			try {
				
				BufferedWriter output=new BufferedWriter(new FileWriter(logFile,true));

				output.append(DateFormat.format("MM-dd hh:mm:ss", new java.util.Date())+" "+msg+"\n");
				output.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}
	public static File getLogFile(){
		return logFile;
	}
	public static File getOldLogFile(){
		return logFile2;
	}
	
	public static boolean inEmulator(){
		Logger.i(TAG, "Testing if in Emulator");
		boolean f=Build.FINGERPRINT.startsWith("generic");

		Logger.i(TAG, "Fingerprint: "+f);
		
		boolean b=(Build.PRODUCT.equals("google_sdk")||Build.PRODUCT.equals("sdk"));
		Logger.i(TAG, "Build: "+b);
		
		boolean g=Build.HARDWARE.contains("goldfish");
		Logger.i(TAG, "Goldfish: "+g);
		Logger.i(TAG, "End resul: "+(f||b||g));
		return f||b||g;
	}
	
	public static void addRegex(String regex,String replacement){
		Pattern p=Pattern.compile(regex);
		Logger.regex.put(p, replacement);
	}
	
	private static String applyRegex(String s){
		for(Entry<Pattern,String> e:regex.entrySet()){
			s=e.getKey().matcher(s).replaceAll(e.getValue());
		}
		return s;
		
	}
	

}
