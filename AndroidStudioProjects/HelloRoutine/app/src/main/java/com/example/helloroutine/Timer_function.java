package com.example.helloroutine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

public class Timer_function extends AppCompatActivity {

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
    private boolean flag = true;
    private boolean Timercheck = true;

    String sHour = "0";
    String sMin = "0";
    String sSec = "0";

    private long time = 0;
    private long tempTime = 0;

    NotificationManager manager;
    NotificationCompat.Builder builder;

    private static String CHANNEL_ID = "TimerPushAlarm";
    private static String CHANEL_NAME = "PushAlarm";

    FrameLayout setting;
    FrameLayout timer;
    SharedPreferences spref;

    private Button onBtn, offBtn;

    @Override
    protected void onCreate(Bundle saveInstanceStat) {
        super.onCreate(saveInstanceStat);
        setContentView(R.layout.timer_function);


        cntText = findViewById(R.id.cnt_text);
        startBtn = findViewById(R.id.cntdown_btn);
        stopBtn = findViewById(R.id.stop_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        hourText = findViewById(R.id.hour);
        minText = findViewById(R.id.min);
        secText = findViewById(R.id.sec);

        setting = findViewById(R.id.setting);
        timer = findViewById(R.id.timer);
        spref = getSharedPreferences("gref", MODE_PRIVATE);

        String temp1 = spref.getString("push", "사용");

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstState = true;

                setting.setVisibility(View.VISIBLE);
                timer.setVisibility(View.VISIBLE);
                startStop(temp1);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Long.parseLong(sHour) == 0 && Long.parseLong(sMin) == 0 && Long.parseLong(sSec) == 0) {}
                else {
                    if (flag) {
                        stopBtn.setText("STOP");
                        flag = false;
                    } else if (!flag) {
                        stopBtn.setText("RESUME");
                        flag = true;
                    }
                }
                startStop(temp1);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting.setVisibility(View.VISIBLE);
                timer.setVisibility(View.VISIBLE);
                firstState = true;
                try {
                    stopTimer();
                } catch(Exception e){
                    Toast.makeText(Timer_function.this,"시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateTimer();
    }

    private void startStop (String setting) {
        if (timerRunning) {
            cntDownTimer.cancel();
            timerRunning = false;
        } else {
            startTimer(setting);
        }
    }

    private void startTimer (String setting) {
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
            if (Long.parseLong(sHour) == 0 && Long.parseLong(sMin) == 0 && Long.parseLong(sSec) == 0) {
                Toast.makeText(Timer_function.this, "시간을 입력해주세요.", Toast.LENGTH_SHORT).show();
                Timercheck = false;
            }
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
                if(Timercheck){
                    showNoti(setting);
                } else{}
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
        stopBtn.setText("STOP");
        hourText.setText("");
        minText.setText("");
        secText.setText("");
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

    public void showNoti(String setting){
        Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        Uri ringing = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringing);

        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //버전 오레오 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            );
            builder = new NotificationCompat.Builder(this,CHANNEL_ID); //하위 버전일 경우
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        //알림창 제목
        builder.setContentTitle("타이머 종료");
        //알림창 메시지
        builder.setContentText("타이머가 종료되었습니다.");
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.logo);
        Notification notification = builder.build();
        //알림창 실행
        if(setting.equals("사용")){
            ringtone.play();
            manager.notify(1,notification);
        }

    }

}