package com.example.helloroutine;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

public class RequestHttpConnection {

    public String request(String _url, ContentValues _params) {
        HttpURLConnection urlConn = null;
        StringBuffer sbParms = new StringBuffer();
        if (_params == null)
            sbParms.append("");
        else {
            boolean isAnd = false;
            String key;
            String value;

            for (Map.Entry<String, Object> parameter : _params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if (isAnd)
                    sbParms.append("&");

                sbParms.append(key).append("=").append(value);

                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }
        try {
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setDoOutput(false);

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("HTTP_OK", "연결 요청 실패");
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String line;
            String page = "";

            while ((line = reader.readLine()) != null) {
                page += line;
            }
            return page;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return null;

    }


}