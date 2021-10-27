package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
                /*BackgroundThread thread = new BackgroundThread();
                thread.start();*/

                //Runable객체를 implent하는방법(이것을 구현함으로써 한번 실행될 객체를 정의가능)
                //스레드를 만들고 그안에 Runnable을 집어넣는데 myThread2에서 스레드를 클래스로 별도로
                //만들었을떄와 차이가 없음.
                new Thread(new Runnable() {


                    int value = 0;

                    @Override
                    public void run() {
                        isRun = true;
                        int i = -1;
                        abc.setText("시작");
                        //1초마다 벨류값 1씩 증가시키는 스레드임
                        while ((isRun)) {
                            //핸들러클래스로서 post로 던질수가있음.
                            //핸들러의 post 메소드를 호출하면 Runnable 객체를 전달할 수 있습니다.
                            //핸들러로 전달된 Runnable, 객체는 메인 스레드에서 실행될 수 있으며 따라서 UI를 접근하는 코드는 Runnable 객체 안에 넣어두면 됩니다.
                            //post 메소드 이외에도 지정된 시간에 실행하는 postAtTime 메소드와 지정된 시간만큼 딜레이된 시간후 실행되는 postDelayed 메소드가 있습니다.
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
                                Thread.sleep(1000);
                            } catch (Exception e) {
                            }
                        }
                    }
                }).start(); //start()붙이면 바로실행시킨다.
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRun = false;
                //listA.add(37.57152);
                //listA.add(37.56607);
                //listB.add(126.97714);
                //listB.add(126.98268);
                for(int i = 0; i < listA.size(); i++){
                    listC.add(new LatLng(listA.get(i), listB.get(i)));
                }
                Intent intent = new Intent(GpsStart.this, Gpsline.class);
                intent.putParcelableArrayListExtra("Key01", listC);
                startActivity(intent);


            }
        });



    }


}