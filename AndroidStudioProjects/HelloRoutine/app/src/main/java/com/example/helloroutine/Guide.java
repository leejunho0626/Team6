package com.example.helloroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Guide extends AppCompatActivity {

    Button btnTop,btnBottom,btnAllbody, btnYouTube;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnTop = findViewById(R.id.btnTop);
        btnBottom = findViewById(R.id.btnBottom);
        btnAllbody = findViewById(R.id.btnAllbody);
        btnYouTube = findViewById(R.id.btnYoutube);


        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide.this, TOP.class); //화면 전환
                startActivity(intent);
            }
        });
        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide.this, Bottom.class); //화면 전환
                startActivity(intent);
            }
        });
        btnAllbody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide.this, Allbody.class); //화면 전환
                startActivity(intent);
            }
        });

        btnYouTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=%EB%AC%B8%ED%99%94%EC%B2%B4%EC%9C%A1%EA%B4%80%EA%B4%91%EB%B6%80+%EC%9A%B4%EB%8F%99"));
                startActivity(intent);
            }
        });


    }
}