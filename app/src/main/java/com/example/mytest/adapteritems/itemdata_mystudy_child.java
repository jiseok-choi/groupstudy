package com.example.mytest.adapteritems;

import android.renderscript.ScriptIntrinsicYuvToRGB;

public class itemdata_mystudy_child {

    public static final int LIST = 1;
    public int type;

    public String time;
    public String text;

    public itemdata_mystudy_child(int type, String time, String text) {
        this.type = type;
        this.time = time;
        this.text = text;
    }

    public String getTime(){
        return time;
    }
    public String getText(){
        return text;
    }

}
