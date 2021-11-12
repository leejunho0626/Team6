package com.example.helloroutine;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
//친구 DB 객체
public class UserFriend {

    public String id;
    public UserFriend(String id){
        this.id = id;
    }
}
