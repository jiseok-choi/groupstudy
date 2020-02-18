package com.example.mytest.adapteritems;

import com.google.gson.annotations.SerializedName;

public class itemdata_studygroup_child {

    public static final int List= 1;

    @SerializedName("mail")
    private String mMail;
    @SerializedName("name")
    private String mName;
    @SerializedName("time")
    private String mTime;

    public itemdata_studygroup_child(String mMail, String mName) {
        this.mMail = mMail;
        this.mName = mName;
        this.mTime = "00:00";
    }
    public itemdata_studygroup_child(String mMail, String mName, String mTime) {
        this.mMail = mMail;
        this.mName = mName;
        if(mTime == null){
            this.mTime = "00:00";
        }else{
            this.mTime = mTime;
        }

    }

    public String getmMail() {
        return mMail;
    }

    public String getmTime() {
        return mTime;
    }

    public itemdata_studygroup_child(String text) {
        this.mName = text;
    }
    public String getTime() {
        if(mTime == null){
            return "00:00";
        }
        return mTime;
    }
    public String getmName() {return mName;}

    public void setmMail(String mMail) {
        this.mMail = mMail;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
