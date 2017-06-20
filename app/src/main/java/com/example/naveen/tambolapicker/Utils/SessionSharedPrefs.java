package com.example.naveen.tambolapicker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.naveen.tambolapicker.R;

/**
 * Created by mac on 6/20/17
 */

public class SessionSharedPrefs {

    private static final String POSITION = "position";
    private static final String AUTO_SWITCH = "autoSwitch";
    private static final String TIME_BUTTON_CLICKED = "timeButtonChecked";
    private static final String NUMBER_ARRAY = "numberArray";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private SessionSharedPrefs(Context context) {
        String PREF_FILENAME = "naveen.thontepu.tambolapicker";
        pref = context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
    }

    private static SessionSharedPrefs sessionSharedPrefs;

    public static void setInstance(Context context) {
        sessionSharedPrefs = new SessionSharedPrefs(context);
    }


    public static SessionSharedPrefs getInstance() {
        return sessionSharedPrefs;
    }

    public void setAutoSwitch(boolean AutoSwitch) {
        editor.putBoolean(AUTO_SWITCH, AutoSwitch);
        editor.commit();
    }

    public boolean getAutoSwitch() {
        return pref.getBoolean(AUTO_SWITCH, true);
    }

    public void setTimeButtonChecked(int TimeButtonChecked) {
        editor.putInt(TIME_BUTTON_CLICKED, TimeButtonChecked);
        editor.commit();
    }

    public int getTimeButtonChecked() {
        return pref.getInt(TIME_BUTTON_CLICKED, R.id.seconds_10);
    }

    public void setPosition(int Position) {
        editor.putInt(POSITION, Position);
        editor.commit();
    }

    public int getPosition() {
        return pref.getInt(POSITION, 0);
    }

    public void setNumberArray(int[] num) {
        String numbers = "";
        for (int i = 0; i < num.length - 1; i++) {
            numbers = numbers + String.valueOf(num[i]) + ",";
        }
        numbers = numbers + String.valueOf(num[num.length - 1]);
        editor.putString(NUMBER_ARRAY, numbers);
        editor.commit();
    }

    public int[] getNumberArray() {
        String numbers = pref.getString(NUMBER_ARRAY, null);
        int[] num = new int[90];
        if (numbers != null) {
            String[] number = numbers.split(",");
            for (int i = 0; i < number.length; i++) {
                num[i] = Integer.parseInt(number[i]);
            }
        }
        return num;
    }

}
