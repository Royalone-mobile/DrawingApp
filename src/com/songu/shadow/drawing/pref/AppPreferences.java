package com.songu.shadow.drawing.pref;

import android.content.SharedPreferences;
import android.util.Log;


public class AppPreferences{
	
	public static boolean geolocation;
	
	
	public static void updatePreferences(SharedPreferences prefs){
		geolocation = prefs.getBoolean("geolocation", true);
		Log.d("draw", "geo:"+geolocation);
	}

}
