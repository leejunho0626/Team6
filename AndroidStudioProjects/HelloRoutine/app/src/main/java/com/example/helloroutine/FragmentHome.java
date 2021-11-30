package com.example.helloroutine;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.util.FusedLocationSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;
import static java.lang.Thread.sleep;

public class FragmentHome extends Fragment {

    TextView tmp, pty, pcp, sno, sky, pop, time3;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static LocationSource mLocationSource;
    GridView grid;
    GridAdapter adt;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ProgressDialog customProgressDialog;
    private static Animation fab_open, fab_close;
    private static Boolean isFabOpen = false;
    private static FloatingActionButton fab, fab1, fab2, fab3;
    static  LinearLayout linearLayout;
    String cnt;
    String x;
    SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd");
    LocationManager mLocMan; // 위치 관리자

    private static String CHANNEL_ID = "TimerPushAlarm";
    private static String CHANEL_NAME = "PushAlarm";
    NotificationManager manager;
    NotificationCompat.Builder builder;
    SharedPreferences spref;
    static boolean check = true;
    RecyclerView recyclerView,recyclerView2;
    RecyclerAdapter recyclerAdapter;
    ChallengeAdapter challenge_adapter;
    String base_time = time();
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> list2 = new ArrayList<>();

    ImageView im1, im2, im3;

