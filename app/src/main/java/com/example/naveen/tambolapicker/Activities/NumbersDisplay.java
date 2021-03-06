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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.CustomEvent;
import com.example.naveen.tambolapicker.R;
import com.example.naveen.tambolapicker.Utils.CrashLyticsUtil;
import com.example.naveen.tambolapicker.Utils.SessionSharedPrefs;
import com.example.naveen.tambolapicker.Utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NumbersDisplay extends AppCompatActivity {
    int[] num = new int[90];
    ObjectAnimator animator;
    int position = 0;
    List<TextView> textViewList;
    @BindView(R.id.automaticSwitch)
    Switch automaticSwitch;
    @BindView(R.id.timeButtons)
    RadioGroup timeButtons;
    @BindView(R.id.startstopButtons)
    LinearLayout startstopButtons;
    @BindView(R.id.progress1)
    ProgressBar progress1;
    @BindView(R.id.numberDisplayText)
    TextView numberDisplayText;
    @BindView(R.id.nextButton)
    Button nextButton;
    @BindView(R.id.boardLinearLayout)
    LinearLayout boardLinearLayout;
    @BindView(R.id.completeLayout)
    ScrollView completeLayout;
    private TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers_display);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        int fromButton = intent.getIntExtra("From_Button", -1);
        Utilities.printLog("From_Button = " + fromButton);
        String TITLE = intent.getStringExtra("Title");
        getSupportActionBar().setTitle(TITLE);
        //initialize all Views
        textViewList = new ArrayList<>();
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
        //Setting the Board
        setGameBoard();
        //Animation setting up
        setAnimator();
        //Check if autoSwitch is on or off
        checkAutoSwitch();
        timeButtons.setOnCheckedChangeListener((group, checkedId) -> setAnimatorDuration());
    }

    private void setAnimator() {
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
            CrashLyticsUtil.logCustomerEvent(new CustomEvent("Automatic")
                    .putCustomAttribute("position", position));
            nextButton.setVisibility(View.GONE);
            timeButtons.setVisibility(View.VISIBLE);
            progress1.setVisibility(View.VISIBLE);
            startstopButtons.setVisibility(View.VISIBLE);
            setAnimatorDuration();
            SessionSharedPrefs.getInstance().setAutoSwitch(true);
        } else {
            CrashLyticsUtil.logCustomerEvent(new CustomEvent("Manual")
                    .putCustomAttribute("position", position));
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
                CrashLyticsUtil.logCustomerEvent(new CustomEvent("10 seconds"));
                animator.setDuration(10 * 1000);
                SessionSharedPrefs.getInstance().setTimeButtonChecked(R.id.seconds_10);
                break;
            case R.id.seconds_20:
                CrashLyticsUtil.logCustomerEvent(new CustomEvent("20 seconds"));
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
        Random ran = new Random();
        for (int i = 0; i < num.length; i++) {
            int temp, r;
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
    }

    public void startMethod(View view) {
        if (position < num.length) {
            CrashLyticsUtil.logCustomerEvent(new CustomEvent("AutoStarted")
                    .putCustomAttribute("position", position)
                    .putCustomAttribute("time", timeButtons.getCheckedRadioButtonId() == R.id.seconds_10 ? 10 : 20));
            textToSpeech.speak("Number picking started.", TextToSpeech.QUEUE_FLUSH, null);
            animator.start();
        }
    }

    public void stopMethod(View view) {
        CrashLyticsUtil.logCustomerEvent(new CustomEvent("AutoStopped")
                .putCustomAttribute("position", position)
                .putCustomAttribute("time", timeButtons.getCheckedRadioButtonId() == R.id.seconds_10 ? 10 : 20));
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
                textToSpeech.speak("Board completed", TextToSpeech.QUEUE_ADD, null);
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
            CrashLyticsUtil.logCustomerEvent(new CustomEvent("See Board"));
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
