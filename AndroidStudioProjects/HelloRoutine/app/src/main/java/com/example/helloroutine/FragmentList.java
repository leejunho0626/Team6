package com.example.helloroutine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentList extends Fragment {

    CheckBox btnFav;
    CheckedTextView cbFav;
    TextView txtCha1 , txtCha2, txtCha3, txtCha4, txtCha5, txtScore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressBar progressBar;
    ProgressDialog customProgressDialog;
    String x;
    ListView listView;
    ListAdapter adapter;



    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_list, container, false);

        cbFav = view.findViewById(R.id.cbFav);
        txtScore = view.findViewById(R.id.txtScore);
        progressBar= view.findViewById(R.id.prg);
        listView = view.findViewById(R.id.listView);
        adapter = new ListAdapter((getActivity()));
        listView.setAdapter(adapter);


        //로딩화면 객체 생성
        customProgressDialog = new ProgressDialog(getActivity());
        //로딩화면을 투명하게 설정
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // 로딩화면 보여주기
        customProgressDialog.show();

        firebaseAuth = FirebaseAuth.getInstance();



        totalDistance(); //거리 DB 불러오기 - 진행도 표시
        totalPlan();
        totalAttendance();
        loadingScore();


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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int check_position = listView.getCheckedItemPosition();   //리스트뷰의 포지션을 가져옴.
                Object vo = adapterView.getAdapter().getItem(i).toString();  //리스트뷰의 포지션 내용을 가져옴
                Toast.makeText(getContext().getApplicationContext(), Integer.toString(i), Toast.LENGTH_LONG).show();
            }
        });






        /*btnFav1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                //즐겨찾기 설정
                if(btnFav1.isChecked()){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                //첫번째 즐겨찾기에 이미 등록된 상태라면
                                if (document.exists()) {
                                    findFav(txtCha1.getText().toString()); //다른 즐겨찾기 남은공간 검색
                                }
                                //추가1
                                else {
                                    addFav1(txtCha1.getText().toString());
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 추가를 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                //즐겨찾기 해제
                else{
                    deleteFav1(txtCha1.getText().toString());
                }
            }
        });
        btnFav2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //즐겨찾기 설정
                if(btnFav2.isChecked()){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    findFav(txtCha2.getText().toString());

                                } else {
                                    addFav1(txtCha2.getText().toString());
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 추가를 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                }
                //즐겨찾기 해제
                else{
                    deleteFav1(txtCha2.getText().toString());
                }

            }
        });
        btnFav3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //즐겨찾기 설정
                if(btnFav3.isChecked()){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                //첫번째 즐겨찾기에 이미 등록된 상태라면
                                if (document.exists()) {
                                    findFav(txtCha3.getText().toString()); //다른 즐겨찾기 남은공간 검색
                                }
                                //추가1
                                else {
                                    addFav1(txtCha3.getText().toString());
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 추가를 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                //즐겨찾기 해제
                else{
                    deleteFav1(txtCha3.getText().toString());
                }

            }
        });
        btnFav4.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //즐겨찾기 설정
                if(btnFav4.isChecked()){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                //첫번째 즐겨찾기에 이미 등록된 상태라면
                                if (document.exists()) {
                                    findFav(txtCha4.getText().toString()); //다른 즐겨찾기 남은공간 검색
                                }
                                //추가1
                                else {
                                    addFav1(txtCha4.getText().toString());
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 추가를 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                //즐겨찾기 해제
                else{
                    deleteFav1(txtCha4.getText().toString());
                }

            }
        });
        btnFav5.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //즐겨찾기 설정
                if(btnFav5.isChecked()){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                //첫번째 즐겨찾기에 이미 등록된 상태라면
                                if (document.exists()) {
                                    findFav(txtCha5.getText().toString()); //다른 즐겨찾기 남은공간 검색
                                }
                                //추가1
                                else {
                                    addFav1(txtCha5.getText().toString());
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 추가를 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                //즐겨찾기 해제
                else{
                    deleteFav1(txtCha5.getText().toString());
                }

            }
        });*/

        return view;
    }

    //진행도 점수 저장하기
    public void saveScore(String total){
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserRank userRank = new UserRank(user1.getEmail(),total);
        db.collection("DB").document("User").collection(user1.getUid()).document("Score").set(userRank)
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

    }
    //진행도 점수 불러오기
    public void loadingScore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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

                        txtScore.setText("현재점수 : "+score1+"점");




                    } else {
                        txtScore.setText("현재점수 : 0점");
                    }
                }
                else {
                }
            }
        });

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

                        adapter.addItem("걷거나 뛴 거리 3km", Integer.toString(value)+"%", value);
                        adapter.notifyDataSetChanged();

                        int value2 = (int) Math.round(Double.parseDouble(x)/5*100);

                        adapter.addItem("걷거나 뛴 거리 5km", Integer.toString(value2)+"%", value2);
                        adapter.notifyDataSetChanged();

                        String sum = Integer.toString(value+value2);

                        saveScore(sum);

                    } else {
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
                        x = str1.substring(0, str1.indexOf("}"));

                        int value = Integer.parseInt(x)*10;

                        adapter.addItem("운동 일정 10개 추가", Integer.toString(value)+"%", value);
                        adapter.notifyDataSetChanged();

                        int value2 = (int) Math.round(Double.parseDouble(x)/30*100);

                        adapter.addItem("운동 일정 30개 추가", Integer.toString(value2)+"%", value2);
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

                                        String sum = Integer.toString(nowScore+value+value2); //기존점수와 더하기

                                        saveScore(sum);


                                    } else {
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

    //출석일수 DB 불러오기
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

    }


    public void findFav(String list){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("2")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //두번째 즐겨찾기에 이미 등록된 상태라면
                    if (document.exists()) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("3")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    //세번째 즐겨찾기에 이미 등록된 상태라면
                                    if (document.exists()) {
                                        Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 초과(최대3개).", Toast.LENGTH_LONG).show();
                                    }
                                    //추가3
                                    else {
                                        addFav3(list);
                                    }
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 추가를 실패했습니다.", Toast.LENGTH_LONG).show();
                                }
                            }

                        });

                    }
                    //추가2
                    else {
                        addFav2(list);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 추가를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }


    //즐겨찾기 추가1
    public void addFav1(String txt){

        if(txt.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(txt);
            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1").set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
        }
    }

    //즐겨찾기 추가2
    public void addFav2(String txt){

        if(txt.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(txt);
            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("2").set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
        }
    }

    //즐겨찾기 추가3
    public void addFav3(String txt){

        if(txt.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(txt);
            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("3").set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    //즐겨찾기 해제1
    public void deleteFav1(String list){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //첫번째 즐겨찾기 DB 검색
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //존재하는 경우
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));

                        //DB 값과 클릭한 즐겨찾기 리스트와 일치하는 경우
                        if(x.equals(list)){
                            //첫번째 즐겨찾기 삭제
                            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기를 해제했습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 해제를 실패했습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        //일치하지 않는 경우
                        else{
                            deleteFav2(list);
                        }
                    }
                    //존재하지 않는 경우
                    else {
                        deleteFav2(list);
                    }
                }
            }

        });


    }
    //즐겨찾기 해제2
    public void deleteFav2(String list){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("2")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));

                        if(x.equals(list)){
                            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("2")
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기를 해제했습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 해제를 실패했습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                        else{
                            deleteFav3(list);
                        }

                    } else {
                        deleteFav3(list);

                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });


    }
    //즐겨찾기 해제3
    public void deleteFav3(String list){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("3")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));

                        if(x.equals(list)){
                            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("3")
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기를 해제했습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 해제를 실패했습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                    } else {


                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });


    }

    //즐겨찾기 표시1
    /*public void showBtnFav1(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("1")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));

                        if(x.equals(txtCha1.getText().toString())){
                            btnFav1.setChecked(true);
                        }
                        else if(x.equals(txtCha2.getText().toString())){
                            btnFav2.setChecked(true);
                        }
                        else if(x.equals(txtCha3.getText().toString())){
                            btnFav3.setChecked(true);
                        }
                        else if(x.equals(txtCha4.getText().toString())){
                            btnFav4.setChecked(true);
                        }
                        else {
                            btnFav5.setChecked(true);
                        }

                    } else {
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    //즐겨찾기 표시1
    public void showBtnFav2(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("2")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));

                        if(x.equals(txtCha1.getText().toString())){
                            btnFav1.setChecked(true);
                        }
                        else if(x.equals(txtCha2.getText().toString())){
                            btnFav2.setChecked(true);
                        }
                        else if(x.equals(txtCha3.getText().toString())){
                            btnFav3.setChecked(true);
                        }
                        else if(x.equals(txtCha4.getText().toString())){
                            btnFav4.setChecked(true);
                        }
                        else {
                            btnFav5.setChecked(true);
                        }

                    } else {
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
    //즐겨찾기 표시3
    public void showBtnFav3(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document("3")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}"));

                        if(x.equals(txtCha1.getText().toString())){
                            btnFav1.setChecked(true);
                        }
                        else if(x.equals(txtCha2.getText().toString())){
                            btnFav2.setChecked(true);
                        }
                        else if(x.equals(txtCha3.getText().toString())){
                            btnFav3.setChecked(true);
                        }
                        else if(x.equals(txtCha4.getText().toString())){
                            btnFav4.setChecked(true);
                        }
                        else {
                            btnFav5.setChecked(true);
                        }

                    } else {
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }*/

}