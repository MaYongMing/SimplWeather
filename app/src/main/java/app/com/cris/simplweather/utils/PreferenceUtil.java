package app.com.cris.simplweather.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Cris on 2017/6/21.
 */

public class PreferenceUtil {
    public static boolean putString(Context context, String preName, String key, String value){

       SharedPreferences preferences = context.getSharedPreferences(preName,Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key,value);
        return  editor.commit();
    }

    public  static boolean putBoolean(Context context, String preName, String key, boolean value){

        SharedPreferences preferences = context.getSharedPreferences(preName,Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key,value);
        return  editor.commit();
    }

    public  static boolean putLong(Context context, String preName, String key, long value){

        SharedPreferences preferences = context.getSharedPreferences(preName,Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key,value);
        return  editor.commit();
    }


    public static long getLong(Context context, String preName, String key, long value) {

        SharedPreferences preferences = context.getSharedPreferences(preName, Context.MODE_APPEND);
        return preferences.getLong(key,value);
    }

    public static String getString(Context context, String preName, String key, String value) {

        SharedPreferences preferences = context.getSharedPreferences(preName, Context.MODE_APPEND);
        return preferences.getString(key,value);
    }

    public static boolean getboolean(Context context, String preName, String key, boolean value) {

        SharedPreferences preferences = context.getSharedPreferences(preName, Context.MODE_APPEND);
        return preferences.getBoolean(key,value);
    }
}
