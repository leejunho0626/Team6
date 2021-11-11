package com.example.helloroutine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Friend extends AppCompatActivity {

    Button btnAddFr, btnShow;
    TextView txtFr1, txtFrRank, txtFrRank2, test1, test2;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<String> idList = new ArrayList<>();
    ArrayList<String> scoreList = new ArrayList<>();
    ArrayList<String> idScoreList = new ArrayList<>();
    ArrayList<String> friendList = new ArrayList<>();
    ArrayList<String> arrayList = new ArrayList<>();

    String id2, score1, temp1, temp2;
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

        btnShow = findViewById(R.id.btnShow1);
        btnAddFr = findViewById(R.id.btnAddFr);

        test1= findViewById(R.id.test1);
        test2= findViewById(R.id.test2);
        listView = findViewById(R.id.listView);
        listView2 = findViewById(R.id.listView2);


        firebaseAuth = FirebaseAuth.getInstance();



        showFriendList();



        //list_(temp1,temp2);

        /*
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_();
            }
        });*/


        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idScoreList);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendList);
        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scoreList);
        adapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, idList);

        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);

        btnAddFr.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText = new EditText(Friend.this); //입력창
                //다이얼로그 메뉴
                AlertDialog.Builder builder = new AlertDialog.Builder(Friend.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
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
                            builder1.setMessage("추가하시겠습니까?");
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






    }

    public void list_(String id, String score){

        idList.add(id);
        scoreList.add(score);
        if(id==null){
            idList.add("null");
        }
        if(score==null){
            scoreList.add("0");
        }
    }


    //친구 추가1
    public void addFriend(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(uid.length()>0){
            UserFriend userFriend = new UserFriend(uid);
            db.collection("DB").document("User").collection(user.getUid()).document("Friend").collection("Uid").document(uid).set(userFriend)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_LONG).show();

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

    //친구 표시1
    public void showFriendList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Friend").collection("Uid")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str1 = document.getId().toString(); //uid

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
                                            String y = str2.substring(0, str2.indexOf("}"));

                                            Log.d(TAG, "ID" + " => " +y);

                                            friendList.add(y);


                                            adapter2.notifyDataSetChanged();

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

                                                                idScoreList.add(idList.get(i)+ " / " + scoreList.get(i));
                                                            }
                                                            adapter.notifyDataSetChanged();
                                                            Log.d(TAG, "total"+ " => " +idScoreList);


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