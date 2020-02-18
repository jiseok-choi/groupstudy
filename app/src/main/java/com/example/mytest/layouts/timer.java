package com.example.mytest.layouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.Fragment.Fragment_my;
import com.example.mytest.R;
import com.example.mytest.adapteritems.itemdata_studygroup_child;
import com.example.mytest.sharedclass.Shared_Group;
import com.example.mytest.sharedclass.Shared_Studies;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class timer extends AppCompatActivity implements SensorEventListener {

    TextView myOutput;
    String easy_outTime;
    Button btn타이머시작, btn공부완료;
    //현재의 상태를 저장할 변수를 초기화 함
    final static int Init =0;
    final static int Run =1;
    final static int Pause =2;
    static int lastnum = 0;
    static int getLastnum;

    static int seconds;
    static int hour;


    int cur_Status = Init; //현재의 상태를 저장할변수를 초기화함.
    int myCount=1;
    long myBaseTime;
    long myPauseTime;
    long now;
    long outTime;

    //firebase DB생성
    private FirebaseFirestore dbgroup = FirebaseFirestore.getInstance();
    private FirebaseFirestore dbstudies = FirebaseFirestore.getInstance();
    DocumentReference docRefgroup, docRefstudies;

    //모션동작 변수
    SensorManager sm;
    SensorEventListener mGyroLis;
    Sensor mGgyroSensor = null;
    Boolean 뒤집음;
    double X = 0;
    double Y = 0;
    double Z = -9.8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);


        //모션 동작 메소드
        sm = (SensorManager) getSystemService(SENSOR_SERVICE); //센서매니저 생성
        mGgyroSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //마그네틱 필드 센서 생성
        sm.registerListener(this, mGgyroSensor, SensorManager.SENSOR_DELAY_GAME);





        //Thread로 텍스트값 변경하기
        final TextView txt_명언 = findViewById(R.id.txt_명언);
        final ArrayList<String> 명언 = new ArrayList<>();
        명언.add("일찍 일어나는 새가 벌레한마리 더 먹는다");
        명언.add("즐기는 사람을 이길 수 없다");
        명언.add("오늘을 놓치면 내일은 없다");
        final int[] num = {0};

        final Handler txthandler;
        Runnable txtTimertask;

        txthandler = new Handler();
        txtTimertask = new Runnable() {
            @Override
            public void run() {
                //동작 내용
                txt_명언.setText(명언.get(num[0]));
                if(num[0] == 2){
                    num[0] = 0;
                }else{
                    num[0]++;
                }

                txthandler.postDelayed(this,2000);
            }
        };
        txthandler.removeCallbacks(txtTimertask);
        txthandler.postDelayed(txtTimertask, 2000);



        myOutput = (TextView)findViewById(R.id.txt_time);
        getPreferences();

