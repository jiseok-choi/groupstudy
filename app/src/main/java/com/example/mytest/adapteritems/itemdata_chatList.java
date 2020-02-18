package com.example.mytest.adapteritems;

import android.graphics.Bitmap;

public class itemdata_chatList {

    public Bitmap bitmap;
    public String Name;
    public String Text;
    public String Day;

    public itemdata_chatList(Bitmap bitmap, String Name, String Text, String Day) {
        this.bitmap = bitmap;
        this.Name = Name;
        this.Text = Text;
        this.Day = Day;
    }

    public Bitmap getBitmap(){return bitmap;}
    public String getName(){return Name;}
    public String getTxt(){return Text;}
    public String getDay(){return Day;}
}
