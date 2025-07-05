package com.inquiro.app;

import android.app.Application;
import com.inquiro.app.data.local.PrefsManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PrefsManager.init(this);
    }
}