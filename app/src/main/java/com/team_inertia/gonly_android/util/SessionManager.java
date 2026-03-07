package com.team_inertia.gonly_android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "GonlySession";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveLogin(String token, String email, String fullName, long userId) {
        editor.putString("token", token);
        editor.putString("email", email);
        editor.putString("fullName", fullName);
        editor.putLong("userId", userId);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString("token", null);
    }

    public String getEmail() {
        return prefs.getString("email", "");
    }

    public String getFullName() {
        return prefs.getString("fullName", "");
    }

    public long getUserId() {
        return prefs.getLong("userId", -1);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("isLoggedIn", false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}