//        btn타이머시작 = (Button) findViewById(R.id.btn_타이머시작);
//        btn타이머시작.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                myOnClick(v);
//            }
//        });

        final SharedPreferences sh_studies = getSharedPreferences(Shared_Studies.Studies, MODE_PRIVATE);
        final SharedPreferences.Editor sh_studiesEditor = sh_studies.edit();
        SharedPreferences shared_whoami = getSharedPreferences("shared_whoami", MODE_PRIVATE);
        SharedPreferences.Editor whoamiEditor = shared_whoami.edit();
        final String 사용자 = shared_whoami.getString("whoami", "");
        final Gson gson = new Gson();

        //studies 키 만들기
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras(); //액스트라에서 받아주는 것
        final String 공부명 = bundle.getString("공부명");
        String date = Integer.toString(Fragment_my.mYear) + Integer.toString(Fragment_my.mMonth) + Integer.toString(Fragment_my.mDay);
        final String studieskey = 사용자 + 공부명 + date;

                btn공부완료 = (Button) findViewById(R.id.btn_타이머완료);
        btn공부완료.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myOnClick(v);
                myOutput.setText("00:00");
                myTimer.removeMessages(0);
                removeAllPreferences();

                //쉐어드에 저장
                String fromjson = sh_studies.getString(studieskey, "");
                final Shared_Studies shared_studies = gson.fromJson(fromjson, Shared_Studies.class);
                //이전 시간을 가져와서 숫자로 변환 시켜주어야 함
                String tiem =  timeplus(shared_studies.getmTime());
                //변환시켜준 시간을 다시 넣어야함
                shared_studies.setmTime(tiem);
                final String tojson = gson.toJson(shared_studies);
                //파이어베이스 우선저장
                docRefstudies = dbstudies.collection("Lets_study").document("Studies");
                String newkey = studieskey.replace(".","_");
                final Map<String, Object> changeStudy = new HashMap<>();
                changeStudy.put(newkey, tojson);
                docRefstudies.set(changeStudy, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //저장완료되면 쉐어드에도 저장
                        sh_studiesEditor.putString(studieskey, tojson);
                        sh_studiesEditor.apply();



                        //그룹 공부시간 변경
                        final SharedPreferences sh_group = getSharedPreferences(Shared_Group.Group, MODE_PRIVATE);
                        final SharedPreferences.Editor sh_groupeditor = sh_group.edit();
                        String fromjson2 = sh_group.getString(shared_studies.getmGroup(),"");
                        final Shared_Group shared_group = gson.fromJson(fromjson2, Shared_Group.class);

                        int min = (int)(outTime / 1000 / 60);
                        int sec = (int)((outTime/1000)%60);
                        Shared_Group sh_그룹 = new Shared_Group(sh_group);
                        ArrayList<itemdata_studygroup_child> newmember = sh_그룹.유저공부시간더하기(shared_group.getmMember(), 사용자, min, sec);

                        //수정한 시간정보가 담겨있는 멤버클래스
                        shared_group.setmMember(newmember);

                        //그룹정보 다시 저장
                        final String tojsongroup = gson.toJson(shared_group);

                        //파이어베이스 우선저장
                        docRefgroup = dbgroup.collection("Lets_study").document("Group");
                        String newkey2 = studieskey.replace(".","_");
                        Map<String, Object> changeGroup = new HashMap<>();
                        changeGroup.put(shared_studies.getmGroup(), tojson);
                        docRefstudies.set(changeGroup, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //저장완료되면 쉐어드에도 저장
                                String newman = shared_studies.getmMakeUsermail().replace(".","_");
                                sh_groupeditor.putString(shared_studies.getmGroup(), tojsongroup);
                                sh_groupeditor.apply();
                                finish();
                            }
                        });

                    }
                });




            }
                });






    }

    //시간변환후 더하는 메소드
    public String timeplus(String time){
        String returntime;

        if(time.equals("first")){
            return "00:00";
        }
        String[] array = time.split(":");
        int min = Integer.parseInt(array[0]);
        int sec = Integer.parseInt(array[1]);
        min = min + (int)(outTime / 1000 / 60);
        sec = sec + (int)((outTime/1000)%60);
        returntime = String.format("%02d:%02d", min, sec);
        return  returntime;
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
        editor.commit();
    }

    // 값(Key Data) 삭제하기
    private void removePreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("hi");
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }





    ////////////////////////////////////////////////////////////////

    public void myOnClick(View v){
        switch(v.getId()){
//            case R.id.btn_타이머시작: //시작버튼을 클릭했을때 현재 상태값에 따라 다른 동작을 할수있게끔 구현.
//                switch(cur_Status){
//                    case Init:
//
//
//
//                        myBaseTime = SystemClock.elapsedRealtime();
//                        //System.out.println(myBaseTime);
//                        //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
//                        myTimer.sendEmptyMessage(0);
//                        btn타이머시작.setText("멈춤"); //버튼의 문자"시작"을 "멈춤"으로 변경
//                        cur_Status = Run; //현재상태를 런상태로 변경
//
//                        //System.out.println("LOG_시작전_초기상태값"+myBaseTime);
//                        //System.out.println("LOG_시작전_현재값"+now);
//                        //System.out.println("LOG_시작전_퍼즈값"+myPauseTime);
//                        break;
//                    case Run:
//                        myTimer.removeMessages(0); //핸들러 메세지 제거
//                        myPauseTime = SystemClock.elapsedRealtime();
//                        btn타이머시작.setText("다시시작");
//                        cur_Status = Pause;
//                        btn공부완료.setVisibility(View.VISIBLE);
//                        //System.out.println("LOG_멈춤_초기상태값"+myBaseTime);
//                        //System.out.println("LOG_멈춤_현재값"+now);
//                        //System.out.println("LOG_멈춤_퍼즈값"+myPauseTime);
//                        break;
//                    case Pause:
//                        now = SystemClock.elapsedRealtime();
//                        myTimer.sendEmptyMessage(0);
//                        myBaseTime += (now- myPauseTime);
//                        btn타이머시작.setText("멈춤");
//                        btn공부완료.setVisibility(View.GONE);
//                        cur_Status = Run;
//                        //System.out.println("LOG_시작후_초기상태값"+myBaseTime);
//                        //System.out.println("LOG_시작후_현재값"+now);
//                        //System.out.println("LOG_시작후_퍼즈값"+myPauseTime);
//
//                        break;
//
//
//                }
//                break;
            case R.id.btn_타이머완료:
                Toast.makeText(timer.this, myOutput.getText(), Toast.LENGTH_SHORT).show();
                break;

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
        outTime = now - myBaseTime;
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
        savePreferences();
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


    @Override
    public void onSensorChanged(SensorEvent event) {

        double gyroX = event.values[0];
        double gyroY = event.values[1];
        double gyroZ = event.values[2];

        Log.i("senser!",gyroX+"   "+gyroY+"   "+gyroZ+"   "+SystemClock.elapsedRealtime()+"   "+now+"   "+myBaseTime+"   "+myPauseTime);

        if(5>(X-gyroX) && 5>(Y-gyroY) && 5>(Z-gyroZ)){
            뒤집음 = true;
        }else{
            뒤집음 = false;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy(){

        super.onDestroy();

        sm.unregisterListener(this);

    }

    public void 타이머동작(){

    }
    public void 타이머중지(){

    }

}
