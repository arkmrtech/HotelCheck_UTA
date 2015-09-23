package com.lk.hotelcheck.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * @author lk
 *
 */
public class SharedPreferencesUtil {
	public final static String SETTING = "Setting";
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING, Activity.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean(key, value).commit();
	}
	
	public static boolean getBoolean(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING, Activity.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}
	
	public static void putString(Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING, Activity.MODE_PRIVATE);
		sharedPreferences.edit().putString(key, value).commit();
	}
	
	public static String getString(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SETTING, Activity.MODE_PRIVATE);
		return sharedPreferences.getString(key,"");
	}
	
}
