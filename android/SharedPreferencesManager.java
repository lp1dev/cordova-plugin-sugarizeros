package org.olpcfrance.sugarizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.cordova.CallbackContext;

/**
 * Created by lupin on 10/08/16.
 */
public class SharedPreferencesManager {
    final static String LAUNCHES_TAG = "LAUNCHES";
    final static String IS_SETUP_TAG = "IS_SETUP";
    final static String IS_DEFAULT_LAUNCHER_TAG = "IS_DEFAULT_LAUNCHER";

    public static void putInt(Context context, String tag, int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(tag, value);
        editor.apply();
    }
    public static void putString(Context context, String tag, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(tag, value);
        editor.apply();
    }
    public static void getInt(CallbackContext callbackContext, Context context, String tag){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        callbackContext.success(prefs.getInt(tag, -1));
    }
    public static int getInt(Context context, String tag){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(tag, -1);
    }
    public static String getString(Context context, String tag){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(tag, "");
    }
}
