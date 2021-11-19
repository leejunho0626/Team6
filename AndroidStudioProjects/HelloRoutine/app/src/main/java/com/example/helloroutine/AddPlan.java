package com.example.helloroutine;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class AddPlan extends AppCompatActivity {

    TextView exeType, exeTime, clickDate;
    EditText exeNum, exeSet, exeWeight;
    Button btnSave;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_plan);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제

        exeType = findViewById(R.id.exeType);
        exeNum = findViewById(R.id.exeNUm);
        exeSet = findViewById(R.id.exeSet);
        exeWeight = findViewById(R.id.exeWeight);
        exeTime = findViewById(R.id.exeTime);
        btnSave = findViewById(R.id.btnSave_plan);
        clickDate = findViewById(R.id.clickDate);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String date = bundle.getString("date");
        clickDate.setText(date);

        exeType.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] oItems = {"팔 운동", "어깨 운동", "다리 운동", "가슴 운동", "등 운동"};

                AlertDialog.Builder oDialog = new AlertDialog.Builder(AddPlan.this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                oDialog.setTitle("해야 할 운동을 선택하세요")
                        .setItems(oItems, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                exeType.setText(oItems[which]);
                            }
                        })
                        .setCancelable(false)
                        .show();

            }
        });
        exeTime.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener mTimeSetListener =
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                exeTime.setText(hourOfDay + ":" + minute+"분");
                            }
                        };

                TimePickerDialog oDialog = new TimePickerDialog(AddPlan.this, android.R.style.Theme_DeviceDefault_Light_Dialog, mTimeSetListener, 0, 0, false);
                oDialog.show();

            }
        });


        btnSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit = exeType.getText().toString()+" : "+exeNum.getText().toString()+"회 "+exeSet.getText().toString()+"세트 "
                        +exeWeight.getText().toString()+"kg "+exeTime.getText().toString()+"시간"; //입력한 값
                //Toast.makeText(getApplicationContext(), edit, Toast.LENGTH_LONG).show();
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                String date = bundle.getString("date");
                writeUpload(date, edit, exeType.getText().toString());

            }
        });



    }
    @Override
    public void onBackPressed(){


    }




    //목표 설정1
    public void writeUpload(String date, String edit, String type){

        if(edit.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(edit);
            db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(date).document(type).set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }else{

        }
    }

}
