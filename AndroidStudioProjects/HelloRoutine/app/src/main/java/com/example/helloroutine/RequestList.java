package com.example.helloroutine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

public class RequestList extends AppCompatActivity {

    TextView txtId;
    Button btnCancel;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView recyclerView, recyclerView2;
    RequestAdapter requestAdapter;
    ReceiveAdapter receiveAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rq);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제

        recyclerView = (RecyclerView)findViewById(R.id.recyceler_rqList);
        recyclerView2 = (RecyclerView)findViewById(R.id.recyceler_rvList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        requestAdapter = new RequestAdapter();
        receiveAdapter = new ReceiveAdapter();

        rqList();
        rvList();





    }
    //보낸 요청
    public void rqList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("RQ_Friend").collection("Uid")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str = document.getId();


                            db.collection("DB").document("User").collection(str).document("ID")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str2 = document.getData().toString();
                                        str2 = str2.substring(str2.indexOf("=")+1);
                                        String y = str2.substring(0, str2.indexOf("}"));
                                        requestAdapter.setArrayData(y);
                                        recyclerView.setAdapter(requestAdapter);
                                    } else {

                                    }
                                }

                            });
                        } else {

                        }

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //받은 요청
    public void rvList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("AS_Friend").collection("Uid")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str = document.getId().toString();

                            db.collection("DB").document("User").collection(str).document("ID")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str2 = document.getData().toString();
                                        str2 = str2.substring(str2.indexOf("=")+1);
                                        String y = str2.substring(0, str2.indexOf("}"));
                                        receiveAdapter.setArrayData(y);
                                        recyclerView2.setAdapter(receiveAdapter);
                                    } else {

                                    }
                                }

                            });
                        } else {

                        }

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }







}
