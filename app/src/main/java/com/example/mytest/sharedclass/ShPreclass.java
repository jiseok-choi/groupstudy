package com.example.mytest.sharedclass;


import android.content.SharedPreferences;

import com.google.gson.Gson;

public class ShPreclass {
    Gson gson;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Shared_class shared_class;

    public ShPreclass(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
        gson = new Gson();
        shared_class = new Shared_class();
    }

    //커밋
    public void commituser(Shared_user shared_user){
        String tojson = shared_class.touser(shared_user);
        editor.putString(shared_user.getmMail(), tojson);
        editor.apply();
    }
    public void commitgroup(Shared_Group shared_group){
        String tojson = shared_class.togroup(shared_group);
        editor.putString(shared_group.getmGroupname(), tojson);
        editor.apply();
    }
//    public void commitstudies(Shared_Studies shared_studies){
//        String tojson = shared_class.tostudies(shared_studies);
//        editor.putString(shared_studies., tojson);
//        editor.apply();
//    }
}
