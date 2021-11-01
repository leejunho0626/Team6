package com.example.helloroutine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Friend extends AppCompatActivity {

    Button btnAddFr;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnAddFr = findViewById(R.id.btnAddFr);
        firebaseAuth = FirebaseAuth.getInstance();


        btnAddFr.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

    }
}