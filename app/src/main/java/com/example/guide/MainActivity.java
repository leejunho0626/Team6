package com.example.guide;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnTop,btnBottom,btnAllbody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTop = findViewById(R.id.btnTop);
        btnBottom = findViewById(R.id.btnBottom);
        btnAllbody = findViewById(R.id.btnAllbody);


        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TOP.class); //화면 전환
                startActivity(intent);
            }
        });
        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Bottom.class); //화면 전환
                startActivity(intent);
            }
        });
        btnAllbody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Allbody.class); //화면 전환
                startActivity(intent);
            }
        });


    }
}