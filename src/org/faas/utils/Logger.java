package org.faas.utils;

import java.text.DecimalFormat;

import org.cloudbus.cloudsim.core.CloudSim;

public class Logger {
	
	public static final int ERROR = 2;
	public static final int INFO = 1; // added on 08.20
	public static final int DEBUG = 0;
	// CWE-500 public -> private
	private static int LOG_LEVEL = Logger.DEBUG;
	private static DecimalFormat df = new DecimalFormat("#.00"); 
	// CWE-500 public -> private
	private static boolean ENABLED = false;;

	public static boolean isEnabled() { return ENABLED; }
	public static void enable() {
		ENABLED = true;
	}
	
	public static void disable() {
		ENABLED = false;
	}


	public static void setLogLevel(int level){
		LOG_LEVEL = level;
	}

	public static void debug(Class aClass,String functionName,String name, String message){
		debug(aClass.getName()+"."+functionName,name,message);
	}

	public static void debug(Object obj,String functionName,String name, String message){
		debug(obj.getClass().getName()+"."+functionName,name,message);
	}

//	public static void debug(String name, String message){
//		debug(null,name,message);
//	}
	
	public static void debug(String classInfo, String name, String message){
		if(!ENABLED)
			return;
		if(Logger.LOG_LEVEL <= Logger.DEBUG) {
			if (classInfo != null) {
				System.out.println(df.format(CloudSim.clock())+"] "+classInfo+" : "+name+" : "+message);
			} else {
				System.out.println(df.format(CloudSim.clock())+"] "+name+" : "+message);
			}
		}
	}
	
	public static void info(String classInfo, String name, String message){
		if(!ENABLED)
			return;
		if(Logger.LOG_LEVEL <= Logger.INFO) {
			if (classInfo != null) {
				System.out.println(df.format(CloudSim.clock())+"] "+classInfo+" : "+name+" : "+message);
			} else {
				System.out.println(df.format(CloudSim.clock())+"] "+name+" : "+message);
			}
		}
	}
	
	public static void error(String name, String message){
		if(!ENABLED)
			return;
		if(Logger.LOG_LEVEL <= Logger.ERROR)
			System.out.println(df.format(CloudSim.clock())+" : "+name+" : "+message);
	}
	
}
