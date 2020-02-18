package com.example.mytest.adapteritems;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.mytest.layouts.studynow;
import com.google.gson.annotations.SerializedName;

import java.io.File;

public class studynowItem {


    public static final int TITLE_TYPE = 0;
    public static final int TEXT_TYPE = 1;
    public static final int EMAGE_TYPE = 2;

    @SerializedName("type")
    public int mType;
    @SerializedName("title")
    public String mTitle;
    @SerializedName("text")
    public String mText;
    @SerializedName("uri")
    public String mUri;



    //컨스트럭터 만들기 (단축키 alt+insert)
    public studynowItem(int type, String txt, String what) {
        this.mType = type;

        if(what.equals("title")){
            this.mTitle = txt;
        }else if(what.equals("text")){
            this.mText = txt;
        }
    }
    public studynowItem(int type, Uri uri, Context context) {
        this.mType = type;
        this.mUri = uri.toString();
    }

    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTxt(){
        return mText;
    }
    public void setTxt(String text){
        this.mText = text;
    }

    public String getmUri(){return mUri;}

    public Bitmap getImage(Context context){
        Uri ur = Uri.parse(mUri);

        Cursor cursor = null; //DB 에서 가져온 데이터를 row기준으로 다루기 위한 클래스
        File tempFile;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};

            assert ur != null;
            cursor = context.getContentResolver().query(ur, proj, null, null, null);

            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            tempFile = new File(cursor.getString(column_index));


        } finally { //꼭 실행해야 하는 구문!
            if (cursor != null) {
                cursor.close();
            }
        }


        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);





        return originalBm;
    }

}
