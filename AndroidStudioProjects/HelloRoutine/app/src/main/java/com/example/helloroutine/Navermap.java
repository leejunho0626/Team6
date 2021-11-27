package com.example.helloroutine;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import java.util.ArrayList;

public class Navermap extends AppCompatActivity implements OnMapReadyCallback {
    private static NaverMap naverMap;
    TextView textView;
    TextView speed_;
    TextView km;
    double speed = 0;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String x; //최종 거리값



    ArrayList listC = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.navermap);
        speed_ = findViewById(R.id.speed); // 이동거리
        textView = findViewById(R.id.textView); // 이동시간
        km = findViewById(R.id.km); // 평균 속력



        //네이버지도 객체 생성

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        Intent receive_intent_ = getIntent();
        speed = receive_intent_.getDoubleExtra("Key02", 0);  // 이동거리 관련 Key값 m 값을 가져옴
        double total = Math.round((speed/1000)*100)/100.0;
        speed_.setText("이동 거리 " + Double.toString(total) + "km"); // Km값으로 소수점 2번째 자리까지 보여줌.
        //DB

        double time_ = 0;
        time_ = receive_intent_.getDoubleExtra("Key03", 0); // 총 이동시간을 가져옴 단위 1000당 1초

        int num = (int) Math.round(time_);
        int sec = (num/1000) % 60;
        int min = (num/1000/60) % 60 ;
        int hour = (num/1000/60/60) % 60 ;
        if(hour==0) {
            if(min==0)
            {
                textView.setText("이동시간 : " + sec + "초");
            }
            else {
                textView.setText("이동시간 : " + min+"분"+sec + "초");
            }
        }
        else {
            textView.setText("이동시간 : " +hour+"시"+min+"분"+sec + "초");
        }

        double avgspeed = (speed/1000)/(time_/1000/60/60); // 평균 속력을 Km/h로 보여줌

        km.setText("평균 속력" + Double.toString(Math.round(avgspeed*100)/100.0) + "km/h"); // km/h값을 소수점 2번째 자리까지 보여줌

        loadDistance(total);


    }


    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;


        Intent receive_intent = getIntent();

        listC.clear();
        listC = receive_intent.getParcelableArrayListExtra("Key01"); // 위도 경도의 좌표값을 list에 담아서 가져옴

        CameraPosition cameraPosition = new CameraPosition((LatLng) listC.get(0), 16); // 첫번째 시작지점으로 카메라의 위치를 조정함.
        naverMap.setCameraPosition(cameraPosition); // 첫번째 시작지점으로 카메라 위치를 조정함.

        Marker markerst = new Marker();
        markerst.setPosition((LatLng) listC.get(0)); // 시작 좌표
        markerst.setCaptionText("시작"); // 마커에 시작이라는 글씨 출력
        markerst.setWidth(70);
        markerst.setHeight(100);
        markerst.setCaptionColor(Color.BLUE);
        markerst.setCaptionTextSize(16);
        markerst.setMap(naverMap);


        Marker markerfn = new Marker();
        markerfn.setPosition((LatLng) listC.get(listC.size() - 1)); // 종료 좌표
        markerfn.setWidth(70);
        markerfn.setHeight(100);
        markerfn.setCaptionText("종료"); // 마커에 종료라는 글씨 출력
        markerfn.setCaptionColor(Color.BLUE);
        markerfn.setCaptionTextSize(16);
        markerfn.setMap(naverMap);

        //경로선 표시
        PathOverlay path = new PathOverlay();
        path.setCoords(listC);
        path.setColor(Color.GREEN);
        path.setWidth(25);
        path.setMap(naverMap);

    }

    public void loadDistance(double total){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Total").document("DistanceCnt")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        x = str1.substring(0, str1.indexOf("}"));


                        double temp = Double.parseDouble(x)+total;
                        saveDistance(Double.toString(temp));


                    } else {
                        saveDistance("0"); //문서 생성
                        db.collection("DB").document(user.getEmail()).collection("Total").document("DistanceCnt")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str1 = document.getData().toString();
                                        str1 = str1.substring(str1.indexOf("=")+1);
                                        x = str1.substring(0, str1.indexOf("}")); //0

                                        double temp = Double.parseDouble(x)+total;
                                        saveDistance(Double.toString(temp));

                                    } else {


                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "거리 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                                }
                            }

                        });
                    }
                }
                else {
                }
            }

        });

    }


    public void saveDistance(String total){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(total);
        db.collection("DB").document(user.getEmail()).collection("Total").document("DistanceCnt").set(userWrite)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error.(getEmail)", Toast.LENGTH_LONG).show();
                    }
                });

    }
}