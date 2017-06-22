package com.example.naveen.tambolapicker.Utils;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mac on 6/22/17
 */

public class CrashLyticsUtil {
    public static void initialize(Context context) {
        Fabric.with(context, new Crashlytics());
    }

    public static void logNewNumberEvent() {
        Answers.getInstance().logCustom(new CustomEvent("New number picking"));
    }

    public static void logContinueNumberEvent() {
        Answers.getInstance().logCustom(new CustomEvent("Continue number picking")
                .putCustomAttribute("position", SessionSharedPrefs.getInstance().getPosition()));
    }

    public static void logCustomerEvent(CustomEvent customEvent){
        Answers.getInstance().logCustom(customEvent);
    }
}
