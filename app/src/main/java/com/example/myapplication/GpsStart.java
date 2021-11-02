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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GpsStart extends AppCompatActivity {

    Button btn1;
    Button btn2;
    TextView abc;
    TextView textview;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSION_REQUEST_CODE = 100;
    SimpleDateFormat format1 = new SimpleDateFormat ( "HHmmss");
    ArrayList<Double> listA = new <Double>ArrayList();
    ArrayList <Double>listB = new <Double>ArrayList();
    ArrayList listC = new ArrayList();
    boolean isRun = false;
    String firstTime = null;
    String secondTime = null;
    float speed_ = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        abc = findViewById(R.id.abc);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        textview = findViewById(R.id.textview);
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
                        firstTime = format1.format (System.currentTimeMillis());
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

                abc.setText("이동 거리 " + Double.toString(add) + "m");
                Date d1 = null;
                Date d2 = null;
                try {
                    d1 = format1.parse(firstTime);
                    d2 = format1.parse(secondTime);

                }catch(Exception e) {
                    e.printStackTrace();
                }
                double diff = d2.getTime() - d1.getTime();
                intent.putExtra("Key03", diff);
                textview.setText("이동시간" + Double.toString(diff/1000) + "초");

                startActivity(intent);



            }
        });



    }

}