package com.example.helloroutine;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class Route extends AppCompatActivity implements OnMapReadyCallback {
    private static NaverMap naverMap;
    Button btn1;
    Button btn2;
    TextView abc;
    TextView textView;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSION_REQUEST_CODE = 100;
    SimpleDateFormat format1 = new SimpleDateFormat ( "HHmmss");
    ArrayList<Double> listA = new <Double>ArrayList();      // 위도
    ArrayList <Double>listB = new <Double>ArrayList();      // 경도
    ArrayList<Double>speedlist = new <Double>ArrayList();   // 이동 거리의 합
    ArrayList listC = new ArrayList();       // 위도, 경도
    boolean isRun = false;
    String firstTime = null;
    String secondTime = null;
    boolean btnbool = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        abc = findViewById(R.id.abc);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        textView = findViewById(R.id.txtView);
        Handler handler2 = new Handler();

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        // 지도 객체 생성
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);


        btn1.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnbool == false) {
                    btnbool = true;
                    listA.clear();
                    listB.clear();
                    speedlist.clear();
                    listC.clear();
                    new Thread(new Runnable() {


                        int value = 0;

                        @Override
                        public void run() {
                            isRun = true;
                            abc.setText("시작");  // 거리측정이 시작이 되면 abc textview가 시작으로 바뀜.
                            firstTime = format1.format(System.currentTimeMillis()); // 시작시간 측정
                            while ((isRun)) { // 정지버튼이 눌리기 전까지 반복.
                                handler2.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        GpsTracker gpsTracker = new GpsTracker(Route.this);
                                        double latitude = gpsTracker.getLatitude();// 위도
                                        double longitude = gpsTracker.getLongitude(); // 경도
                                        listA.add(latitude);
                                        listB.add(longitude);
                                    }
                                });
                                try {
                                    Thread.sleep(500);  // 0.5초마다 반복
                                } catch (Exception e) {
                                }
                            }
                        }
                    }).start();
                }
                else Toast.makeText(getApplicationContext(), "이미 시작버튼이 눌려있습니다.", Toast.LENGTH_SHORT).show();

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnbool==true) {
                    btnbool = false;
                    isRun = false;
                    double s = 0; // 총거리
                    secondTime = format1.format(System.currentTimeMillis()); // 종료시간
                    int j = 0;

                    for(int i = 0; i < listA.size()-1; i++){
                        Location locationA = new Location("point A");
                        locationA.setLatitude(listA.get(i));
                        locationA.setLongitude(listB.get(i));

                        Location locationB = new Location("point B");
                        locationB.setLatitude(listA.get(i+1));
                        locationB.setLongitude(listB.get(i+1));
                        s = locationA.distanceTo(locationB); //lista[i], listb[i] ~ lista[i+1], listb[i+1] 사이의 거리를 s에 저장
                        if(s > 100) // 거리가 50m 가 넘을 시 lista, listb를 비워줌. gps가 갑자기 이상한 위치로 잡히는 오류 수정
                        {
                            listA.set(i + 1, null);
                            listB.set(i + 1, null);
                            while (listA.remove(null)) {
                            }
                            while (listB.remove(null)) {
                            }
                        }

                    }

                    for (int i = 0; i < listA.size(); i++) {
                        listC.add(new LatLng(listA.get(i), listB.get(i))); // listC에 위도 경도 저장
                    }
                    Intent intent = new Intent(Route.this, Navermap.class);
                    intent.putParcelableArrayListExtra("Key01", listC); // Key01로 listC list값 넘겨줌.

                    double add = 0;
                    s = 0;
                    float distance_ = 0;

                    for (int i = 0; i < listA.size() - 1; i++) { // 총 이동거리 저장
                        Location locationA = new Location("point A");
                        locationA.setLatitude(listA.get(i));
                        locationA.setLongitude(listB.get(i));

                        Location locationB = new Location("point B");
                        locationB.setLatitude(listA.get(i + 1));
                        locationB.setLongitude(listB.get(i + 1));
                        s = locationA.distanceTo(locationB);
                        speedlist.add(s);
                        add = add + s;

                    }
                    intent.putExtra("Key02", add); // Key02값으로 이동거리 넘겨줌.

                    abc.setText("이동 거리 " + Double.toString(add) + "m"); // 이동거리를 잠깐 보여줌. x
                    Date d1 = null; //시작시간 종료시간의 차이 구하는 코드.
                    Date d2 = null;
                    try {
                        d1 = format1.parse(firstTime);
                        d2 = format1.parse(secondTime);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    double diff = d2.getTime() - d1.getTime();
                    intent.putExtra("Key03", diff);
                    textView.setText("이동시간" + Double.toString(diff / 1000) + "초");


                    startActivity(intent);


                }
                else Toast.makeText(getApplicationContext(), "시작버튼이 눌려있지 않습니다.", Toast.LENGTH_SHORT).show();

            }
        });



    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        UiSettings uiSettings = naverMap.getUiSettings();
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        uiSettings.setLocationButtonEnabled(true);




    }



}