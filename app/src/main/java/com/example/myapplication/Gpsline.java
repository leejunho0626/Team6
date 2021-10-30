package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;

import java.util.ArrayList;
import java.util.Arrays;

public class Gpsline extends AppCompatActivity implements OnMapReadyCallback {
    private LatLng myLatLng = new LatLng( 37.57152, 126.97714);
    private static NaverMap naverMap;
    TextView textView;
    TextView speed_;
    ArrayList listC = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsline);
        speed_ = findViewById(R.id.speed);




        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
        double speed = 0;
        Intent receive_intent_ = getIntent();
        speed = receive_intent_.getDoubleExtra("Key02", 0);
        speed_.setText(Double.toString(speed));





    }


    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;


        Intent receive_intent = getIntent();


        listC.clear();

        listC = receive_intent.getParcelableArrayListExtra("Key01");

        CameraPosition cameraPosition = new CameraPosition((LatLng) listC.get(0), 16);
        naverMap.setCameraPosition(cameraPosition);

        Marker markerst = new Marker();
        markerst.setPosition((LatLng) listC.get(0));
        markerst.setCaptionText("시작");
        markerst.setCaptionColor(Color.BLUE);
        markerst.setCaptionTextSize(16);
        markerst.setMap(naverMap);


        Marker markerfn = new Marker();
        markerfn.setPosition((LatLng) listC.get(listC.size() - 1));
        markerfn.setCaptionText("종료");
        markerfn.setCaptionColor(Color.BLUE);
        markerfn.setCaptionTextSize(16);
        markerfn.setMap(naverMap);





        PathOverlay path = new PathOverlay();
        path.setCoords(listC);

        path.setWidth(30);
        path.setMap(naverMap);



    }
}