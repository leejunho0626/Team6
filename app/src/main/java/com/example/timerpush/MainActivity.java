package com.example.timerpush;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView cntText;

    private Button startBtn;
    private Button stopBtn;
    private Button cancelBtn;

    private EditText hourText;
    private EditText minText;
    private EditText secText;

    private CountDownTimer cntDownTimer;

    private boolean timerRunning;
    private boolean firstState;

    private long time = 0;
    private long tempTime = 0;

    int cnt = 0;


    FrameLayout setting;
    FrameLayout timer;



    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);

        cntText = findViewById(R.id.cnt_text);
        startBtn = findViewById(R.id.cntdown_btn);
        stopBtn = findViewById(R.id.stop_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        hourText = findViewById(R.id.hour);
        minText = findViewById(R.id.min);
        secText = findViewById(R.id.sec);

        setting = findViewById(R.id.setting);
        timer = findViewById(R.id.timer);


            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstState = true;

                    setting.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    startStop();
                }
            });

            stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cnt % 2 == 1)
                        stopBtn.setText("STOP");
                    else
                        stopBtn.setText("RESUME");
                    startStop();

                    cnt++;
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setting.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    firstState = true;
                    stopTimer();
                }
            });
            updateTimer();
        }

        private void startStop () {
            if (timerRunning) {
                cntDownTimer.cancel();
                timerRunning = false;
            } else {
                startTimer();
            }
        }

        private void startTimer () {
            String sHour = "0";
            String sMin = "0";
            String sSec = "0";

            if (firstState) {
                sHour = hourText.getText().toString();
                sMin = minText.getText().toString();
                sSec = secText.getText().toString();
                if (hourText.getText().toString().equals(""))
                    sHour = "0";
                if (minText.getText().toString().equals(""))
                    sMin = "0";
                if (secText.getText().toString().equals(""))
                    sSec = "0";
                time = (Long.parseLong(sHour) * 3600000) + (Long.parseLong(sMin) * 60000) + (Long.parseLong(sSec) * 1000) + 1000;
            } else {
                time = tempTime;
            }

            cntDownTimer = new CountDownTimer(time, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tempTime = millisUntilFinished;
                    updateTimer();
                }

                @Override
                public void onFinish() {
                }
            }.start();

            timerRunning = true;
            firstState = false;
        }

        private void stopTimer () {
            cntDownTimer.cancel();
            timerRunning = false;
            cancelBtn.setText("CANCEL");
            cntText.setText("0:00:00");
        }

        private void updateTimer () {
            int hour = (int) tempTime / 3600000;
            int min = (int) tempTime % 3600000 / 60000;
            int sec = (int) tempTime % 3600000 % 60000 / 1000;

            String timeLeftText = "";

            timeLeftText = "" + hour + ":";

            if (min < 10) timeLeftText += "0";
            timeLeftText += min + ":";

            if (sec < 10) timeLeftText += "0";
            timeLeftText += sec;

            cntText.setText(timeLeftText);
        }
}