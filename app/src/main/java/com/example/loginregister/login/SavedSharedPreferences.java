package com.example.loginregister.login;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.List;

public class SavedSharedPreferences {
    static final String USER_NAME="username";
    static final String USER_TOKEN="usertoken";
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName) {
        SharedPreferences.Editor editor=getSharedPreferences(ctx).edit();
        editor.putString(USER_NAME,userName);
        editor.commit();
    }

    public static String getUserName(Context ctx) {
        return getSharedPreferences(ctx).getString(USER_NAME,"");
    }

    public static void setUserToken(Context ctx,String userToken){
        SharedPreferences.Editor editor=getSharedPreferences(ctx).edit();
        editor.putString(USER_TOKEN,userToken);
        editor.commit();
    }
    public static String getUserToken(Context ctx) {
        return getSharedPreferences(ctx).getString(USER_TOKEN,"");
    }
}
