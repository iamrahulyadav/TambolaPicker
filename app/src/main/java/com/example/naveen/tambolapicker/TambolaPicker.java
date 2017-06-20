package com.example.naveen.tambolapicker;

import android.app.Application;

import com.example.naveen.tambolapicker.Utils.SessionSharedPrefs;

/**
 * Created by mac on 6/20/17
 */

public class TambolaPicker extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SessionSharedPrefs.setInstance(this);
    }
}
