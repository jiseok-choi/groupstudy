package com.example.mytest.adapteritems;

import android.graphics.Bitmap;

public class itemdata_talking {

    public static final int TALKING_ME = 0;
    public static final int TALKING_YOU = 1;

    public int Type;
    public String text;
    public Bitmap Bm;

    public itemdata_talking(int type, String txt, Bitmap Bm) {
        this.Type = type;
        this.text = txt;
        this.Bm = Bm;
    }

    public String getText(){return text;}
    public Bitmap getBm(){return Bm;}
}
