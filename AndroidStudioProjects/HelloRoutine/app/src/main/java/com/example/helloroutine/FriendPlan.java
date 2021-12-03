package com.example.helloroutine;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Calendar;

public class FriendPlan extends AppCompatActivity {

    TextView clickID ,txtDate;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;




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

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog.OnDateSetListener mDateSetListener =
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String Year = String.valueOf(year);
                                //한 자리수 날짜 오류 수정
                                String month = null;
                                if(String.valueOf(monthOfYear+1).length()<2){
                                    month = "0"+String.valueOf(monthOfYear+1);
                                }
                                else{
                                    month = String.valueOf(monthOfYear+1);
                                }
                                String day = null;
                                if(String.valueOf(dayOfMonth).length()<2){
                                    day = "0"+String.valueOf(dayOfMonth);
                                }
                                else{
                                    day = String.valueOf(dayOfMonth);
                                }
                                String date = Year+"."+month+"."+day;

                                txtDate.setText(date);
                                showPlanList(id, date);
                            }

                        };
                DatePickerDialog oDialog = new DatePickerDialog(FriendPlan.this, android.R.style.Theme_DeviceDefault_Light_Dialog, mDateSetListener, nYear, nMon, nDay);
                oDialog.show();

            }
        });

    }


    public void showPlanList(String id, String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(id).collection("Plan").document("plan").collection(date)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot document = task.getResult();
                recyclerAdapter.arrayList.clear();
                if(!document.isEmpty()){
                    db.collection("DB").document(id).collection("Plan").document("plan").collection(date)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    //DB 필드명 표시 지워서 데이터 값만 표시
                                    String str2 = document.getData().toString();
                                    str2 = str2.substring(str2.indexOf("=")+1);
                                    String y = str2.substring(0, str2.indexOf("}"));

                                    recyclerAdapter.setArrayData(y);
                                    recyclerView.setAdapter(recyclerAdapter);
                                }


                            } else {
                                Toast.makeText(getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    recyclerAdapter.setArrayData("일정이 없습니다.");
                    recyclerView.setAdapter(recyclerAdapter);
                }

            }

        });

    }

}

