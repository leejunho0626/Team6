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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.Session;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
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
    boolean img;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String x;
    SimpleDateFormat format = new SimpleDateFormat ( "yyyy.MM.dd");
    String format_1 = format.format(System.currentTimeMillis());
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);

        adt = new GridAdapter(getActivity()); //어댑터 객체 생성
        grid = view.findViewById(R.id.grid); //그리드뷰 객체 참조
        date = view.findViewById(R.id.date);
        pre = view.findViewById(R.id.pre);
        next = view.findViewById(R.id.next);
        txt1 = view.findViewById(R.id.calPlan1);
        btnAdd = view.findViewById(R.id.btnAddPlan);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyceler_clickPlan);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)) ;
        recyclerAdapter = new RecyclerAdapter();

        //달력표시
        cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        cal.set(y, m - 1, 1);
        show();

        SimpleDateFormat format2 = new SimpleDateFormat ( "MM월 dd일");
        SimpleDateFormat format3 = new SimpleDateFormat ( "yyyy.MM.dd");
        String format_1_1 = format2.format(System.currentTimeMillis());
        String format_2_1 = format3.format(System.currentTimeMillis());

        txt1.setText(format_1_1);
        showPlanList(format_2_1);



        btnAdd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddPlan.class);
                intent.putExtra("date",format_1);
                startActivity(intent);

            }
        });



        //날짜 클릭 시
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
                String clickDate = adt.mItem.get(i).year()+"."+month+"."+day;
                String clickDate2 = month+"월 "+day+"일";

                txt1.setText(clickDate2);

                recyclerAdapter.arrayList.clear();
                showPlanList(clickDate);

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




    //일정 표시
    public void showPlanList(String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(date)
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
                    Toast.makeText(getContext().getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }





}
