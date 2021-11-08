package com.example.helloroutine;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
//목표설정 DB 객체
public class UserRank {

    public String id, score;
    public UserRank(String id, String score) {
        //
        this.id = id;
        this.score = score;
    }

}
