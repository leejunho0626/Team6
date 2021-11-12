package com.example.helloroutine;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
//목표설정 DB 객체
public class UserWrite {

    public String write;
    public UserWrite(String write) {
        this.write = write;
    }

}