package com.example.helloroutine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

public class Challenge_complete extends AppCompatActivity {


    RecyclerView recyclerView;
    Complete_Adapter complete_adapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_complete);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제

        complete_adapter = new Complete_Adapter();
        recyclerView = (RecyclerView)findViewById(R.id.recyceler_Complete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ;

        completeList();



    }

    public void completeList(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Challenge").document("List").collection("Complete")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str1 = document.getData().toString(); //{score=점수,id=이메일}
                            str1 = str1.substring(str1.indexOf("=")+1); //점수,id=이메일}
                            String score1 = str1.substring(0, str1.indexOf(",")); //점수
                            String id1 = str1.substring(str1.indexOf("=")+1); //이메일}
                            String id2 = id1.substring(0, id1.indexOf("}")); //이메일

                            complete_adapter.setArrayData(id2, score1);
                            recyclerView.setAdapter(complete_adapter);

                        } else {

                        }

                    }
                }
                else {
                }
            }
        });


    }


}
