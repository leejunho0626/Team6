package com.example.helloroutine;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
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
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import static java.lang.Thread.sleep;

public class Add_plan extends AppCompatActivity {

    TextView exeType, exeTime, clickDate;
    EditText exeNum, exeSet;
    Button btnSave;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    NotificationManager manager;
    NotificationCompat.Builder builder;
    private static String CHANNEL_ID = "TimerPushAlarm";
    private static String CHANEL_NAME = "PushAlarm";
    SharedPreferences spref;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plan);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //다크모드 해제

        exeType = findViewById(R.id.exeType);
        exeNum = findViewById(R.id.exeNUm);
        exeSet = findViewById(R.id.exeSet);
        exeTime = findViewById(R.id.exeTime);
        btnSave = findViewById(R.id.btnSave_plan);
        clickDate = findViewById(R.id.clickDate);

        spref = getSharedPreferences("gref", MODE_PRIVATE);

        String temp1 = spref.getString("push", "사용");


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String date = bundle.getString("date");
        String txtType = bundle.getString("exeType");
        clickDate.setText(date);

        if(txtType==null){
            exeType.setText("선택하기");
            exeType.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final CharSequence[] oItems = {"어깨 운동", "팔 운동", "등 운동", "가슴 운동", "복근 운동", "다리 운동"};

                    AlertDialog.Builder oDialog = new AlertDialog.Builder(Add_plan.this);

                    oDialog.setTitle("해야 할 운동을 선택하세요")
                            .setItems(oItems, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    exeType.setText(oItems[which]);
                                }
                            })
                            .show();

                }
            });
        }
        else {
            //DB 필드명 표시 지워서 데이터 값만 표시
            exeType.setText(txtType);

        }




        exeTime.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener mTimeSetListener =
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                exeTime.setText(hourOfDay + ":" + minute+"분");
                            }
                        };

                TimePickerDialog oDialog = new TimePickerDialog(Add_plan.this, android.R.style.Theme_DeviceDefault_Light_Dialog, mTimeSetListener, 0, 0, false);
                oDialog.show();

            }
        });


        btnSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String edit = exeType.getText().toString()+": "+exeSet.getText().toString()+"세트 "+exeNum.getText().toString()+"회/ "
                        +"예정 시간 : "+exeTime.getText().toString(); //입력한 값
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                String date = bundle.getString("date");
                writeUpload(date, edit, exeType.getText().toString(), temp1);
                Toast.makeText(getApplicationContext(), "저장했습니다.", Toast.LENGTH_LONG).show();
                finish();

            }
        });
    }

    //뒤로 가기 버튼 클릭 시
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

    //목표 설정1
    public void writeUpload(String date, String edit, String type, String setting){
        if(edit.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(edit);
            db.collection("DB").document(user.getEmail()).collection("Plan").document("plan").collection(date).document(type).set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            //
                            loadingTotalPlan(setting);
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

    //추가한 일정 횟수 추가
    public void addTotalPlan(String total){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(total);
        db.collection("DB").document(user.getEmail()).collection("Total").document("PlanCnt").set(userWrite)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //최종 일정 횟수 불러오기
    public void loadingTotalPlan(String setting){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Total").document("PlanCnt")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //문서가 존재하는 경우
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String data = str1.substring(0, str1.indexOf("}")); //일정 쵯수

                        //데이터 값
                        int temp = Integer.parseInt(data)+1; //일정 횟수
                        int value = temp*10; //도전과제
                        int value2 = (int) Math.round(Double.parseDouble(String.valueOf(temp))/30*100); //도전과제
                        String sum = Integer.toString(value+value2);

                        data = Integer.toString(temp);
                        //String distance = listC.toString();
                        addTotalPlan(Integer.toString(temp)); //일정 횟수 추가
                        loadScore(sum);
                        if(temp==10){
                            showNoti("도전과제 완료", "운동 일정 10개 추가", setting);
                        }
                        if(temp==30){
                            showNoti("도전과제 완료", "운동 일정 30개 추가", setting);
                        }
                        if(temp==50){
                            showNoti("도전과제 완료", "운동 일정 50개 추가",setting);
                        }

                    }
                    else {
                        try {
                            addTotalPlan("0"); //문서 생성
                            sleep(1000);
                            db.collection("DB").document(user.getEmail()).collection("Total").document("PlanCnt")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            //DB 필드명 표시 지워서 데이터 값만 표시
                                            String str1 = document.getData().toString();
                                            str1 = str1.substring(str1.indexOf("=")+1);
                                            String data = str1.substring(0, str1.indexOf("}")); //0점
                                            //데이터 값
                                            int temp = Integer.parseInt(data)+1;
                                            int value = temp*10; //도전과제
                                            int value2 = (int) Math.round(Double.parseDouble(String.valueOf(temp))/30*100); //도전과제
                                            String sum = Integer.toString(value+value2);


                                            try {
                                                addTotalPlan(Integer.toString(temp));
                                                sleep(1000);
                                                loadScore(sum);

                                            }   catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        } else {

                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "일정 횟수 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
                else {

                }
            }
        });
    }

    //일정 진행도 점수 저장하기
    public void saveScore(String total){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserRank userRank = new UserRank(user.getEmail(),total);
        db.collection("DB").document(user.getEmail()).collection("Total").document("PlanScore").set(userRank)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                    }
                });

    }

    public void loadScore(String data){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Total").document("PlanScore")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //문서가 존재하는 경우
                    if (document.exists()) {
                        saveScore(data); //업데이트

                    }
                    else {
                        try {
                            saveScore("0");
                            sleep(1000);
                            db.collection("DB").document(user.getEmail()).collection("Total").document("PlanScore")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        //문서가 존재하는 경우
                                        if (document.exists()) {

                                            saveScore(data); //추가할 점수 + 기존 점수

                                        } else {
                                            Toast.makeText(getApplicationContext(), "일정 횟수 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "일정 횟수 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                                    }
                                }

                            });
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "일정 횟수 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void showNoti(String title, String text ,String setting){
        Vibrator vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        Uri ringing = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringing);

        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //버전 오레오 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            );
            builder = new NotificationCompat.Builder(this,CHANNEL_ID); //하위 버전일 경우
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        //알림창 제목
        builder.setContentTitle(title);
        //알림창 메시지
        builder.setContentText(text);
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_stat_name);
        Notification notification = builder.build();

        if(setting.equals("사용")){
            ringtone.play();
            manager.notify(1,notification);
        }

    }
}
