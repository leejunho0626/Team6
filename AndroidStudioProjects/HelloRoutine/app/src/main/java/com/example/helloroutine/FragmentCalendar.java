package com.example.helloroutine;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.auth.Session;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class FragmentCalendar extends Fragment {

    GridView grid;
    GridAdapter adt;
    Calendar cal;
    TextView date, txt1;
    ImageButton pre, next, btnAdd;
    ScrollView dialogView;
    EditText exeType, exeTime, exeNum, exeSet, exeWeight;
    Context mContext;
    boolean img;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ProgressDialog customProgressDialog;
    String x;
    Dialog dialog;
    SimpleDateFormat format = new SimpleDateFormat ( "yyyy.MM.dd일");
    String format_1 = format.format(System.currentTimeMillis());
    final CharSequence[] oItems = {"팔 운동", "어깨 운동", "다리 운동", "가슴 운동", "등 운동"};


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);

        adt = new GridAdapter(getActivity()); //어댑터 객체 생성
        grid = view.findViewById(R.id.grid); //그리드뷰 객체 참조
        date = view.findViewById(R.id.date);
        pre = view.findViewById(R.id.pre);
        next = view.findViewById(R.id.next);
        txt1 = view.findViewById(R.id.calPlan1);
        btnAdd = view.findViewById(R.id.btnAddPlan);

           //달력표시
        cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        cal.set(y, m - 1, 1);
        show();

        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(getActivity());
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        txt1.setText(format_1);



        btnAdd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialog(clickDate);
                AddPlan addPlan= new AddPlan();
                //addPlan.writeUpload(clickDate, "test11", user.getUid().toString());
                Intent intent = new Intent(getActivity(), AddPlan.class); //메인화면으로 이동
                intent.putExtra("date",format_1);
                startActivity(intent);


            }
        });

        
        //날짜 클릭 시
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 로딩창 보여주기
                customProgressDialog.show();
                //한 자리수 날짜 오류 수정
                String month = null;
                if(adt.mItem.get(i).month().length()<2){
                    month = "0"+adt.mItem.get(i).month();
                }
                else{
                    month = adt.mItem.get(i).month();
                }
                String day = null;
                if(adt.mItem.get(i).day().length()<2){
                    day = "0"+adt.mItem.get(i).day();
                }
                else{
                    day = adt.mItem.get(i).day();
                }
                //클릭한 날짜 데이터
                String clickDate = adt.mItem.get(i).year()+"."+month+"."+day+"일";

                txt1.setText(clickDate);


                writeDownload(clickDate);
                writeDownload2(clickDate);
                writeDownload3(clickDate);

                btnAdd.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       //showDialog(clickDate);
                        AddPlan addPlan= new AddPlan();
                        //addPlan.writeUpload(clickDate, "test11", user.getUid().toString());
                        Intent intent = new Intent(getActivity(), AddPlan.class); //메인화면으로 이동
                        intent.putExtra("date",clickDate);
                        startActivity(intent);

                    }
                });

                //로딩 쓰레드
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
                        timer.schedule(task, 1500);
                    }
                });
                thread.start();

            }
        });

        pre.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
              pre();
            }
        });
        next.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        return view;
    }


    //달력 표시
    private void show()
    {
        adt.clear();
        int y = cal.get(Calendar.YEAR);
        int m =cal.get(Calendar.MONTH)+1;
        date.setText(y+"-"+m);
        // 1일의 요일
        int fText = cal.get(Calendar.DAY_OF_WEEK);
        // 빈 날짜 넣기
        for (int i = 1; i<fText; i++)
        {
            boolean img = false;
            GridItem item = new GridItem(Integer.toString(y), Integer.toString(m), img);
            adt.add(item);
        }
        // 이번 달 마지막 날
        int lDay = getLastDay(y, m);
        for (int i=1; i<=lDay; i++)
        {
            img = false;
            cal.set(y, cal.get(Calendar.MONTH), i);
            int text = cal.get(Calendar.DAY_OF_WEEK);
            GridItem item = new GridItem(Integer.toString(y), Integer.toString(m), Integer.toString(i), text, img);
            adt.add(item);
        }
        grid.setAdapter(adt);
    }

    // 이전 달
    public void pre()
    {
        int y = cal.get(Calendar.YEAR);
        int m =cal.get(Calendar.MONTH)-1;
        cal.set(y, m, 1);
        show();
    }
    // 다음 달
    public void next()
    {
        int y = cal.get(Calendar.YEAR);
        int m =cal.get(Calendar.MONTH)+1;
        cal.set(y, m, 1);
        show();
    }
    // 특정월의 마지막 날짜
    private int getLastDay(int year, int month)
    {
        Date d = new Date(year, month, 1);
        d.setHours(d.getDay()-1*24);
        SimpleDateFormat f = new SimpleDateFormat("dd");
        return Integer.parseInt(f.format(d));
    }

    //추가한 일정 횟수 추가
    public void addTotalPlan(String total){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(total);
        db.collection("DB").document("User").collection(user.getUid()).document("TotalPlan").set(userWrite)
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
    //최종 일정 횟수 불러오기
    public void loadingTotalPlan(){

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
                        //데이터 값
                        int temp = Integer.parseInt(x)+1;
                        x = Integer.toString(temp);
                        //String distance = listC.toString();
                        addTotalPlan(x);

                    }
                    else {
                        addTotalPlan("0"); //문서 생성
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
                                        //데이터 값
                                        int temp = Integer.parseInt(x)+1;
                                        x = Integer.toString(temp);
                                        //String distance = listC.toString();
                                        addTotalPlan(x);

                                    } else {


                                    }
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), "일정 횟수 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                                }
                            }

                        });
                    }
                }
                else {


                }
            }

        });
    }



    //목표 설정1
    public void writeUpload(String date, String edit){

        if(edit.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(edit);
            db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(date).document("1").set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Toast.makeText(getActivity().getApplicationContext(), "묙표가 저장되었습니다.", Toast.LENGTH_LONG).show();


                            loadingTotalPlan();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "내용을 다시 입력하세요.", Toast.LENGTH_LONG).show();
        }
    }

    //목표 설정2
    public void writeUpload2(String date, String edit){

        if(edit.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(edit);
            db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(date).document("2").set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Toast.makeText(getActivity().getApplicationContext(), "묙표가 저장되었습니다.", Toast.LENGTH_LONG).show();

                            loadingTotalPlan();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "내용을 다시 입력하세요.", Toast.LENGTH_LONG).show();
        }
    }

    //목표 설정3
    public void writeUpload3(String date, String edit){

        if(edit.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(edit);
            db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(date).document("3").set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Toast.makeText(getActivity().getApplicationContext(), "묙표가 저장되었습니다.", Toast.LENGTH_LONG).show();

                            loadingTotalPlan();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "내용을 다시 입력하세요.", Toast.LENGTH_LONG).show();
        }
    }

    //목표 설정 메뉴1
    public void showDialog(String date) {

        dialogView = (ScrollView) View.inflate(getActivity(),R.layout.dialog_plan,null);
        //다이얼로그 메뉴
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle(date);
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exeType=dialogView.findViewById(R.id.exeType);
                exeNum=dialogView.findViewById(R.id.exeNUm);
                exeSet=dialogView.findViewById(R.id.exeSet);
                exeWeight=dialogView.findViewById(R.id.exeWeight);
                exeTime=dialogView.findViewById(R.id.exeTime);


                String edit = exeType.getText().toString()+" : "+exeNum.getText().toString()+"회 "+exeSet.getText().toString()+"세트 "
                        +exeWeight.getText().toString()+"kg "+exeTime.getText().toString()+"시간"; //입력한 값
                if(exeType.getText().toString().length()>0){
                    builder1.setTitle("저장하시겠습니까?");
                    builder1.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            writeUpload(date, edit); //작성한 글 DB로 저장
                        }
                    });
                    builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Toast.makeText(getActivity().getApplicationContext(), "취소되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder1.show();
                }
                else{
                    builder1.setCancelable(false);
                    Toast.makeText(getActivity().getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_LONG).show();
                }

            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity().getApplicationContext(), "취소되었습니다.", Toast.LENGTH_LONG).show();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    //목표 설정 메뉴2
    public void showDialog2(String date) {

        dialogView = (ScrollView) View.inflate(getActivity(),R.layout.dialog_plan,null);
        //다이얼로그 메뉴
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle("운동 목표 설정");
        builder.setMessage(date);
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exeType=dialogView.findViewById(R.id.exeType);
                exeNum=dialogView.findViewById(R.id.exeNUm);
                exeSet=dialogView.findViewById(R.id.exeSet);
                exeWeight=dialogView.findViewById(R.id.exeWeight);
                exeTime=dialogView.findViewById(R.id.exeTime);
                String edit = exeType.getText().toString()+" : "+exeNum.getText().toString()+"회 "+exeSet.getText().toString()+"세트 "
                        +exeWeight.getText().toString()+"kg "+exeTime.getText().toString()+"시간"; //입력한 값
                if(edit.length()>0){
                    builder1.setTitle("");
                    builder1.setMessage("저장하시겠습니까?");
                    builder1.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            writeUpload2(date, edit); //작성한 글 DB로 저장
                        }
                    });
                    builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getActivity().getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder1.show();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_LONG).show();
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

    //목표 설정 메뉴3
    public void showDialog3(String date) {


        dialogView = (ScrollView) View.inflate(getActivity(),R.layout.dialog_plan,null);
        //다이얼로그 메뉴
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        builder.setTitle("운동 목표 설정");
        builder.setMessage(date);
        builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exeType=dialogView.findViewById(R.id.exeType);
                exeNum=dialogView.findViewById(R.id.exeNUm);
                exeSet=dialogView.findViewById(R.id.exeSet);
                exeWeight=dialogView.findViewById(R.id.exeWeight);
                exeTime=dialogView.findViewById(R.id.exeTime);
                exeType.setClickable(false);
                exeType.setFocusable(false);
                String edit = exeType.getText().toString()+" : "+exeNum.getText().toString()+"회 "+exeSet.getText().toString()+"세트 "
                        +exeWeight.getText().toString()+"kg "+exeTime.getText().toString()+"시간"; //입력한 값
                if(edit.length()>0){
                    builder1.setTitle("");
                    builder1.setMessage("저장하시겠습니까?");
                    builder1.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            writeUpload3(date, edit); //작성한 글 DB로 저장
                        }
                    });
                    builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getActivity().getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder1.show();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_LONG).show();
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

    //설정한 목표 표시1
    public void writeDownload(String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(date).document("1")
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




                    } else {


                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "목표 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //설정한 목표 표시2
    public void writeDownload2(String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(date).document("2")
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


                    } else {


                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "목표 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //설정한 목표 표시3
    public void writeDownload3(String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(date).document("3")
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
                    } else {

                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "목표 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
