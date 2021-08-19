package com.example.loginregister.login;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import com.example.loginregister.Fragment_Edit_User_Info;

public class PreferencesManager {

    static final String Account_Id = "id";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void storeAccount(Context ctx, String id) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Account_Id,"id");
        editor.commit();
    }

    public static String getAccount(Context ctx) {
        return getSharedPreferences(ctx).getString(Account_Id,"");
    }

    public static void removeAccount(Context ctx) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }
}