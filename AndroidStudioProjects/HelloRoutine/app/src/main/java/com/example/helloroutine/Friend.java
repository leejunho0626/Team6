package com.example.helloroutine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import static android.content.ContentValues.TAG;

public class Friend extends AppCompatActivity {

    Button btnAddFr, btnRq;
    ProgressDialog customProgressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<String> idList = new ArrayList<>();
    ArrayList<String> scoreList = new ArrayList<>();
    ArrayList<String> idScoreList = new ArrayList<>();
    ArrayList<String> friendList = new ArrayList<>();
    String id2, score1;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    ArrayAdapter<String> adapter3;
    ArrayAdapter<String> adapter4;
    ListView listView, listView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnAddFr = findViewById(R.id.btnAddFriend);
        btnRq = findViewById(R.id.btnRQ);
        listView = findViewById(R.id.listView);
        listView2 = findViewById(R.id.listView2);

        //로딩화면 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩화면을 투명하게 설정
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // 로딩화면 보여주기
        customProgressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();

        showFriendList();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idScoreList);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendList);
        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scoreList);
        adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idList);

        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);

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
                timer.schedule(task, 1000);
            }
        });
        thread.start();

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Friend.this, FriendPlan.class); //화면 전환
                intent.putExtra("id",friendList.get(i));
                startActivity(intent);
            }
        });

        btnAddFr.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText = new EditText(Friend.this); //입력창
                //다이얼로그 메뉴
                AlertDialog.Builder builder = new AlertDialog.Builder(Friend.this);
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Friend.this);
                builder.setTitle("친구 추가하기");
                builder.setMessage("복사한 친구의 UID를 입력하세요.");
                builder.setView(editText);

                builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String edit = editText.getText().toString(); //입력한 값
                        if(edit.length()>0){
                            builder1.setTitle("");
                            builder1.setMessage("친구 요청을 하시겠습니까?");
                            builder1.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    addFriend(edit);
                                }
                            });
                            builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                                }
                            });
                            builder1.show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "UID을 입력하세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
        });

        //친구 요청 목록
        btnRq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friend.this, RequestList.class); //화면 전환
                startActivity(intent);

            }
        });
    }
    //친구 추가
    public void addFriend(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(uid.length()>0){
            UserFriend userFriend = new UserFriend(uid);
            db.collection("DB").document("User").collection(user.getUid()).document("RQ_Friend").collection("Uid").document(uid).set(userFriend)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            receiveMsg(uid);
                            Toast.makeText(getApplicationContext(), "요청을 보냈습니다.", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "UID을 다시 입력하세요.", Toast.LENGTH_LONG).show();
        }
    }

    //친구 추가
    public void receiveMsg(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(uid.length()>0){
            UserFriend userFriend = new UserFriend(user.getUid());
            db.collection("DB").document("User").collection(uid).document("AS_Friend").collection("Uid").document(user.getUid()).set(userFriend)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Log.d(TAG, " 요청 성공 ");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "UID을 다시 입력하세요.", Toast.LENGTH_LONG).show();
        }
    }

    //친구 표시
    public void showFriendList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //친구 uid 검색
        db.collection("DB").document("User").collection(user.getUid()).document("Friend").collection("Uid")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str1 = document.getId().toString(); //uid

                            //친구uid로 아이디 검색
                            db.collection("DB").document("User").collection(str1).document("ID")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()){
                                            //DB 필드명 표시 지워서 데이터 값만 표시
                                            String str2 = document.getData().toString();
                                            str2 = str2.substring(str2.indexOf("=")+1);
                                            String id = str2.substring(0, str2.indexOf("}")); //아이디

                                            //친구 배열에 아이디 추가, 자신 아이디 제거
                                            friendList.add(id);
                                            for (int i =0; i < friendList.size() ; i++ ){
                                                if(friendList.get(i).toString().equals(user.getEmail())){
                                                    friendList.remove(i);
                                                }
                                            }
                                            adapter2.notifyDataSetChanged();

                                            //친구 점수 검색
                                            db.collection("DB").document("User").collection(str1).document("Score")
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()){
                                                            //DB 필드명 표시 지워서 데이터 값만 표시
                                                            String str1 = document.getData().toString(); //{score=점수,id=이메일}
                                                            str1 = str1.substring(str1.indexOf("=")+1); //점수,id=이메일}
                                                            score1 = str1.substring(0, str1.indexOf(",")); //점수
                                                            String id1 = str1.substring(str1.indexOf("=")+1); //이메일}
                                                            id2 = id1.substring(0, id1.indexOf("}")); //이메일

                                                            Log.d(TAG, "id/score" + " => " +id2+" "+score1);

                                                            scoreList.add(score1);
                                                            adapter3.notifyDataSetChanged();
                                                            idList.add(id2);
                                                            adapter4.notifyDataSetChanged();

                                                            for (int i =0; i < idList.size() ; i++ ){
                                                                for (int j = i+1; j < idList.size() ; j++ ){
                                                                    if (Integer.parseInt(scoreList.get(j)) > Integer.parseInt(scoreList.get(i))){
                                                                        String temp = scoreList.get(i);
                                                                        scoreList.set(i, scoreList.get(j));
                                                                        scoreList.set(j, temp);
                                                                        String str = idList.get(i);
                                                                        idList.set(i, idList.get(j));
                                                                        idList.set(j, str);
                                                                    }
                                                                }
                                                            }
                                                            idScoreList.clear();
                                                            for (int i =0; i < idList.size() ; i++ ){
                                                                idScoreList.add(i+1+". "+idList.get(i)+ " / " + scoreList.get(i));

                                                            }
                                                            adapter.notifyDataSetChanged();
                                                            Log.d(TAG, "total"+ " => " +idScoreList);


                                                        }
                                                        else{
                                                            //친구 점수 검색
                                                            db.collection("DB").document("User").collection(str1).document("ID")
                                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            //DB 필드명 표시 지워서 데이터 값만 표시
                                                                            String str2 = document.getData().toString();
                                                                            str2 = str2.substring(str2.indexOf("=")+1);
                                                                            String id = str2.substring(0, str2.indexOf("}")); //아이디
                                                                            idScoreList.add("- "+id+ " / " + 0);
                                                                            adapter.notifyDataSetChanged();
                                                                        }
                                                                    }
                                                                }


                                                            });

                                                        }
                                                    }
                                                }

                                            });




                                        }
                                    }
                                }

                            });
                        } else {

                        }

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "목표 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}