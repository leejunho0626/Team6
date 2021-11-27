package com.example.helloroutine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Allbody extends AppCompatActivity {

    Button btn13, btn14, btn15,btn16,btn17,btn18,btn19,btn20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allbody);

        btn13=findViewById(R.id.btn13);
        btn14=findViewById(R.id.btn14);
        btn15=findViewById(R.id.btn15);
        btn16=findViewById(R.id.btn16);
        btn17=findViewById(R.id.btn17);
        btn18=findViewById(R.id.btn18);
        btn19=findViewById(R.id.btn19);
        btn20=findViewById(R.id.btn20);

        btn13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Allbody.this, ShowExe.class); //화면 전환
                intent.putExtra("date","13");
                startActivity(intent);


            }
        });
        btn14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Allbody.this, ShowExe.class); //화면 전환
                intent.putExtra("date","14");
                startActivity(intent);
            }
        });
        btn15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Allbody.this, ShowExe.class); //화면 전환
                intent.putExtra("date","15");
                startActivity(intent);
            }
        });

        btn16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Allbody.this, ShowExe.class); //화면 전환
                intent.putExtra("date","16");
                startActivity(intent);
            }
        });

        btn17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Allbody.this, ShowExe.class); //화면 전환
                intent.putExtra("date","17");
                startActivity(intent);
            }
        });

        btn18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Allbody.this, ShowExe.class); //화면 전환
                intent.putExtra("date","18");
                startActivity(intent);
            }
        });
        btn19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Allbody.this, ShowExe.class); //화면 전환
                intent.putExtra("date","19");
                startActivity(intent);
            }
        });
        btn20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Allbody.this, ShowExe.class); //화면 전환
                intent.putExtra("date","20");
                startActivity(intent);
            }
        });
    }
}