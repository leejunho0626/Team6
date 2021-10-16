package com.example.helloroutine;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
//글 작성 DB 객체
public class UserWrite {

    public String write;
    public UserWrite(String write) {
        this.write = write;
    }

    public String getWrite() {
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }


}