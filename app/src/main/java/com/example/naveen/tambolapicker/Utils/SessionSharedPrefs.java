package com.example.naveen.tambolapicker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mac on 6/20/17
 */

public class SessionSharedPrefs {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private SessionSharedPrefs(Context context) {
        String PREF_FILENAME = "naveen.thontepu.tambolapicker";
        pref = context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
    }
}
