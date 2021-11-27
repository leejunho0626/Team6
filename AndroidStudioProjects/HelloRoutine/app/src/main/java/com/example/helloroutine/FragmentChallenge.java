package com.example.helloroutine;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;
import static java.lang.Thread.sleep;

public class FragmentChallenge extends Fragment {

    private Context mContext;
    CheckBox cbFav;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressBar progressBar;
    ProgressDialog customProgressDialog;
    String x;
    NotificationManager manager;
    NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "TimerPushAlarm";
    private static String CHANEL_NAME = "PushAlarm";
    ArrayList<String> list = new ArrayList<>();
    Button btnComplete;


    RecyclerView recyclerView;
    Challenge_Adapter challenge_adapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_challenge, container, false);

        cbFav = view.findViewById(R.id.cbFav);
        progressBar= view.findViewById(R.id.prg);
        firebaseAuth = FirebaseAuth.getInstance();
        challenge_adapter = new Challenge_Adapter();
        recyclerView = (RecyclerView)view.findViewById(R.id.recyceler_challengeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)) ;
        btnComplete = view.findViewById(R.id.btnComplete);


        //로딩화면 객체 생성
        customProgressDialog = new ProgressDialog(getActivity());
        //로딩화면을 투명하게 설정
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 로딩화면 보여주기
        customProgressDialog.show();
        addList();
        totalDistance();

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Challenge_complete.class); //화면 전환
                startActivity(intent);
            }
        });


        //로딩화면 종료
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
                timer.schedule(task, 500);
            }
        });
        thread.start();


        return view;
    }

    //거리 DB 불러오기
    public void totalDistance(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Total").document("AttendanceCnt")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //출석일수 O
                    if (document.exists()) {

                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String day = str1.substring(0, str1.indexOf("}"));

                        db.collection("DB").document(user.getEmail()).collection("Total").document("PlanCnt")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    //출석일수 O 일정 횟수 O
                                    if (document.exists()) {
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str1 = document.getData().toString();
                                        str1 = str1.substring(str1.indexOf("=")+1);
                                        String plan = str1.substring(0, str1.indexOf("}")); //추가한 일정 횟수

                                        db.collection("DB").document(user.getEmail()).collection("Total").document("DistanceCnt")
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    //출석일수 O 일정 횟수 O 거리 O
                                                    if (document.exists()) {
                                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                                        String str1 = document.getData().toString();
                                                        str1 = str1.substring(str1.indexOf("=")+1);
                                                        String distance = str1.substring(0, str1.indexOf("}"));

                                                        for(int i = 0; i<list.size(); i++) {
                                                            showChallengeList(plan, distance, day,i);
                                                        }

                                                    }
                                                    //출석일수 O 일정 횟수 O 거리 X
                                                    else {
                                                        for(int i = 0; i<list.size(); i++) {
                                                            showChallengeList(plan, "0", day,i);
                                                        }
                                                    }
                                                }
                                                else {

                                                }
                                            }
                                        });

                                    }
                                    //출석일수 O 일정 횟수 X
                                    else {

                                        db.collection("DB").document(user.getEmail()).collection("Total").document("DistanceCnt")
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    //출석일수 O 일정 횟수 x 거리 O
                                                    if (document.exists()) {
                                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                                        String str1 = document.getData().toString();
                                                        str1 = str1.substring(str1.indexOf("=")+1);
                                                        String distance = str1.substring(0, str1.indexOf("}")); //추가한 출석 횟수


                                                        for(int i = 0; i<list.size(); i++) {
                                                            showChallengeList("0", distance, day,i);
                                                        }




                                                    }
                                                    //출석일수 O 일정 횟수 X 거리 X
                                                    else {
                                                        for(int i = 0; i<list.size(); i++) {
                                                            showChallengeList("0", "0", day,i);
                                                        }
                                                    }
                                                }
                                                else {

                                                }
                                            }
                                        });

                                    }
                                }
                                else {

                                }
                            }
                        });


                    } else {


                    }
                }
                else {
                }
            }
        });

    }

    public void addList(){
        list.clear();
        list.add("운동 일정 10개 추가");
        list.add("운동 일정 30개 추가");
        list.add("운동 일정 50개 추가");
        list.add("걷거나 뛴 거리 1km");
        list.add("걷거나 뛴 거리 3km");
        list.add("걷거나 뛴 거리 5km");
        list.add("누적 42.195km 달성");
        list.add("출석 횟수 3일");
        list.add("출석 횟수 7일");
        list.add("출석 횟수 15일");
    }


    public void showChallengeList(String plan ,String distance, String today, int i) {

        int value1 = Integer.parseInt(plan)*10;
        value1 = checkscore(value1);
        int value2 = (int) Math.round(Double.parseDouble(plan)/30*100);
        value2 = checkscore(value2);
        int value3 = (int) Math.round(Double.parseDouble(plan)/50*100);
        value3 = checkscore(value3);
        int dis1 = (int) Math.round(Double.parseDouble(distance)/1*100);
        dis1 = checkscore(dis1);
        int dis2 = (int) Math.round(Double.parseDouble(distance)/3*100);
        dis2 = checkscore(dis2);
        int dis3 = (int) Math.round(Double.parseDouble(distance)/5*100);
        dis3 = checkscore(dis3);
        int dis4 = (int) Math.round(Double.parseDouble(distance)/42.195*100);
        dis4 = checkscore(dis4);
        int cnt = (int) Math.round(Double.parseDouble(today)/3*100);
        cnt = checkscore(cnt);
        int cnt2 = (int) Math.round(Double.parseDouble(today)/7*100);
        cnt2 = checkscore(cnt2);
        int cnt3 = (int) Math.round(Double.parseDouble(today)/15*100);
        cnt3 = checkscore(cnt3);



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        int finalValue = value1;
        int finalValue2 = value2;
        int finalValue3 = value3;
        int finalDis1 = dis1;
        int finalDis2 = dis2;
        int finalDis3 = dis3;
        int finalDis4 = dis4;
        int finalCnt1 = cnt;
        int finalCnt2 = cnt2;
        int finalCnt3 = cnt3;

        SimpleDateFormat format = new SimpleDateFormat ( "yyyy.MM.dd");
        String format_1 = format.format(System.currentTimeMillis());
        if (finalValue >= 100) {
            saveComplete(list.get(0), format_1);
        }
        if(finalValue2 >= 100) {
            saveComplete(list.get(1), format_1);
        }
        if(finalValue3 >= 100) {
            saveComplete(list.get(2), format_1);
        }
        if(finalDis1 >= 100) {
            saveComplete(list.get(3), format_1);
        }
        if(finalDis2 >= 100) {
            saveComplete(list.get(4), format_1);
        }
        if(finalDis3 >= 100) {
            saveComplete(list.get(5), format_1);
        }
        if(finalDis4 >= 100) {
            saveComplete(list.get(6), format_1);
        }
        if(finalCnt1 >= 100) {
            saveComplete(list.get(7), format_1);
        }
        if(finalCnt2 >= 100) {
            saveComplete(list.get(8), format_1);
        }
        if(finalCnt3 >= 100) {
            saveComplete(list.get(9), format_1);
        }
        db.collection("DB").document(user.getEmail()).collection("Challenge").document("List").collection("Favorite").document(list.get(i))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (list.get(i).equals("운동 일정 10개 추가")) {
                            challenge_adapter.setArrayData(list.get(i), finalValue, true);
                        }
                        else if (list.get(i).equals("운동 일정 30개 추가")) {
                            challenge_adapter.setArrayData(list.get(i), finalValue2, true);
                        }
                        else if (list.get(i).equals("운동 일정 50개 추가")) {
                            challenge_adapter.setArrayData(list.get(i), finalValue3, true);
                        }
                        else if (list.get(i).equals("걷거나 뛴 거리 1km")) {
                            challenge_adapter.setArrayData(list.get(i), finalDis1, true);
                        }
                        else if (list.get(i).equals("걷거나 뛴 거리 3km")) {
                            challenge_adapter.setArrayData(list.get(i), finalDis2, true);
                        }
                        else if (list.get(i).equals("걷거나 뛴 거리 5km")) {
                            challenge_adapter.setArrayData(list.get(i), finalDis3, true);
                        }
                        else if (list.get(i).equals("누적 42.195km 달성")) {
                            challenge_adapter.setArrayData(list.get(i), finalDis4, true);
                        }
                        else if (list.get(i).equals("출석 횟수 3일")) {
                            challenge_adapter.setArrayData(list.get(i), finalCnt1, true);
                        }
                        else if (list.get(i).equals("출석 횟수 7일")) {
                            challenge_adapter.setArrayData(list.get(i), finalCnt2, true);
                        }
                        else if(list.get(i).equals("출석 횟수 15일")){
                            challenge_adapter.setArrayData(list.get(i), finalCnt3, true);
                        }
                        recyclerView.setAdapter(challenge_adapter);

                    }
                    else {
                        if (list.get(i).equals("운동 일정 10개 추가")) {
                            challenge_adapter.setArrayData(list.get(i), finalValue, false);
                        }
                        else if (list.get(i).equals("운동 일정 30개 추가")) {
                                challenge_adapter.setArrayData(list.get(i), finalValue2, false);
                        }
                        else if (list.get(i).equals("운동 일정 50개 추가")) {
                                challenge_adapter.setArrayData(list.get(i), finalValue3, false);
                            }
                        else if (list.get(i).equals("걷거나 뛴 거리 1km")) {
                                challenge_adapter.setArrayData(list.get(i), finalDis1, false);
                        }
                        else  if (list.get(i).equals("걷거나 뛴 거리 3km")) {
                                challenge_adapter.setArrayData(list.get(i), finalDis2, false);
                            }
                        else if (list.get(i).equals("걷거나 뛴 거리 5km")) {
                                challenge_adapter.setArrayData(list.get(i), finalDis3, false);
                            }
                        else if (list.get(i).equals("누적 42.195km 달성")) {
                                challenge_adapter.setArrayData(list.get(i), finalDis4, false);
                            }
                        else if (list.get(i).equals("출석 횟수 3일")) {
                                challenge_adapter.setArrayData(list.get(i), finalCnt1, false);
                            }
                        else if (list.get(i).equals("출석 횟수 7일")) {
                                challenge_adapter.setArrayData(list.get(i), finalCnt2, false);
                            }
                        else if(list.get(i).equals("출석 횟수 15일")){
                                challenge_adapter.setArrayData(list.get(i), finalCnt3, false);
                        }

                        recyclerView.setAdapter(challenge_adapter);
                    }
                }

            }

        });

    }

    public static int checkscore(int score)
    {
        if(score>100)
        {
            score = 100;

            return score;
        }
        return score;


    }

    public void saveComplete(String txt,String time){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("DB").document(user.getEmail()).collection("Challenge").document("List").collection("Complete").document(txt)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        UserRank userRank = new UserRank(txt, time);
                        db.collection("DB").document(user.getEmail()).collection("Challenge").document("List").collection("Complete").document(txt).set(userRank)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                                    }
                                });


                    } else {
                    }
                }
                else {


                }
            }
        });


    }






}