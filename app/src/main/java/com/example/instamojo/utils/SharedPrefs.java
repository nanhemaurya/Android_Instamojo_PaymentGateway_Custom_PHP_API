package com.example.instamojo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {


    public static void setSP(Context context, String key, String val) {
        SharedPreferences sp = context.getSharedPreferences(Constant.login_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, val);
        editor.apply();
    }

    public static String getSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.login_SP, Context.MODE_PRIVATE);
        return sp.getString(key, "null");
    }

    public static void removeSP(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(Constant.login_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

}
