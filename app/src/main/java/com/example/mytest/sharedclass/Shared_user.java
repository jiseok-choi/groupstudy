package com.example.mytest.sharedclass;

import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ArrayAdapter;

import com.google.firebase.database.Exclude;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Shared_user {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static String user = "user";

    @SerializedName("name")
    private String mName;
    @SerializedName("mail")
    private String mMail;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("photo")
    private String mUri;
    @SerializedName("frends")
    private ArrayList<String> mFrands;
    @SerializedName("areyoufrend")
    private ArrayList<String> mAreyoufrend;
    @SerializedName("logindate")
    private String mLoginDate;
//    @SerializedName("grouplist")
//    private ArrayList<String> mGrouplist;

    ////////////////////////////////////////////constructor


    public Shared_user() {

    }

    public Shared_user(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }
    public Shared_user(String name, String mail, String password) {
        this.mName = name;
        this.mMail = mail;
        this.mPassword = password;
        this.mUri = "";
        //this.mGrouplist = new ArrayList<>();
    }
    public Shared_user(Uri uri){

        this.mUri = uri.toString();
    }



    ////////////////////////////////////////////getter

    public static String getUser() {
        return user;
    }

    public String getmName() {
        return mName;
    }

    public String getmMail() {
        return mMail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public Uri getmUri() {
        if(mUri.equals("")){
            return null;
        }
        return Uri.parse(mUri);
    }
    public String getmmUri(){
        if(mUri.equals("")){
            return "nodata";
        }
        return this.mUri;
    };

    public ArrayList<String> getmFrands() {
        if(mFrands == null){
            return mFrands = new ArrayList<>();
        }
        return mFrands;
    }
    public ArrayList<String> getmAreyoufrend() {
        if(mAreyoufrend == null){
            return mAreyoufrend = new ArrayList<>();
        }
        return mAreyoufrend;
    }

    public String getmLoginDate() {
        if(mLoginDate == null){
            return "first";
        }
        return mLoginDate;
    }
    ////////////////////////////////////////////setter


    public void setmUri(Uri mUri) { this.mUri = mUri.toString(); }

    public void setmFrands(ArrayList<String> mFrands) {
        this.mFrands = mFrands;
    }
    public void setmAreyoufrend(ArrayList<String> mAreyoufrend) {
        this.mAreyoufrend = mAreyoufrend;
    }

    public void setmLoginDate(String mLoginDate) {
        this.mLoginDate = mLoginDate;
    }





    ////////////////////////////////////////////전체 유저 목록 가져오기
    public ArrayList<Shared_user> get유저목록(){
        Gson gson = new Gson();
        ArrayList<Shared_user> list = new ArrayList<>();


        Map<String, ?> users = sharedPreferences.getAll();
        for(Map.Entry<String,?> entry : users.entrySet()){
            String json = sharedPreferences.getString(entry.getKey(), "" );
            Shared_user sh = gson.fromJson(json, Shared_user.class);
            list.add(sh);
        }
        return list;
    }

    //받은 메일에 해당하는 Shared_user 반환하기
    public Shared_user get유저정보(String mail){
        Shared_user 유저정보 = new Shared_user();
        ArrayList<Shared_user> allList = get유저목록();

        for(int i = 0; i < allList.size(); i++){
            if(mail.equals(allList.get(i).getmMail())){
                유저정보 = allList.get(i);
            }
        }
        return 유저정보;
    }




}