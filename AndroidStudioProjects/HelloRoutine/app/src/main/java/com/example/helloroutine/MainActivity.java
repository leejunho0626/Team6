package com.example.helloroutine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentUser fragmentUser = new FragmentUser();
    private FragmentCalendar fragmentCalendar = new FragmentCalendar();
    private FragmentChallenge fragmentChallenge = new FragmentChallenge();
    private static final long FINISH_INTERVAL_TIME = 2000;
    private static long backPressedTime = 0;
    static ProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();

        //하단 메뉴바
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());


    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(this, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

        }
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();


            switch(menuItem.getItemId())
            {
                case R.id.user:
                    transaction.replace(R.id.frameLayout, fragmentUser).commitAllowingStateLoss();
                    break;
                case R.id.calendar:
                    transaction.replace(R.id.frameLayout, fragmentCalendar).commitAllowingStateLoss();
                    break;
                case R.id.home:
                    //로딩창 객체 생성
                    customProgressDialog = new ProgressDialog(MainActivity.this);
                    //로딩창을 투명하게
                    customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    // 로딩창 보여주기
                    customProgressDialog.show();
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            TimerTask task = new TimerTask(){
                                @Override
                                public void run() {
                                    customProgressDialog.dismiss();
                                }
                            };
                            Timer timer = new Timer();
                            timer.schedule(task, 1500);
                        }
                    });
                    thread.start();
                    break;
                case R.id.challenge:
                    //로딩창 객체 생성
                    customProgressDialog = new ProgressDialog(MainActivity.this);
                    //로딩창을 투명하게
                    customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    // 로딩창 보여주기
                    customProgressDialog.show();
                    transaction.replace(R.id.frameLayout, fragmentChallenge).commitAllowingStateLoss();
                    Thread thread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            TimerTask task = new TimerTask(){
                                @Override
                                public void run() {
                                    customProgressDialog.dismiss();
                                }
                            };
                            Timer timer = new Timer();
                            timer.schedule(task, 1000);
                        }
                    });
                    thread1.start();
                    break;
            }
            return true;
        }
    }

}