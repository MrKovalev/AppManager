package com.titanium.app_manager.base;

import com.google.android.gms.ads.MobileAds;

import androidx.multidex.MultiDexApplication;

public class App extends MultiDexApplication {

    private static final String ID = "ca-app-pub-9743454109151384~7431603311";

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, ID);
    }
}