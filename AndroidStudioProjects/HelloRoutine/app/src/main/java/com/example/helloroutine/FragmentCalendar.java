package com.example.helloroutine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentCalendar extends Fragment {

    GridView grid;
    GridAdapter adt;
    Calendar cal;
    TextView date, txt1, txt2, txt3, txt4;
    ImageButton pre, next, addPlan;
    LinearLayout dialogView;
    EditText exeType, exeNum, exeSet, exeWeight, exeTime;
    Button btnChange, btnRemove, btnChange2, btnRemove2, btnChange3, btnRemove3;
    Context mContext;
    boolean img;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    static String clickDB;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_calendar, container, false);

        adt = new GridAdapter(getActivity()); //어댑터 객체 생성
        grid = view.findViewById(R.id.grid); //그리드뷰 객체 참조
        date = view.findViewById(R.id.date);
        pre = view.findViewById(R.id.pre);
        next = view.findViewById(R.id.next);
        txt1 = view.findViewById(R.id.calPlan1);
        txt2 = view.findViewById(R.id.calPlan2);
        txt3 = view.findViewById(R.id.calPlan3);
        txt4 = view.findViewById(R.id.calPlan4);
        btnChange = view.findViewById(R.id.btnChange);
        btnRemove = view.findViewById(R.id.btnRemove);
        btnChange2 = view.findViewById(R.id.btnChange2);
        btnRemove2 = view.findViewById(R.id.btnRemove2);
        btnChange3 = view.findViewById(R.id.btnChange3);
        btnRemove3 = view.findViewById(R.id.btnRemove3);
        addPlan = view.findViewById(R.id.addPlan);
        txt2.setVisibility(view.INVISIBLE);
        txt3.setVisibility(view.INVISIBLE);
        txt4.setVisibility(view.INVISIBLE);
        btnChange.setVisibility(view.INVISIBLE);
        btnRemove.setVisibility(view.INVISIBLE);
        btnChange2.setVisibility(view.INVISIBLE);
        btnRemove2.setVisibility(view.INVISIBLE);
        btnChange3.setVisibility(view.INVISIBLE);
        btnRemove3.setVisibility(view.INVISIBLE);
        addPlan.setVisibility(view.INVISIBLE);

        //달력표시
        cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        cal.set(y, m - 1, 1);
        show();
        
        //날짜 클릭 시
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickDate = adt.mItem.get(i).year()+"."+adt.mItem.get(i).month()+"."+adt.mItem.get(i).day()+"일";
                //Toast.makeText(getActivity(), clickDate, Toast.LENGTH_LONG).show();
                clickDB = clickDate;
                txt1.setText(clickDB);
                txt2.setVisibility(view.VISIBLE);
                writeDownload(clickDate);
                btnChange.setVisibility(view.VISIBLE);
                btnRemove.setVisibility(view.VISIBLE);
                btnChange2.setVisibility(view.VISIBLE);
                btnRemove2.setVisibility(view.VISIBLE);
                btnChange3.setVisibility(view.VISIBLE);
                btnRemove3.setVisibility(view.VISIBLE);
                addPlan.setVisibility(view.VISIBLE);

                addPlan.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        showDialog(clickDB);
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

    //목표 설정
    public void writeUpload(String date, String edit){

        if(edit.length()>0){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            UserWrite userWrite = new UserWrite(edit);
            db.collection("DB").document("User").collection(user.getUid()).document(date).set(userWrite)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            Toast.makeText(getActivity().getApplicationContext(), "묙표가 저장되었습니다.", Toast.LENGTH_LONG).show();
                            txt2.setText(" "+edit);
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

    //글 작성 메뉴
    public void showDialog(String date) {

        dialogView = (LinearLayout) View.inflate(getActivity(),R.layout.dialog_plan,null);
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
                            writeUpload(date, edit); //작성한 글 DB로 저장
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

    //설정한 목표 표시
    public void writeDownload(String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document(date)
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
                        txt2.setText(" "+x);
                    } else {
                        txt2.setText(" 새로운 목표을 설정하세요.");

                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "목표 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
