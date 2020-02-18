package com.example.mytest.firebaseclass;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class firebaseuser {
    private String mName;
    private String mMail;
    private String mPassword;
    private String mUrl;
    private ArrayList<String> mFrands;
    private ArrayList<String> mAreyoufrend;
    private String mLoginDate;

    public firebaseuser() {
    }

    public firebaseuser(String mName, String mMail, String mPassword) {
        this.mName = mName;
        this.mMail = mMail;
        this.mPassword = mPassword;
        this.mLoginDate = "first";
    }



    ////////////////////////////////////////////firebase 메서드
//    @Exclude
//    public Map<String, Object> toMap(){
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("name", mName);
//        result.put("mail", mMail);
//        result.put("password", mPassword);
//        result.put("photo", mUri);
//        result.put("frends", mFrands);
//        result.put("areyoufrend", mAreyoufrend);
//        result.put("logindate", mLoginDate);
//        return result;
//    }


    public String getmMail() {
        return mMail;
    }

    public String getmName() {
        return mName;
    }

    public String getmPassword() {
        return mPassword;
    }

    public String getmUrl() {
        if(mUrl == null){
            return "first";
        }
        return mUrl;
    }

    public ArrayList<String> getmFrands() {
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

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setmFrands(ArrayList<String> mFrands) {
        this.mFrands = mFrands;
    }

    public void setmAreyoufrend(ArrayList<String> mAreyoufrend) {
        this.mAreyoufrend = mAreyoufrend;
    }
}
