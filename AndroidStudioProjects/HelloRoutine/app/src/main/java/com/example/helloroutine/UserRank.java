package com.example.helloroutine;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
//점수 DB 객체
public class UserRank {

    public String id;
    public String score;
    public UserRank(String id, String score) {
        this.id = id;
        this.score = score;
    }

}
