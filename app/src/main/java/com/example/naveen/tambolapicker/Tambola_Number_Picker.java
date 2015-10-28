package com.example.naveen.tambolapicker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Tambola_Number_Picker extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambola__number__picker);
        Button previousNumber = (Button)findViewById(R.id.previousNumber);
        pref = getSharedPreferences("com.example.naveen.tambolapicker",MODE_PRIVATE);
        editor = pref.edit();
        if(!pref.getBoolean("numThere",false)||pref.getInt("position",0)==90||pref.getInt("position",0)==0){
            previousNumber.setEnabled(false);
        }else {
            previousNumber.setEnabled(true);
        }


    }

    public void previousNumberPicking(View view) {
        Intent intent = new Intent(this,NumbersDisplay.class);
        intent.putExtra("From_Button",1);
        intent.putExtra("Title","Continue Number Picking");
        startActivity(intent);

    }

    public void newNumberPicking(View view) {
        Intent intent = new Intent(this,NumbersDisplay.class);
        intent.putExtra("From_Button",0);
        intent.putExtra("Title","New Number Picking");
        startActivity(intent);
        editor.putInt("position", 0);
        editor.putBoolean("autoSwitch",true);
        editor.commit();
    }
}
