package com.example.naveen.tambolapicker.Utils;

import android.util.Log;

import com.example.naveen.tambolapicker.BuildConfig;

/**
 * Created by mac on 6/20/17
 */

public class Utilities {
    /**
     * Prints log with tag as package name.
     *
     * @param msg
     */
    public static void printLog(String msg) {
        String TAG = "com.example.naveen.tambolapicker";
        printLog(TAG, msg);
    }

    /**
     * Prints log with tag that is provided.
     *
     * @param tag
     * @param msg
     */
    public static void printLog(String tag, String msg) {
        if (BuildConfig.DEBUG && msg != null) {
            int max = 3000;
            if (msg.length() < max) {
                Log.i(tag, msg);
            } else {
                int len = msg.length();
                for (int i = 0; i < len; i += max) {
                    Log.i(tag, msg.substring(i, (i + max) < len ? i + max : len));
                }
            }
        }
    }
}
