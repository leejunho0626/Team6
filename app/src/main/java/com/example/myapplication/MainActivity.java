package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_outPut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_outPut = (TextView) findViewById(R.id.tv_outPut);

        String service_key = "Qq0ncOZYtzwIBrVT5cMMrn%2BKP7nlXYXT52ZR%2FpoM6l%2B5W6ch9YeTupmGsrR6bfVDSvXVdI7Gl6sRzKhrkgQbHw%3D%3D";
        String num_of_rows = "10";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = "20210927"; // date
        String base_time = "0500";   // time
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

            tv_outPut.setText(s);
            Log.d("onpostEx", "출력 값 : "+s);
        }
    }

}