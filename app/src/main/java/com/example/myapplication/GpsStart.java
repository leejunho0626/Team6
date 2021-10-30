package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GpsStart extends AppCompatActivity {

    Button btn1;
    Button btn2;
    TextView abc;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSION_REQUEST_CODE = 100;

    ArrayList<Double> listA = new <Double>ArrayList();
    ArrayList <Double>listB = new <Double>ArrayList();
    ArrayList listC = new ArrayList();
    boolean isRun = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        abc = findViewById(R.id.abc);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        Handler handler2 = new Handler();

        btn1.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {


                    int value = 0;

                    @Override
                    public void run() {
                        isRun = true;
                        int i = -1;
                        abc.setText("시작");
                        //Location location = null;

                        //speed_ = location.getSpeed();
                        while ((isRun)) {
                            handler2.post(new Runnable() {
                                @Override
                                public void run() {
                                    GpsTracker gpsTracker = new GpsTracker(GpsStart.this);
                                    double latitude = gpsTracker.getLatitude();// 위도
                                    double longitude = gpsTracker.getLongitude();
                                    listA.add(latitude);
                                    listB.add(longitude);
                                }
                            });
                            try {
                                Thread.sleep(3000);
                            } catch (Exception e) {
                            }
                        }
                    }
                }).start();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRun = false;
                for(int i = 0; i < listA.size(); i++){
                    listC.add(new LatLng(listA.get(i), listB.get(i)));
                }
                Intent intent = new Intent(GpsStart.this, Gpsline.class);
                intent.putParcelableArrayListExtra("Key01", listC);
                //intent.putExtra("speedKey", speed_);

                double add = 0;
                double s = 0;
                float distance_ = 0;

                for(int i = 0; i < listA.size()-1; i++){
                    Location locationA = new Location("point A");
                    locationA.setLatitude(listA.get(i));
                    locationA.setLongitude(listB.get(i));

                    Location locationB = new Location("point B");
                    locationB.setLatitude(listA.get(i+1));
                    locationB.setLongitude(listB.get(i+1));
                    s = locationA.distanceTo(locationB);
                    add = add + s;

                }
                intent.putExtra("Key02", add);

                abc.setText(Double.toString(add));

                startActivity(intent);



            }
        });



    }

}