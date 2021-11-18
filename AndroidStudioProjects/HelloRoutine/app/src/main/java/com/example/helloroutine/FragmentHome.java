package com.example.helloroutine;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import static java.lang.Thread.sleep;

public class FragmentHome extends Fragment {

    static TextView tmp, pty, pcp, sno, sky, pop, time3;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static LocationSource mLocationSource;
    static GridView grid;
    static GridAdapter adt;
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    static ProgressDialog customProgressDialog;
    private static Animation fab_open, fab_close;
    private static Boolean isFabOpen = false;
    private static FloatingActionButton fab, fab1, fab2;
    static  LinearLayout linearLayout;
    static String x;
    static SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd");
    static String format_1 = format.format(System.currentTimeMillis());
    static String format_2 = null;
    static String cnt;
    static boolean check = true;
    static RecyclerView recyclerView,recyclerView2;
    static RecyclerAdapter recyclerAdapter;
    static ChallengeAdapter challengeAdapter;

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
        challengeAdapter = new ChallengeAdapter();

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
            }
        });

        //운동 가이드 클릭
        linearLayout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=%ED%94%BC%EC%A7%80%EC%BB%AC%EA%B0%A4%EB%9F%AC%EB%A6%AC"));
                startActivity(intent);
            }
        });

        //현재 날짜
        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat ( "yyyy.MM.dd일");
        String format_time1 = format1.format (System.currentTimeMillis());
        String format_time2 = format2.format (System.currentTimeMillis());

        //목표 표시
        showPlanList(format_time2);

        //도전과제 표시
        totalDistance(); //거리 DB 불러오기 - 진행도 표시
        totalPlan();

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
        String nx=null;
        String ny=null;
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

            if(we.equals(add_[i-2])) break;
        }
        String service_key = "Qq0ncOZYtzwIBrVT5cMMrn%2BKP7nlXYXT52ZR%2FpoM6l%2B5W6ch9YeTupmGsrR6bfVDSvXVdI7Gl6sRzKhrkgQbHw%3D%3D";
        String num_of_rows = "12";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = format_time1; // date
        String base_time = time();   // time
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
    /*
    //출석 점수 가져오기
    public void totalAttendance(String today){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("TotalAttendance")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //DB 필드명 표시 지워서 데이터 값만 표시
                        String str1 = document.getData().toString();
                        str1 = str1.substring(str1.indexOf("=")+1);
                        format_2 = str1.substring(0, str1.indexOf("}"));

                        saveDate(today);

                        db.collection("DB").document("User").collection(user.getUid()).document("TotalAttendanceCnt")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        //DB 필드명 표시 지워서 데이터 값만 표시
                                        String str1 = document.getData().toString();
                                        str1 = str1.substring(str1.indexOf("=")+1);
                                        cnt = str1.substring(0, str1.indexOf("}"));
                                        cnt = caldate(format_1, format_2, cnt);

                                        saveCnt(cnt);

                                    } else {

                                    }
                                }
                                else {
                                }
                            }
                        });

                    } else {

                    }
                }
                else {
                    cnt ="1";
                    check = false;

                }
            }
        });

    }

    //출석 날짜
    public static String caldate(String nowdate, String secdate, String cnt) { // nowdate = 오늘 날짜, secdate 전에 로그에 찍혀있는 날, cnt 몇일 연속 접속이였는지
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
                int i = Integer.parseInt(cnt);
                i++;
                check = false;
                cnt=Integer.toString(i);

            }
            cal2.add(Calendar.DATE, -1);
            if(format.format(cal1.getTime()).equals(format.format(cal2.getTime())));
            else if(check) cnt = "1";
        }



        return cnt;

    }

    //출석 일수 저장하기
    public void saveCnt(String total){
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(total);
        db.collection("DB").document("User").collection(user1.getUid()).document("TotalAttendanceCnt").set(userWrite)
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
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserWrite userWrite = new UserWrite(total);
        db.collection("DB").document("User").collection(user1.getUid()).document("TotalAttendance").set(userWrite)
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

    }*/

    //플로팅메뉴 애니메이션
    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
    //도전과제 거리 점수 불러오기
    public void totalDistance(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("TotalDistance")
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

                        int value = (int) Math.round(Double.parseDouble(x)/3*100);
                        int value2 = (int) Math.round(Double.parseDouble(x)/5*100);

                        showBtnFav("0", value, value2);
                        showBtnFav("1", value, value2);


                    } else {
                        challengeAdapter.setArrayData("걷거나 뛴 거리 3km", 0, 0);
                        challengeAdapter.setArrayData("걷거나 뛴 거리 5km", 0, 0);
                        recyclerView2.setAdapter(challengeAdapter);
                    }
                }
                else {
                }
            }
        });

    }

    //일정추가 횟수 DB 불러오기
    public void totalPlan(){
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

                        int value = Integer.parseInt(x)*10;
                        int value2 = (int) Math.round(Double.parseDouble(x)/30*100);
                        showBtnFav("2", value, value2);
                        showBtnFav("3", value, value2);


                    } else {
                        challengeAdapter.setArrayData("운동 일정 10개 추가", 0, 0);
                        challengeAdapter.setArrayData("운동 일정 10개 추가", 0, 0);
                        recyclerView2.setAdapter(challengeAdapter);
                    }
                }
                else {


                }
            }
        });
    }

    public void showBtnFav(String position, int value , int value2){
        try {
            sleep(0);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("DB").document("User").collection(user.getUid()).document("Challenge").collection("Favorite").document(position)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            if(document.getId().equals("0")){
                                //adapter2.addItem("걷거나 뛴 거리 3km", Integer.toString(value)+"%", value);
                                challengeAdapter.setArrayData("걷거나 뛴 거리 3km", value, value);
                            }
                            else if(document.getId().equals("1")){
                                //adapter2.addItem("걷거나 뛴 거리 5km", Integer.toString(value2)+"%", value2);
                                challengeAdapter.setArrayData("걷거나 뛴 거리 5km", value2, value2);
                            }
                            else if(document.getId().equals("2")){
                                //adapter2.addItem("운동 일정 10개 추가", Integer.toString(value)+"%", value);
                                challengeAdapter.setArrayData("운동 일정 10개 추가", value, value);
                            }

                            else {
                                //adapter2.addItem("운동 일정 30개 추가", Integer.toString(value2)+"%", value2);
                                challengeAdapter.setArrayData("운동 일정 30개 추가", value2, value2);
                            }

                        }
                        else {

                            
                        }

                        recyclerView2.setAdapter(challengeAdapter);
                        //adapter2.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "즐겨찾기 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                }

            });
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //오늘의 일정 표시
    public void showPlanList(String today){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("Plan").collection(today)
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

                        } else {

                        }

                    }

                } else {
                    Toast.makeText(getContext().getApplicationContext(), "일정 불러오기를 실패했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String TMP = null;   // 1시간 기온
            String PTY = null;   // 강수형태 없음 0 비 1 눈비 2 눈 3 소나기 4
            String PCP = null;   // 1시간 강수량
            String SNO = null;   // 1시간 적설량
            String SKY = null;   // 하늘 상태 // 맑음 1 구름많음 3 흐림 4
            String POP = null;   // 강수확률
            int cnt = 1;


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

            if(time().equals("-1")){
                time3.setText("기준 : 2300시");
            }
            else {
                time3.setText("기준 : " + time() + "시");
            }
            Log.d("onpostEx", "출력 값 : "+s);
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
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요 ? ");
        builder.setCancelable(true);

        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
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

}