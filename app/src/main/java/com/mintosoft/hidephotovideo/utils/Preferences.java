package com.mintosoft.hidephotovideo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    public static void savepasscode(Context context, String passcode) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("Passcode", passcode);
        editor.apply();
    }

    public static String getpasscode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        return preferences.getString("Passcode", null);
    }

    public static boolean usefingerprint(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("usefingerprint", false);
    }

    public static void savesecurityquestionNumber(Context context, Integer passcode) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE).edit();
        editor.putInt("SecurityQuestionNumber", passcode);
        editor.apply();
    }

    public static Integer getsecurityquestionNumber(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        return preferences.getInt("SecurityQuestionNumber", 1);
    }

    public static void savesecurityanswer(Context context, String passcode) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE).edit();
        editor.putString("SecurityAnswer", passcode);
        editor.apply();
    }

    public static String getsecurityanswer(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        return preferences.getString("SecurityAnswer", null);
    }

    public static void setAlertDialog(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE).edit();
        editor.putBoolean("AlertDialog", value);
        editor.apply();
    }

    public static boolean getAlertDialog(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        return preferences.getBoolean("AlertDialog", true);
    }
}
