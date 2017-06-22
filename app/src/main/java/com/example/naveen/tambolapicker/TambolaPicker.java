package com.example.naveen.tambolapicker;

import android.app.Application;

import com.example.naveen.tambolapicker.Utils.CrashLyticsUtil;
import com.example.naveen.tambolapicker.Utils.SessionSharedPrefs;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by mac on 6/20/17
 */

public class TambolaPicker extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashLyticsUtil.initialize(this);
        SessionSharedPrefs.setInstance(this);
    }
}
