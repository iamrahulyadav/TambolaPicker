package com.example.naveen.tambolapicker;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumbersDisplay extends AppCompatActivity {
    public static final String TAG = "naveen.tambolapicker";
    int[] num=new int[90];
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ObjectAnimator animator;
    ProgressBar progress1;
    int position=0;
    TextView numberDisplayText;
    Switch automaticSwitch;
    RadioGroup timeButtons;
    Button nextButton;
    LinearLayout startstopButtons,boardLinearLayout;
    List<TextView> textViewList;
    ViewGroup completeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers_display);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        int fromButton = intent.getIntExtra("From_Button", -1);
        Log.i(TAG, "From_Button = " + fromButton);
        String TITLE = intent.getStringExtra("Title");
        getSupportActionBar().setTitle(TITLE);
        //initialize all Views
        numberDisplayText =(TextView)findViewById(R.id.numberDisplayText);
        timeButtons = (RadioGroup)findViewById(R.id.timeButtons);
        nextButton = (Button)findViewById(R.id.nextButton);
        progress1 = (ProgressBar)findViewById(R.id.progress1);
        automaticSwitch = (Switch)findViewById(R.id.automaticSwitch);
        startstopButtons = (LinearLayout)findViewById(R.id.startstopButtons);
        boardLinearLayout = (LinearLayout)findViewById(R.id.boardLinearLayout);
        textViewList = new ArrayList<TextView>();
        completeLayout = (ViewGroup)findViewById(R.id.completeLayout);
        //Setting Shared Preference
        pref = getSharedPreferences("com.example.naveen.tambolapicker",MODE_PRIVATE);
        editor = pref.edit();
        automaticSwitch.setChecked(pref.getBoolean("autoSwitch", true));
        //initialize variables
        position = pref.getInt("position",0);
        timeButtons.check(pref.getInt("timeButtonChecked", R.id.seconds_10));
        //Getting numbers array
        if (fromButton == 0){
            initialize();
            randamize();
            saveNumbersInPrefs();
        }
        if (fromButton == 1){
            getNumbersFromPrefs();
            numberDisplayText.setText(String.valueOf(num[position-1]));
        }
        //Setting the Board
        setGameBoard();

        //Animation setting up
        animator = ObjectAnimator.ofInt(progress1,"progress",100,0);
        setAnimatorDuration();
        animator.setInterpolator(null);
        animator.setRepeatCount(Animation.INFINITE);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (position != 0) {
                    textViewList.get(num[position - 1] - 1).setBackgroundColor(Color.parseColor("#00b5e2"));
                    textViewList.get(num[position - 1] - 1).setTextColor(Color.WHITE);
                }
                numberDisplayText.setText(String.valueOf(num[position]));
                textViewList.get(num[position] - 1).setBackgroundColor(Color.parseColor("#e22d00"));
                textViewList.get(num[position] - 1).setTypeface(Typeface.DEFAULT_BOLD);
                position += 1;
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.i(TAG, "position1 = " + position);
                editor.putInt("position", position);
                editor.commit();

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (position < num.length) {
                    Log.i(TAG, "Position = " + position);
                    textViewList.get(num[position - 1] - 1).setBackgroundColor(Color.parseColor("#00b5e2"));
                    textViewList.get(num[position - 1] - 1).setTextColor(Color.WHITE);
                    numberDisplayText.setText(String.valueOf(num[position]));
                    textViewList.get(num[position] - 1).setBackgroundColor(Color.parseColor("#e22d00"));
                    textViewList.get(num[position] - 1).setTypeface(Typeface.DEFAULT_BOLD);
                    position += 1;
                    if (position >= num.length) {
                        stopAnimation();
                    }
                }

            }
        });
        //Check if autoSwitch is on or off
        checkAutoSwitch();
        timeButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setAnimatorDuration();
            }
        });
    }

    private void setGameBoard(){
        for (int i=0;i<9;i++){
            LinearLayout row = new LinearLayout(this);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,1);
            row.setLayoutParams(llParams);
            for(int j=0;j<10;j++){
                TextView number = new TextView(this);
                String numberText;
                if ((i*10+(j+1))<10){
                    numberText ="0"+String.valueOf((i * 10 + (j + 1)));
                }else{
                    numberText = String.valueOf((i * 10 + (j + 1)));
                }
                number.setText(numberText);
                number.setBackgroundResource(R.drawable.border);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,1);
                textParams.setMargins(3, 3, 3, 3);
                number.setLayoutParams(textParams);
                number.setTextSize(20);
                number.setPadding(4, 4, 4, 4);
                number.setTextColor(Color.BLACK);
                number.setGravity(Gravity.CENTER);

                row.addView(number);
                textViewList.add(number);
            }
            boardLinearLayout.addView(row);
        }
        for(int i=0;i<position;i++){
            textViewList.get(num[i]-1).setBackgroundColor(Color.parseColor("#00b5e2"));
            textViewList.get(num[i]-1).setTextColor(Color.WHITE);
        }

    }

    private void stopAnimation() {
        animator.cancel();
    }

    public void checkAutoSwitch() {
        if (automaticSwitch.isChecked()){
            nextButton.setVisibility(View.GONE);
            timeButtons.setVisibility(View.VISIBLE);
            progress1.setVisibility(View.VISIBLE);
            startstopButtons.setVisibility(View.VISIBLE);
            setAnimatorDuration();
            editor.putBoolean("autoSwitch", true);
            editor.commit();
        }else {
            nextButton.setVisibility(View.VISIBLE);
            timeButtons.setVisibility(View.GONE);
            progress1.setVisibility(View.GONE);
            startstopButtons.setVisibility(View.GONE);
            animator.cancel();
            editor.putBoolean("autoSwitch", false);
            editor.commit();
        }
    }

    public void setAnimatorDuration(){
        switch (timeButtons.getCheckedRadioButtonId()){
            case R.id.seconds_10:
                animator.setDuration(10 * 1000);
                editor.putInt("timeButtonChecked",R.id.seconds_10);
                editor.commit();
                break;
            case R.id.seconds_20:
                editor.putInt("timeButtonChecked",R.id.seconds_20);
                editor.commit();
                animator.setDuration(20*1000);
                break;
        }
    }

    public void initialize() {
        for(int i=0;i<num.length;i++){
            num[i]=i+1;
        }
    }
    public void randamize(){
        for (int i=0;i<num.length;i++){
            int temp,r;
            Random ran = new Random();
            r=ran.nextInt(num.length-0);
            temp = num[i];
            num[i]= num[r];
            num[r]=temp;
        }
    }
    public void saveNumbersInPrefs(){
        String numbers="";
        for(int i=0;i<num.length-1;i++){
            numbers=numbers+String.valueOf(num[i])+",";
        }
        numbers=numbers+String.valueOf(num[num.length-1]);
        editor.putString("numberArray",numbers);
        editor.putBoolean("numThere", true);
        editor.commit();
        Log.i(TAG,numbers);

    }
    public void getNumbersFromPrefs(){
        String numbers= pref.getString("numberArray",null);
        String[] number=numbers.split(",");
        for (int i=0;i<number.length;i++){
            num[i]=Integer.parseInt(number[i]);
        }
        Log.i(TAG,num[0]+","+num[1]+","+num[2]+","+num[3]);
    }

    public void startMethod(View view) {
        if(position<num.length){
            animator.start();
        }
    }

    public void stopMethod(View view) {
        animator.cancel();
    }

    public void autoSwitchMethod(View view) {
        checkAutoSwitch();
    }

    public void nextButtonMethod(View view) {
        if (position<num.length){
            try {
                textViewList.get(num[position-1] - 1).setBackgroundColor(Color.parseColor("#00b5e2"));
                textViewList.get(num[position-1] - 1).setTextColor(Color.WHITE);
            }catch (Exception e){

            }
            numberDisplayText.setText(String.valueOf(num[position]));
            textViewList.get(num[position]-1).setBackgroundColor(Color.parseColor("#e22d00"));
            textViewList.get(num[position]-1).setTypeface(Typeface.DEFAULT_BOLD);
            position+=1;
            editor.putInt("position", position);
            editor.commit();
            if (position>=num.length){
                Toast.makeText(this,"All the numbers got completed",Toast.LENGTH_LONG).show();
            }
        }

    }

    public void boardButton(View view) {
        if(boardLinearLayout.getVisibility()==View.GONE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(completeLayout);
            }
            boardLinearLayout.setVisibility(View.VISIBLE);
        }else if(boardLinearLayout.getVisibility()==View.VISIBLE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(completeLayout);
            }
            boardLinearLayout.setVisibility(View.GONE);
        }
    }
}
