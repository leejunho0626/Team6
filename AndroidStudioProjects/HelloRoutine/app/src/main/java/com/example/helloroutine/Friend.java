package com.example.helloroutine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Friend extends AppCompatActivity {

    Button btnAddFr;
    TextView txtFr1;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnAddFr = findViewById(R.id.btnAddFr);
        txtFr1= findViewById(R.id.txtFr1);
        firebaseAuth = FirebaseAuth.getInstance();


        addFrDownload();


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


    //친구 추가1
    public void addFriend(String uid){

        if(uid.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(uid);
            db.collection("DB").document("User").collection(user.getUid()).document("Friend").collection("Uid").document("1").set(userWrite)
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
    public void addFrDownload(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Friend").collection("Uid").document("1")
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


                        db.collection("DB").document("User").collection(x).document("ID")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()){
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str1 = document.getData().toString();
                                        str1 = str1.substring(str1.indexOf("=")+1);
                                        String y = str1.substring(0, str1.indexOf("}"));

                                        txtFr1.setText("1. "+y);
                                    }
                                }
                            }

                        });
                    } else {
                        txtFr1.setText(" 새로운 친구를 추가하세요.");

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "목표 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}