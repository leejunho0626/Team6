package com.example.helloroutine;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
//목표설정 DB 객체
public class UserRank {

    public String score;
    public UserRank(String score) {
        //
        this.score = score;
    }

}
