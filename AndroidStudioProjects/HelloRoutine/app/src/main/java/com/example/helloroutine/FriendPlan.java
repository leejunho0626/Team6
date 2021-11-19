package com.example.helloroutine;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class FriendPlan extends AppCompatActivity {

    TextView clickID ,txtDate;
    static RecyclerView recyclerView;
    static RecyclerAdapter recyclerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_plan);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제

        clickID = findViewById(R.id.clickID);
        txtDate = findViewById(R.id.txtDate);
        recyclerView = (RecyclerView)findViewById(R.id.recyceler_frPlan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ;
        recyclerAdapter = new RecyclerAdapter();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");
        clickID.setText(id);

        Calendar c = Calendar.getInstance();
        int nYear = c.get(Calendar.YEAR);
        int nMon = c.get(Calendar.MONTH);
        int nDay = c.get(Calendar.DAY_OF_MONTH);

        //일반 로그인 버튼 클릭
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog.OnDateSetListener mDateSetListener =
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                String strDate = String.valueOf(year) + ".";
                                strDate += String.valueOf(monthOfYear+1) + ".";
                                strDate += String.valueOf(dayOfMonth);

                                txtDate.setText(strDate);
                                showPlanList(id, strDate);
                            }

                        };

                DatePickerDialog oDialog = new DatePickerDialog(FriendPlan.this, android.R.style.Theme_DeviceDefault_Light_Dialog,
                        mDateSetListener, nYear, nMon, nDay);
                oDialog.show();

            }
        });

    }

    //일정 표시
    public void showPlanList(String id, String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("ID").collection(id).document("uid")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String y = str1.substring(0, str1.indexOf("}")); //uid

                        friendPlan(y, date);

                    }
                    else{

                    }
                }
            }

        });

    }

    //일정 표시
    public void friendPlan(String uid, String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(uid).document("Plan").collection(date)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str2 = document.getData().toString();
                            str2 = str2.substring(str2.indexOf("=")+1);
                            String y = str2.substring(0, str2.indexOf("}"));

                            recyclerAdapter.setArrayData(y);
                            recyclerView.setAdapter(recyclerAdapter);

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

