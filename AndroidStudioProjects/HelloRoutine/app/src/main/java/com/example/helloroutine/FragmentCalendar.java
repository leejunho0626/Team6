package com.example.helloroutine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentCalendar extends Fragment {

    GridView grid;
    GridAdapter adt;
    Calendar cal;
    TextView date, txt1;
    ImageButton pre, next, btnAdd;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SimpleDateFormat format = new SimpleDateFormat ( "yyyy.MM.dd");
    String format_1 = format.format(System.currentTimeMillis());
    RecyclerView recyclerView;
    PlanAdapter planAdapter;
    SwipeRefreshLayout refresh_layout;

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
        planAdapter = new PlanAdapter();
        refresh_layout = view.findViewById(R.id.refresh_layout);

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
        findData(format_2_1);

        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                planAdapter.arrayList.clear();

                // 새로고침 코드를 작성
                findData(format_2_1);
                planAdapter.notifyDataSetChanged();

                // 새로고침 완료시,
                // 새로고침 아이콘이 사라질 수 있게 isRefreshing = false
                refresh_layout.setRefreshing(false);
            }
        });



        planAdapter.setOnItemClicklistener(new OnPlanItemClickListener() {
            @Override
            public void OnItemClick(PlanAdapter.ViewHolder holder, View view, int position) {
                String item = planAdapter.getItem(position);


                if(!item.contains("새로운")){
                    String temp = item.substring(0,item.indexOf(":"));
                    new AlertDialog.Builder(getActivity())
                            .setTitle(format_2_1)
                            .setMessage("· "+item)
                            .setIcon(R.drawable.ic_baseline_today_24)
                            .setPositiveButton("변경", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getActivity(), Add_plan.class);
                                    intent.putExtra("date",format_2_1);
                                    intent.putExtra("exeType",temp);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("DB").document(user.getEmail()).collection("Plan").document("plan").collection(format_2_1).document(temp)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext().getApplicationContext(), "삭제했습니다.", Toast.LENGTH_LONG).show();




                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                }
                            }).show();
                }

            }
        });

        btnAdd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Add_plan.class);
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

                refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        planAdapter.arrayList.clear();

                        // 새로고침 코드를 작성
                        findData(clickDate);
                        planAdapter.notifyDataSetChanged();

                        // 새로고침 완료시,
                        // 새로고침 아이콘이 사라질 수 있게 isRefreshing = false
                        refresh_layout.setRefreshing(false);
                    }
                });

                txt1.setText(clickDate2);

                planAdapter.arrayList.clear();
                //showPlanList(clickDate);
                findData(clickDate);

                btnAdd.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), Add_plan.class);
                        intent.putExtra("date",clickDate);
                        startActivity(intent);

                    }
                });
                planAdapter.setOnItemClicklistener(new OnPlanItemClickListener() {
                    @Override
                    public void OnItemClick(PlanAdapter.ViewHolder holder, View view, int position) {
                        String item = planAdapter.getItem(position);


                        if(!item.contains("새로운")){
                            String temp = item.substring(0,item.indexOf(":"));
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(clickDate)
                                    .setMessage(item)
                                    .setPositiveButton("변경", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Intent intent = new Intent(getActivity(), Add_plan.class);
                                            intent.putExtra("date",clickDate);
                                            intent.putExtra("exeType",temp);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("DB").document(user.getEmail()).collection("Plan").document("plan").collection(clickDate).document(temp)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext().getApplicationContext(), "삭제했습니다.", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });

                                        }
                                    }).show();
                        }
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
            GridItem item = new GridItem(Integer.toString(y), Integer.toString(m));
            adt.add(item);
        }
        // 이번 달 마지막 날
        int lDay = getLastDay(y, m);
        for (int i=1; i<=lDay; i++)
        {
            cal.set(y, cal.get(Calendar.MONTH), i);
            int text = cal.get(Calendar.DAY_OF_WEEK);
            GridItem item = new GridItem(Integer.toString(y), Integer.toString(m), Integer.toString(i), text);
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
        db.collection("DB").document(user.getEmail()).collection("Plan").document("plan").collection(date)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str2 = document.getData().toString();
                        str2 = str2.substring(str2.indexOf("=")+1);
                        String y = str2.substring(0, str2.indexOf("}"));

                        planAdapter.setArrayData(y);
                        recyclerView.setAdapter(planAdapter);
                    }


                } else {
                    Toast.makeText(getContext().getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void findData(String data){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Plan").document("plan").collection(data)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot document = task.getResult();
                if (!document.isEmpty()) {
                    showPlanList(data);

                } else {
                    planAdapter.setArrayData("새로운 일정을 추가하세요.");
                    recyclerView.setAdapter(planAdapter);
                }
            }

        });


    }






}
