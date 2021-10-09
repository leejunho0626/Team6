package com.example.helloroutine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentUser fragmentUser = new FragmentUser();
    private FragmentCalendar fragmentCalendar = new FragmentCalendar();
    private FragmentChallenge fragmentChallenge = new FragmentChallenge();
    private FragmentSetting fragmentSetting = new FragmentSetting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //하단 메뉴바
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());
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
                    transaction.replace(R.id.frameLayout, fragmentHome).commitAllowingStateLoss();
                    break;
                case R.id.challenge:
                    transaction.replace(R.id.frameLayout, fragmentChallenge).commitAllowingStateLoss();
                    break;
                case R.id.setting:
                    transaction.replace(R.id.frameLayout, fragmentSetting).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

}