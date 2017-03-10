package com.alobha.challenger;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.raizlabs.android.dbflow.config.FlowManager;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mrNRG on 08.06.2016.
 */
public class ChallengerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        FlowManager.init(this);
        PersistentPreferences.createInstance(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
