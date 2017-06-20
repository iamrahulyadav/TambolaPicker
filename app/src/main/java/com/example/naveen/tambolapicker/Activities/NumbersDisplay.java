package com.example.naveen.tambolapicker.Activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
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

import com.example.naveen.tambolapicker.R;
import com.example.naveen.tambolapicker.Utils.SessionSharedPrefs;
import com.example.naveen.tambolapicker.Utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NumbersDisplay extends AppCompatActivity {
    public static final String TAG = "naveen.tambolapicker";
    int[] num = new int[90];
    ObjectAnimator animator;
    ProgressBar progress1;
    int position = 0;
    TextView numberDisplayText;
    Switch automaticSwitch;
    RadioGroup timeButtons;
    Button nextButton;
    LinearLayout startstopButtons, boardLinearLayout;
    List<TextView> textViewList;
    ViewGroup completeLayout;
    private TextToSpeech textToSpeech;


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
        numberDisplayText = (TextView) findViewById(R.id.numberDisplayText);
        timeButtons = (RadioGroup) findViewById(R.id.timeButtons);
        nextButton = (Button) findViewById(R.id.nextButton);
        progress1 = (ProgressBar) findViewById(R.id.progress1);
        automaticSwitch = (Switch) findViewById(R.id.automaticSwitch);
        startstopButtons = (LinearLayout) findViewById(R.id.startstopButtons);
        boardLinearLayout = (LinearLayout) findViewById(R.id.boardLinearLayout);
        textViewList = new ArrayList<>();
        completeLayout = (ViewGroup) findViewById(R.id.completeLayout);
        //Setting Shared Preference
        automaticSwitch.setChecked(SessionSharedPrefs.getInstance().getAutoSwitch());
        //initialize variables
        position = SessionSharedPrefs.getInstance().getPosition();
        timeButtons.check(SessionSharedPrefs.getInstance().getTimeButtonChecked());
        //Getting numbers array
        if (fromButton == 0) {
            initializeNumber();
            randamize();
            saveNumbersInPrefs();
        }
        if (fromButton == 1) {
            getNumbersFromPrefs();
            if (position >= 0) {
                numberDisplayText.setText(String.valueOf(num[position - 1]));
            }
        }
        //Setting the Board
        setGameBoard();

        //Animation setting up
        animator = ObjectAnimator.ofInt(progress1, "progress", 100, 0);
        setAnimatorDuration();
        animator.setInterpolator(null);
        animator.setRepeatCount(Animation.INFINITE);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                changeNumber(false);
            }
        });
        //Check if autoSwitch is on or off
        checkAutoSwitch();
        timeButtons.setOnCheckedChangeListener((group, checkedId) -> setAnimatorDuration());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    private void initialize() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                Utilities.printLog("text to speech is set");
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setGameBoard() {
        for (int i = 0; i < 9; i++) {
            LinearLayout row = new LinearLayout(this);
            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            row.setLayoutParams(llParams);
            for (int j = 0; j < 10; j++) {
                TextView number = new TextView(this);
                String numberText;
                if ((i * 10 + (j + 1)) < 10) {
                    numberText = "0" + String.valueOf((i * 10 + (j + 1)));
                } else {
                    numberText = String.valueOf((i * 10 + (j + 1)));
                }
                number.setText(numberText);
                number.setBackgroundResource(R.drawable.border);
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1);
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
        for (int i = 0; i < position; i++) {
            textViewList.get(num[i] - 1).setBackgroundResource(R.drawable.complete_background);
            textViewList.get(num[i] - 1).setTextColor(Color.WHITE);
        }

    }

    private void stopAnimation() {
        animator.cancel();
    }

    public void checkAutoSwitch() {
        if (automaticSwitch.isChecked()) {
            nextButton.setVisibility(View.GONE);
            timeButtons.setVisibility(View.VISIBLE);
            progress1.setVisibility(View.VISIBLE);
            startstopButtons.setVisibility(View.VISIBLE);
            setAnimatorDuration();
            SessionSharedPrefs.getInstance().setAutoSwitch(true);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            timeButtons.setVisibility(View.GONE);
            progress1.setVisibility(View.GONE);
            startstopButtons.setVisibility(View.GONE);
            animator.cancel();
            SessionSharedPrefs.getInstance().setAutoSwitch(false);
        }
    }

    public void setAnimatorDuration() {
        switch (timeButtons.getCheckedRadioButtonId()) {
            case R.id.seconds_10:
                animator.setDuration(10 * 1000);
                SessionSharedPrefs.getInstance().setTimeButtonChecked(R.id.seconds_10);
                break;
            case R.id.seconds_20:
                SessionSharedPrefs.getInstance().setTimeButtonChecked(R.id.seconds_20);
                animator.setDuration(20 * 1000);
                break;
        }
    }

    public void initializeNumber() {
        for (int i = 0; i < num.length; i++) {
            num[i] = i + 1;
        }
    }

    public void randamize() {
        for (int i = 0; i < num.length; i++) {
            int temp, r;
            Random ran = new Random();
            r = ran.nextInt(num.length);
            temp = num[i];
            num[i] = num[r];
            num[r] = temp;
        }
    }

    public void saveNumbersInPrefs() {
        SessionSharedPrefs.getInstance().setNumberArray(num);
        SessionSharedPrefs.getInstance().setNumThere(true);
    }

    public void getNumbersFromPrefs() {
        num = SessionSharedPrefs.getInstance().getNumberArray();
//        String numbers = pref.getString("numberArray", null);
//        if (numbers != null) {
//            String[] number = numbers.split(",");
//            for (int i = 0; i < number.length; i++) {
//                num[i] = Integer.parseInt(number[i]);
//            }
//            Log.i(TAG, num[0] + "," + num[1] + "," + num[2] + "," + num[3]);
//        }
    }

    public void startMethod(View view) {
        if (position < num.length) {
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
        changeNumber(true);
    }

    public void changeNumber(boolean fromNext) {
        if (position < num.length) {
            if (position > 0) {
                textViewList.get(num[position - 1] - 1).setBackgroundResource(R.drawable.complete_background);
                textViewList.get(num[position - 1] - 1).setTextColor(Color.WHITE);
            }
            String number = String.valueOf(num[position]);
            numberDisplayText.setText(number);
            textToSpeech.speak(number, TextToSpeech.QUEUE_FLUSH, null);
            textViewList.get(num[position] - 1).setBackgroundResource(R.drawable.current_background);
            textViewList.get(num[position] - 1).setTypeface(Typeface.DEFAULT_BOLD);
            position += 1;
            SessionSharedPrefs.getInstance().setPosition(position);
            if (position >= num.length) {
                textToSpeech.speak("Board completed",TextToSpeech.QUEUE_ADD,null);
                if (fromNext) {
                    Toast.makeText(this, "All the numbers got completed", Toast.LENGTH_LONG).show();
                } else {
                    stopAnimation();
                }
            }
        }

    }

    public void boardButton(View view) {
        if (boardLinearLayout.getVisibility() == View.GONE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(completeLayout);
            }
            boardLinearLayout.setVisibility(View.VISIBLE);
        } else if (boardLinearLayout.getVisibility() == View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(completeLayout);
            }
            boardLinearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
