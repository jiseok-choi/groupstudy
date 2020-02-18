package com.example.mytest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class timer2 extends AppCompatActivity {
    //따라하기
    private static final long START_TIME_IN_MILLIS = 6000000;

        TextView myOutput;
        String easy_outTime;
        Button btn타이머시작, btn공부완료;
        //현재의 상태를 저장할 변수를 초기화 함
        final static int Init =0;
        final static int Run =1;
        final static int Pause =2;
        final static int Pause2 = 3;
        static int lastnum = 0;
        static int getLastnum;

        static int seconds;
        static int hour;


        int cur_Status = Init; //현재의 상태를 저장할변수를 초기화함.
        int myCount=1;
        long myBaseTime;
        long myPauseTime;
        long now;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        myOutput = (TextView)findViewById(R.id.txt_time);


//        btn타이머시작 = (Button) findViewById(R.id.btn_타이머시작);
//        btn타이머시작.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                myOnClick(v);
//            }
//        });

        btn공부완료 = findViewById(R.id.btn_타이머완료);
        btn공부완료.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

        ///////////////////////////////////////////////////////////////

        // 값 불러오기
        private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        now = pref.getLong("now", 0);
        myBaseTime = pref.getLong("myBaseTime", 0);
        myPauseTime = pref.getLong("myPauseTime", 0);
    }

        // 값 저장하기
        private void savePreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("now", now);
        editor.putLong("myBaseTime", myBaseTime);
        editor.putLong("myPauseTime", myPauseTime);
        editor.apply();
    }

        // 값(Key Data) 삭제하기
        private void removePreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("hi");
        editor.apply();
    }

        // 값(ALL Data) 삭제하기
        private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }





        ////////////////////////////////////////////////////////////////

        public void myOnClick(View v){
        switch(v.getId()){
//            case R.id.btn_타이머시작: //시작버튼을 클릭했을때 현재 상태값에 따라 다른 동작을 할수있게끔 구현.
//                switch(cur_Status){
//                    case Init:
//                        myBaseTime = SystemClock.elapsedRealtime();
//                        //System.out.println(myBaseTime);
//                        //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
//                        myTimer.sendEmptyMessage(0);
//                        btn타이머시작.setText("멈춤"); //버튼의 문자"시작"을 "멈춤"으로 변경
//                        cur_Status = Run; //현재상태를 런상태로 변경
//                        //System.out.println("LOG_시작전_초기상태값"+myBaseTime);
//                        //System.out.println("LOG_시작전_현재값"+now);
//                        //System.out.println("LOG_시작전_퍼즈값"+myPauseTime);
//                        break;
//                    case Run:
//                        myTimer.removeMessages(0); //핸들러 메세지 제거
//                        myPauseTime = SystemClock.elapsedRealtime();
//                        btn타이머시작.setText("시작");
//                        cur_Status = Pause;
//                        //System.out.println("LOG_멈춤_초기상태값"+myBaseTime);
//                        //System.out.println("LOG_멈춤_현재값"+now);
//                        //System.out.println("LOG_멈춤_퍼즈값"+myPauseTime);
//                        break;
//                    case Pause:
//                        now = SystemClock.elapsedRealtime();
//                        myTimer.sendEmptyMessage(0);
//                        myBaseTime += (now- myPauseTime);
//                        btn타이머시작.setText("멈춤");
//                        cur_Status = Run;
//                        //System.out.println("LOG_시작후_초기상태값"+myBaseTime);
//                        //System.out.println("LOG_시작후_현재값"+now);
//                        //System.out.println("LOG_시작후_퍼즈값"+myPauseTime);
//
//                        break;
//                    case Pause2:
//                        now = SystemClock.elapsedRealtime();
//                        myTimer.sendEmptyMessage(0);
//                        myBaseTime += (now-myPauseTime);
//                        btn타이머시작.setText("멈춤");
//                        cur_Status = Run;
//                        break;
//
//                }
//                break;
//

        }
    }

//    Handler handler = new Handler(){
//
//    }



        Handler myTimer = new Handler(){
            public void handleMessage(Message msg){
                myOutput.setText(getTimeOut());

                //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
                myTimer.sendEmptyMessage(0);
            }
        };
        //현재시간을 계속 구해서 출력하는 메소드
        String getTimeOut(){
        now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        long outTime = now - myBaseTime;
        //getLastnum = ((int)(outTime/1000)%60);
        easy_outTime = String.format("%02d:%02d", outTime/1000 / 60, (outTime/1000)%60);
        return easy_outTime;

    }



        //화면 전환시 시간초 멈춤


        @Override
        protected void onStart() {
        super.onStart();
        getPreferences();
    }

        @Override
        protected void onPause() {
        super.onPause();
        btn타이머시작.setText("시작");
        myTimer.removeMessages(0);
        cur_Status = Init;

        savePreferences();
    }

        @Override
        public void onBackPressed() {
        super.onBackPressed();
        btn타이머시작.setText("시작");
        myTimer.removeMessages(0);
        cur_Status = Init;
    }




        //데이터 저장
        @Override
        protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("timer","onSaveInstanceState");
        //outState.putInt("lastnum",getLastnum);

        outState.putString("savetime",easy_outTime);
        outState.putLong("now",now);
        outState.putLong("myBaseTime",myBaseTime);
        outState.putLong("myPauseTime",myPauseTime);
        //outState.putLong("SystemClock.elapsedRealtime()",SystemClock.elapsedRealtime());
        //outState.putLong("getLastnum",getLastnum);
    }

        @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //lastnum = savedInstanceState.getInt("lastnum");
//        Log.i("timer","onRestoreInstanceState");
        easy_outTime = savedInstanceState.getString("savetime");
        now = savedInstanceState.getLong("now");
        myBaseTime = savedInstanceState.getLong("myBaseTime");
        myPauseTime = savedInstanceState.getLong("myPauseTime");
        //SystemClock.elapsedRealtime() =  //savedInstanceState.getLong("SystemClock.elapsedRealtime()");
        //getLastnum = savedInstanceState.getInt("getLastnum");
    }



}
