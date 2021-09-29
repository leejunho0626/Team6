package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private TextView tmp;
    private TextView pty;
    private TextView pcp;
    private TextView sno;
    private TextView sky;
    private TextView pop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tmp = (TextView) findViewById(R.id.tmp);
        pty = (TextView) findViewById(R.id.pty);
        pcp = (TextView) findViewById(R.id.pcp);
        sno = (TextView) findViewById(R.id.sno);
        sky = (TextView) findViewById(R.id.sky);
        pop = (TextView) findViewById(R.id.pop);

        SimpleDateFormat format1 = new SimpleDateFormat ( "yyyyMMdd");
        String format_time1 = format1.format (System.currentTimeMillis());


        String service_key = "Qq0ncOZYtzwIBrVT5cMMrn%2BKP7nlXYXT52ZR%2FpoM6l%2B5W6ch9YeTupmGsrR6bfVDSvXVdI7Gl6sRzKhrkgQbHw%3D%3D";
        String num_of_rows = "12";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = format_time1; // date
        String base_time = time();   // time
        String nx = "60"; // x값
        String ny = "127"; // y값

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
        networkTask.execute();


    }
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
            Log.d("onpostEx", "출력 값 : "+s);
        }
    }

    public static String time()
    {
        String time_;
        SimpleDateFormat format2 = new SimpleDateFormat ( "HHmm");
        String format_time2 = format2.format (System.currentTimeMillis());
        int intTime = Integer.parseInt(format_time2);
        if(intTime<300) time_ = "0200";
        else if(intTime<600) time_ = "0500";
        else if(intTime<900) time_ = "0800";
        else if(intTime<1200) time_ = "1100";
        else if(intTime<1500) time_ = "1400";
        else if(intTime<1800) time_ = "1700";
        else if(intTime<2100) time_ = "2000";
        else time_ = "2300";
        return time_;


    }


}