    String nx=null;
    String ny=null;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        tmp = (TextView) view.findViewById(R.id.tmp);
        pty = (TextView) view.findViewById(R.id.pty);
        pcp = (TextView) view.findViewById(R.id.pcp);
        sno = (TextView) view.findViewById(R.id.sno);
        sky = (TextView) view.findViewById(R.id.sky);
        pop = (TextView) view.findViewById(R.id.pop);
        time3 = (TextView) view.findViewById(R.id.time3);
        adt = new GridAdapter(getActivity()); //어댑터 객체 생성
        grid = view.findViewById(R.id.grid); //그리드뷰 객체 참조
        linearLayout = view.findViewById(R.id.layout_Guid);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyceler_todaytPlan);
        recyclerView2 = (RecyclerView)view.findViewById(R.id.recyceler_challenge);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)) ;
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false)) ;
        recyclerAdapter = new RecyclerAdapter();
        challenge_adapter = new ChallengeAdapter();
        im1 = view.findViewById(R.id.im1);
        im2 = view.findViewById(R.id.im2);
        im3 = view.findViewById(R.id.im3);
        mLocMan = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        spref = getActivity().getSharedPreferences("gref", MODE_PRIVATE);

        String temp1 = spref.getString("push", "사용");

        //로딩화면 객체 생성
        customProgressDialog = new ProgressDialog(getActivity());
        //로딩화면을 투명하게 설정
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // 로딩화면 보여주기
        customProgressDialog.show();

        //플로팅 메뉴 설정
        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);

        fab.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                anim();
            }
        });
        fab1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                anim();
                Intent intent = new Intent(getActivity(), Route.class);
                startActivity(intent);
            }
        });
        fab2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                anim();
                Intent intent = new Intent(getActivity(), Timer_function.class);
                startActivity(intent);
            }
        });
        fab3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                anim();
                Intent intent = new Intent(getActivity(), WalkCnt.class);
                startActivity(intent);
            }
        });

        //운동 가이드 클릭
        linearLayout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //

                Intent intent = new Intent(getActivity(), Guide.class);
                startActivity(intent);
            }
        });

        friendRQ(temp1);
        loadDate(temp1);
        addList();

        //현재 날짜

        SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy.MM.dd");
        String format_time2 = format2.format (System.currentTimeMillis());

        //목표 표시
        showPlanList(format_time2);

        //도전과제 표시
        totalDistance();

        showWeather();

        recommend();

        //totalAttendance(format_1);

        //로딩화면 종료
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

        return view;
    }

    //출석 일수 저장하기
    public void saveCnt(String total){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(total);
        db.collection("DB").document(user.getEmail()).collection("Total").document("AttendanceCnt").set(userWrite)
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

    //출석 점수 저장하기
    public void saveDate(String total){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(total);
        db.collection("DB").document(user.getEmail()).collection("Total").document("AttendanceLast").set(userWrite)
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
    public static String caldate(String nowdate, String secdate, String cnt) {
        SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd");

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        boolean check = true;

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(nowdate);
            d2 = format.parse(secdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal1.setTime(d1);
        cal2.setTime(d2);
        cal2.add(Calendar.DATE, 1);
        if(check)
        {
            if(format.format(cal1.getTime()).equals(format.format(cal2.getTime()))) {
                int temp = Integer.parseInt(cnt);
                temp ++;
                cnt = Integer.toString(temp);
                check = false;


            }
            cal2.add(Calendar.DATE, -1);
            if(format.format(cal1.getTime()).equals(format.format(cal2.getTime())));
            else if(check) cnt = "1";
        }



        return cnt;

    }

    //불러오기
    public void loadDate(String setting){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Total").document("AttendanceLast")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String x = str1.substring(0, str1.indexOf("}")); //날짜

                        SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd");
                        String format_1 = format.format(System.currentTimeMillis());
                        saveDate(format_1);

                        //cnt 값 들어오기
                        db.collection("DB").document(user.getEmail()).collection("Total").document("AttendanceCnt")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str1 = document.getData().toString();
                                        str1 = str1.substring(str1.indexOf("=")+1);
                                        cnt = str1.substring(0, str1.indexOf("}")); //cnt 값

                                        String num=caldate(format_1,x,cnt);
                                        saveCnt(num);
                                        if(num.equals("3")){
                                            showNoti("도전과제 완료","출석 횟수 3일", setting);
                                        }

                                    } else {

                                    }
                                }
                                else {


                                }
                            }
                        });



                    } else {
                        saveCnt("1");
                        SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd");
                        String format_1 = format.format(System.currentTimeMillis());
                        saveDate(format_1);
                    }
                }
                else {


                }
            }
        });
    }
    public static int checkscore(int score)
    {
        if(score>100)
        {
            score = 100;

            return score;
        }
        return score;


    }


    //플로팅메뉴 애니메이션
    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }
    //도전과제
    public void totalDistance(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Total").document("AttendanceCnt")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        String day = str1.substring(0, str1.indexOf("}"));

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
                                        String plan = str1.substring(0, str1.indexOf("}"));

                                        db.collection("DB").document(user.getEmail()).collection("Total").document("DistanceCnt")
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                                        String str1 = document.getData().toString();
                                                        str1 = str1.substring(str1.indexOf("=")+1);
                                                        String distance = str1.substring(0, str1.indexOf("}"));

                                                        showChallengeList(plan, distance, day);

                                                    } else {

                                                        showChallengeList(plan, "0", day);
                                                    }
                                                }
                                                else {

                                                }
                                            }
                                        });

                                    } else {

                                        db.collection("DB").document(user.getEmail()).collection("Total").document("DistanceCnt")
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                                        String str1 = document.getData().toString();
                                                        str1 = str1.substring(str1.indexOf("=")+1);
                                                        String distance = str1.substring(0, str1.indexOf("}"));

                                                        showChallengeList("0", distance, day);

                                                    } else {
                                                        showChallengeList("0", "0", day);
                                                    }
                                                }
                                                else {

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
                    else {

                    }
                }
                else {
                }
            }
        });

    }




    //오늘의 일정 표시
    public void showPlanList(String today){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Plan").document("plan").collection(today)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            String str = document.getId().toString();

                            recyclerAdapter.setArrayData(str);
                            recyclerView.setAdapter(recyclerAdapter);

                        }
                        else{

                        }

                    }

                } else {
                    Toast.makeText(getContext().getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    public void showChallengeList(String plan ,String distance, String today) {



        int value1 = Integer.parseInt(plan)*10;
        value1 = checkscore(value1);
        int value2 = (int) Math.round(Double.parseDouble(plan)/30*100);
        value2 = checkscore(value2);
        int value3 = (int) Math.round(Double.parseDouble(plan)/50*100);
        value3 = checkscore(value3);
        int dis1 = (int) Math.round(Double.parseDouble(distance)/1*100);
        dis1 = checkscore(dis1);
        int dis2 = (int) Math.round(Double.parseDouble(distance)/3*100);
        dis2 = checkscore(dis2);
        int dis3 = (int) Math.round(Double.parseDouble(distance)/5*100);
        dis3 = checkscore(dis3);
        int dis4 = (int) Math.round(Double.parseDouble(distance)/42.195*100);
        dis4 = checkscore(dis4);
        int cnt = (int) Math.round(Double.parseDouble(today)/3*100);
        cnt = checkscore(cnt);
        int cnt2 = (int) Math.round(Double.parseDouble(today)/7*100);
        cnt2 = checkscore(cnt2);
        int cnt3 = (int) Math.round(Double.parseDouble(today)/15*100);
        cnt3 = checkscore(cnt3);

        challenge_adapter.arrayList.clear();
        challenge_adapter.arrayList2.clear();

        int finalValue = value1;
        int finalValue2 = value2;
        int finalValue3 = value3;
        int finalDis1 = dis1;
        int finalDis2 = dis2;
        int finalDis3 = dis3;
        int finalDis4 = dis4;
        int finalCnt1 = cnt;
        int finalCnt2 = cnt2;
        int finalCnt3 = cnt3;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Challenge").document("List").collection("Favorite")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot document = task.getResult();

                if (!document.isEmpty()) {
                    for (QueryDocumentSnapshot document1 : task.getResult()) {

                        if (document1.getId().equals("운동 일정 10개 추가")) {
                            challenge_adapter.setArrayData(list.get(0), finalValue);
                        }

                        else if(document1.getId().toString().equals("운동 일정 30개 추가")){
                            challenge_adapter.setArrayData(list.get(1), finalValue2);
                        }
                        else if(document1.getId().toString().equals("운동 일정 50개 추가")){
                            challenge_adapter.setArrayData(list.get(2), finalValue3);
                        }
                        else if(document1.getId().toString().equals("걷거나 뛴 거리 1km")){
                            challenge_adapter.setArrayData(list.get(3), finalDis1);
                        }
                        else if(document1.getId().toString().equals("걷거나 뛴 거리 3km")){
                            challenge_adapter.setArrayData(list.get(4), finalDis2);
                        }
                        else if(document1.getId().toString().equals("걷거나 뛴 거리 5km")){
                            challenge_adapter.setArrayData(list.get(5), finalDis3);
                        }
                        else if(document1.getId().toString().equals("누적 42.195km 달성")){
                            challenge_adapter.setArrayData(list.get(6), finalDis4);
                        }
                        else if(document1.getId().toString().equals("출석 횟수 3일")){
                            challenge_adapter.setArrayData(list.get(7), finalCnt1);
                        }
                        else if(document1.getId().toString().equals("출석 횟수 7일")){
                            challenge_adapter.setArrayData(list.get(8), finalCnt2);
                        }
                        else {
                            challenge_adapter.setArrayData(list.get(9), finalCnt3);
                        }
                        recyclerView2.setAdapter(challenge_adapter);


                    }
                }
                else {

                }
            }

        });

    }
    public void addList(){
        list.clear();
        list.add("운동 일정 10개 추가");
        list.add("운동 일정 30개 추가");
        list.add("운동 일정 50개 추가");
        list.add("걷거나 뛴 거리 1km");
        list.add("걷거나 뛴 거리 3km");
        list.add("걷거나 뛴 거리 5km");
        list.add("누적 42.195km 달성");
        list.add("출석 횟수 3일");
        list.add("출석 횟수 7일");
        list.add("출석 횟수 15일");
    }

    public void showWeather(){

        if(!mLocMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요 ? ");
            builder.setCancelable(true);

            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // GPS 설정 화면으로 이동
                    Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.create().show();


        }

        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd");
        String format_time1 = format1.format (System.currentTimeMillis());


        //날씨
        String strline = "";
        InputStream inputStream = getResources().openRawResource(R.raw.weather_address_final);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        try {
            while (( line = buffreader.readLine()) != null) {
                strline = strline + line;
            }
        } catch (IOException e) {

        }
        StringTokenizer st = new StringTokenizer(strline);
        String we="1";
        int num = 1;
        int i=0;
        String[] add_ = new String[7];
        StringTokenizer st_ = new StringTokenizer(getaddress_());
        while(st_.hasMoreTokens())
        {
            add_[i] =st_.nextToken();
            i++;
        }
        while(st.hasMoreTokens())
        {

            if(num>20) {
                if((num-21)%14 == 0) {
                    we = st.nextToken();
                    num++;
                }
                else if((num-22)%14 == 0) {
                    nx = st.nextToken();
                    num++;
                }
                else if((num-23)%14 == 0) {
                    ny = st.nextToken();
                    num++;
                }
                else {
                    st.nextToken();
                    num++;
                }
            }
            else {
                st.nextToken();
                num++;
            }

            try {
                //Log.d("onpostEx", "출력 값 : "+nx+ny);
                if(we.equals(add_[i-2])) break;
            }

            catch(IndexOutOfBoundsException e){
                nx=Integer.toString(0);
                ny=Integer.toString(0);
            }
        }
        String service_key = "Qq0ncOZYtzwIBrVT5cMMrn%2BKP7nlXYXT52ZR%2FpoM6l%2B5W6ch9YeTupmGsrR6bfVDSvXVdI7Gl6sRzKhrkgQbHw%3D%3D";
        String num_of_rows = "12";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = format_time1; // date
        // time
        if(time().equals("-1")) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -1);
            base_date =  format1.format(cal.getTime());
            base_time = "2300";
            cal.add(Calendar.DATE, 1);
        }
        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?" +
                "serviceKey=" +service_key+
                "&pageNo=" +page_no+
                "&numOfRows=" +num_of_rows+
                "&dataType=" +date_type+
                "&base_date=" +base_date+
                "&base_time=" +base_time+
                "&nx=" +nx+
                "&ny=" +ny;
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute(); //날씨 실행
    }

    public void recommend(){
//상체
        int[] images = new int[]{
                R.drawable.crunch2, R.drawable.bicyt1, R.drawable.rt11,R.drawable.ratpulldown1,R.drawable.packdeckfly1,R.drawable.packdecklateral1};

        //하체
        int[] images2 = new int[]{
                R.drawable.backlunge, R.drawable.highknees, R.drawable.squart,R.drawable.anglelegpress1,R.drawable.hacksquart1,R.drawable.leg1};


        //전신
        int[] images3 = new int[]{
                R.drawable.flan, R.drawable.flankjack,R.drawable.mt1,R.drawable.sideflankwalk,R.drawable.bantoverowing1,R.drawable.deadlift1,R.drawable.hangingleg,R.drawable.sitlow};



        int randomNum = (int) (Math.random() * 6);
        im1.setImageResource(images[randomNum]);

        int randomNum2 = (int) (Math.random() * 6);
        im2.setImageResource(images2[randomNum2]);

        int randomNum3 = (int) (Math.random() * 8);
        im3.setImageResource(images3[randomNum3]);


    }

    public void friendRQ(String setting){
        list2.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document(user.getEmail()).collection("Receive")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            //DB 필드명 표시 지워서 데이터 값만 표시
                            list2.add(document.getId());
                            if(list2.size()>1){
                                showNoti("친구 요청 ", Integer.toString(list2.size())+"명의 친구 요청이 있습니다.", setting);
                            }
                            else{
                                showNoti("친구 요청 ", document.getId()+"님의 친구 요청이 있습니다.", setting);
                            }


                        } else {

                        }

                    }

                } else {

                }
            }
        });
    }

    public void showNoti(String title, String text ,String setting){
        Vibrator vib = (Vibrator)getActivity().getSystemService(VIBRATOR_SERVICE);
        Uri ringing = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Ringtone ringtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(), ringing);

        builder = null;
        manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE); //버전 오레오 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            );
            builder = new NotificationCompat.Builder(getActivity(),CHANNEL_ID); //하위 버전일 경우
        } else {
            builder = new NotificationCompat.Builder(getActivity());
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




    //날씨
    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
            result = requestHttpConnection.request(url, values);

            return result;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String TMP = null;   // 1시간 기온
            String PTY = null;   // 강수형태 없음 0 비 1 눈비 2 눈 3 소나기 4
            String PCP = null;   // 1시간 강수량
            String SNO = null;   // 1시간 적설량
            String SKY = null;   // 하늘 상태 // 맑음 1 구름많음 3 흐림 4
            String POP = null;   // 강수확률
            int cnt = 1;


            try {
                StringTokenizer st = new StringTokenizer(s, "\"", true);

                while(st.hasMoreTokens()) {
                    if(cnt==91) TMP = st.nextToken();
                    else if(cnt==371) SKY = st.nextToken();
                    else if(cnt==427) PTY = st.nextToken();
                    else if(cnt==483) POP = st.nextToken();
                    else if(cnt==595) PCP = st.nextToken();
                    else if(cnt==707) SNO = st.nextToken();
                    else st.nextToken();
                    cnt++;
                }
                tmp.setText(TMP+"도");
                if(PTY.equals("0")) {
                    pty.setText("");
                    if(SKY.equals("1"))
                        sky.setText("맑음");
                    if(SKY.equals("3"))
                        sky.setText("구름 많음");
                    if(SKY.equals("4"))
                        sky.setText("흐림");
                }

                if(PTY.equals("1")) {
                    pty.setText("비");
                    pcp.setText("강수량 : " +PCP);
                }
                if(PTY.equals("2")) {
                    pty.setText("눈비");
                    pcp.setText("강수량 : " +PCP);
                    sno.setText("적설량 : " +SNO);
                }
                if(PTY.equals("3")) {
                    pty.setText("눈");
                    sno.setText("적설량 : " +SNO);
                }
                if(PTY.equals("4")) {
                    pty.setText("소나기");
                    pcp.setText("강수량 : " +PCP);
                }
                pop.setText("강수확률 : "+POP+"%");
                time3.setText("발표 기준 : " + base_time.substring(0,2) + "시");
                //Log.d("onpostEx", "출력 값 : "+s);


            } catch (NullPointerException e) {

                time3.setText("기상정보를 불러올 수 없습니다.");
                tmp.setText("");

            }
        }
    }

    public static String time()
    {
        String time_;
        SimpleDateFormat format2 = new SimpleDateFormat ( "HHmm");
        String format_time2 = format2.format (System.currentTimeMillis());
        int intTime = Integer.parseInt(format_time2);
        if(intTime<200) time_ = "-1";
        else if(intTime<500) time_ = "0200";
        else if(intTime<800) time_ = "0500";
        else if(intTime<1100) time_ = "0800";
        else if(intTime<1400) time_ = "1100";
        else if(intTime<1700) time_ = "1400";
        else if(intTime<2000) time_ = "1700";
        else if(intTime<2300) time_ = "2000";
        else time_ = "2300";
        return time_;
    }

    //위치
    public String getaddress_()
    {
        GpsTracker gpsTracker = new GpsTracker(getActivity());
        double latitude = gpsTracker.getLatitude();// 위도
        double longitude = gpsTracker.getLongitude(); //경도 //필요시 String address = getCurrentAddress(latitude, longitude); ex) 대한민국 서울시 종로구
        mLocationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        return getCurrentAddress(latitude, longitude);
    }

    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    100);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getActivity(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "잘못된 GPS 좌표";

        }
        if (addresses == null || addresses.size() == 0) {
            //Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
    }

    private void showDialogForLocationServiceSetting() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){ //위치 권한 확인

            //위치 권한 요청
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

}