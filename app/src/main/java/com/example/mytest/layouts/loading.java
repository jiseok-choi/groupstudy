package com.example.mytest.layouts;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.mytest.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static com.example.mytest.R.drawable.hardstudy;



public class loading extends AppCompatActivity {
    ArrayList<ImageView> booklist;
    int num;
    //firebase



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

//        //firebase 초기화
//        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                .setDeveloperModeEnabled(BuildConfig.DEBUG)
//                .setMinimumFetchIntervalInSeconds(3600)
//                .build();
//        mFirebaseRemoteConfig.setConfigSettings(configSettings);



        //FirebaseAuth.getInstance().signOut();

        ImageView hard = (ImageView) findViewById(R.id.imageView2);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(hard);
        Glide.with(this).load(hardstudy).into(gifImage);

        ImageView book1, book2, book3, book4;
        book1 = findViewById(R.id.book1);
        book2 = findViewById(R.id.book2);
        book3 = findViewById(R.id.book3);
        book4 = findViewById(R.id.book4);

        booklist = new ArrayList<>();

        booklist.add(book1);
        booklist.add(book2);
        booklist.add(book3);
        booklist.add(book4);

        num = 0;

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
        //startLoading();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }



    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 2000);
    }

    //Task
    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        //<doInBackground()의 변수종류, onProgressUpdate()에서 사용할 변수종류, onPostExecute()에서 사용할 변수종류>
        //doIn 변수종류 : 우리가 정의한 AsyncTask를 execute할 때 전해줄 값의 종류
        //onProgress 변수종류 : 진행상황을 업데이트 할때 전달할 값의 종류
        //onPost 변수종류 : AsyncTask 끝난뒤 결과 값의 종류
        @Override
        protected void onPreExecute() { //백그라운드 작업이 시작되기 전 호출되는 메소드(준비작업)
            super.onPreExecute();
            //startLoading();
        }

        @Override
        protected Void doInBackground(Void... Void){
            //pre메소드 이후 실행됨

            for(int i = 0; i < 4; i++){
                publishProgress();
                SystemClock.sleep(500);
                num++;
            }
        return null;
        }


        @Override
        protected void onProgressUpdate(Void... params) {
            //doIn 이 실행되는 도중 publichProgress() 를 호출하면 실행되는 메소드 (진행을 알릴때)
                try{
                    booklist.get(num).setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }
        }

        @Override
        protected void onPostExecute(Void result) { //백그라운드 작업이 완료된후 결과값
            super.onPostExecute(result);
            Intent intent = new Intent(loading.this, login.class);
            startActivity(intent);

            finish();

        }
    }



}
