package com.example.naveen.tambolapicker.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.naveen.tambolapicker.R;

public class TambolaNumberPicker extends AppCompatActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private Button previousNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambola_number_picker);
        previousNumber = (Button) findViewById(R.id.previousNumber);
        pref = getSharedPreferences("com.example.naveen.tambolapicker", MODE_PRIVATE);
        editor = pref.edit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!pref.getBoolean("numThere", false) || pref.getInt("position", 0) == 90 || pref.getInt("position", 0) == 0) {
            previousNumber.setEnabled(false);
            previousNumber.setTextColor(getResources().getColor(R.color.ButtonTextColor));
        } else {
            previousNumber.setTextColor(getResources().getColor(R.color.TextColor));
            previousNumber.setEnabled(true);
        }

    }

    public void previousNumberPicking(View view) {
        Intent intent = new Intent(this, NumbersDisplay.class);
        intent.putExtra("From_Button", 1);
        intent.putExtra("Title", "Continue Number Picking");
        startActivity(intent);

    }

    public void newNumberPicking(View view) {
        Intent intent = new Intent(this, NumbersDisplay.class);
        intent.putExtra("From_Button", 0);
        intent.putExtra("Title", "New Number Picking");
        startActivity(intent);
        editor.putInt("position", 0);
        editor.putBoolean("autoSwitch", true);
        editor.commit();
    }
}
