package com.example.helloroutine;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Timer;
import java.util.TimerTask;

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
    ListView listView;
    ListAdapter adapter;
    NotificationManager manager;
    NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "TimerPushAlarm";
    private static String CHANEL_NAME = "PushAlarm";
    final String[] list = {"운동 일정 10개 추가", "운동 일정 30개 추가", "운동 일정 50개 추가", "걷거나 뛴 거리 1km", "걷거나 뛴 거리 3km", "걷거나 뛴 거리 5km","걷거나 뛴 거리 42.195km"
    ,"출석 횟수 3일","출석 횟수 7일", "출석 횟수 15일","출석 횟수 30일"};

    RecyclerView recyclerView;
    Challenge_Adapter challenge_adapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_challenge, container, false);

        cbFav = view.findViewById(R.id.cbFav);
        progressBar= view.findViewById(R.id.prg);
        listView = view.findViewById(R.id.listView);
        adapter = new ListAdapter((getActivity()));
        listView.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();
        challenge_adapter = new Challenge_Adapter();
        recyclerView = (RecyclerView)view.findViewById(R.id.recyceler_challengeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)) ;

        //로딩화면 객체 생성
        customProgressDialog = new ProgressDialog(getActivity());
        //로딩화면을 투명하게 설정
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 로딩화면 보여주기
        customProgressDialog.show();
        showChallengeList();

        try {
            totalDistance(); //거리 DB 불러오기 - 진행도 표시
            sleep(2000);

            totalPlan();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //totalAttendance();

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
        db.collection("DB").document("User").collection(user.getUid()).document("TotalDistance")
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

                        int value = (int) Math.round(Double.parseDouble(x)/3*100);
                        int value2 = (int) Math.round(Double.parseDouble(x)/5*100);

                        showBtnFav("0", value, value2);
                        showBtnFav("1", value, value2);
                        String sum = Integer.toString(value+value2);


                    } else {

                        showBtnFav("0", 0, 0);
                        showBtnFav("1", 0, 0);
                    }
                }
                else {
                }
            }
        });

    }

    //일정추가 횟수 DB 불러오기
    public void totalPlan(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("TotalPlan")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        x = str1.substring(0, str1.indexOf("}")); //추가한 일정 횟수

                        int value = Integer.parseInt(x)*10;
                        int value2 = (int) Math.round(Double.parseDouble(x)/30*100);
                        showBtnFav("2", value, value2);
                        showBtnFav("3", value, value2);

                        db.collection("DB").document("User").collection(user.getUid()).document("Score")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str1 = document.getData().toString(); //{score=점수,id=이메일}
                                        str1 = str1.substring(str1.indexOf("=")+1); //점수,id=이메일}
                                        String score1 = str1.substring(0, str1.indexOf(",")); //점수
                                        String id1 = str1.substring(str1.indexOf("=")+1); //이메일}
                                        String id2 = id1.substring(0, id1.indexOf("}")); //이메일

                                        int nowScore = Integer.parseInt(score1); //점수

                                        String sum = Integer.toString(nowScore+value+value2); //기존점수와 더하기

                                    } else {

                                    }
                                }
                                else {
                                }
                            }
                        });

                    } else {
                        showBtnFav("2", 0, 0);
                        showBtnFav("3", 0, 0);
                    }
                }
                else {

                }
            }
        });
    }
    public void showBtnFav(String position, int value , int value2){
        try {
            sleep(500);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document(position)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            if(document.getId().equals("0")){
                                adapter.addItem("걷거나 뛴 거리 3km", Integer.toString(value)+"%", value, true);
                            }
                            else if(document.getId().equals("1")){
                                adapter.addItem("걷거나 뛴 거리 5km", Integer.toString(value2)+"%", value2, true);
                            }
                            else if(document.getId().equals("2")){
                                adapter.addItem("운동 일정 10개 추가", Integer.toString(value)+"%", value, true);
                            }

                            else {
                                adapter.addItem("운동 일정 30개 추가", Integer.toString(value2)+"%", value2, true);
                            }

                        }
                        else {
                            if(position.equals("0")){
                                adapter.addItem("걷거나 뛴 거리 3km", Integer.toString(value)+"%", value, false);
                            }
                            else if(position.equals("1")){
                                adapter.addItem("걷거나 뛴 거리 5km", Integer.toString(value2)+"%", value2, false);
                            }
                            else if(position.equals("2")){
                                adapter.addItem("운동 일정 10개 추가", Integer.toString(value)+"%", value, false);
                            }
                            else {
                                adapter.addItem("운동 일정 30개 추가", Integer.toString(value2)+"%", value2, false);
                            }

                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext.getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                }

            });
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //오늘의 일정 표시
    public void showChallengeList() {
        for (int i = 0; i < list.length; i++) {
            challenge_adapter.setArrayData(list[i],0,true);
            recyclerView.setAdapter(challenge_adapter);

        }
    }



    /*//출석일수 DB 불러오기
    public void totalAttendance(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("TotalAttendance")
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

                        int value = Integer.parseInt(x)*10;
                        check = true;
                        adapter.addItem("3일 연속 출석", Integer.toString(value)+"%", value);
                        adapter.notifyDataSetChanged();

                        db.collection("DB").document("User").collection(user.getUid()).document("Score")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str1 = document.getData().toString(); //{score=점수,id=이메일}
                                        str1 = str1.substring(str1.indexOf("=")+1); //점수,id=이메일}
                                        String score1 = str1.substring(0, str1.indexOf(",")); //점수
                                        String id1 = str1.substring(str1.indexOf("=")+1); //이메일}
                                        String id2 = id1.substring(0, id1.indexOf("}")); //이메일

                                        int nowScore = Integer.parseInt(score1); //점수

                                        String sum = Integer.toString(nowScore+value); //기존점수와 더하기

                                        saveScore(sum);


                                    } else {
                                    }
                                }
                                else {
                                }
                            }
                        });


                    } else {
                        adapter.addItem("3일 연속 출석", Integer.toString(0)+"%", 0);
                        adapter.addItem("7일 연속 출석", Integer.toString(0)+"%", 0);
                        adapter.addItem("10일 연속 출석", Integer.toString(0)+"%", 0);
                        adapter.notifyDataSetChanged();
                    }
                }
                else {

                }
            }
        });

    }*/



}