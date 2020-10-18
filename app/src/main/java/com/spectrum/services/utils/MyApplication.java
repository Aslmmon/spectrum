package com.spectrum.services.utils;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import androidx.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {

    private RxBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        bus = new RxBus();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public RxBus bus() {
        return bus;
    }

}