package com.example.mytest;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {

    private long backkeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    //생성자에 사용하는 액티비티를 받아 저장하기!!!
    public BackPressCloseHandler(Activity context){
        this.activity = context;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backkeyPressedTime + 2000){
            backkeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if(System.currentTimeMillis() <= backkeyPressedTime + 2000){
            backkeyPressedTime = System.currentTimeMillis();
            activity.finish();
            toast.cancel();
        }
    }

    private void showGuide() {
        toast = Toast.makeText(activity, "종료하려면 두번 뒤로가기 하세요", Toast.LENGTH_SHORT);
        toast.show();
    }
}
