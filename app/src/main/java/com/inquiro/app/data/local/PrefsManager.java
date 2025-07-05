package com.inquiro.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREFS_NAME = "INQUIRO_APP_PREFS";
    private static final String KEY_AUTH_TOKEN = "AUTH_TOKEN";
    private static PrefsManager instance;
    private final SharedPreferences sharedPreferences;

    private PrefsManager(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new PrefsManager(context);
        }
    }

    public static PrefsManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PrefsManager must be initialized in Application class");
        }
        return instance;
    }

    public void saveAuthToken(String token) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}