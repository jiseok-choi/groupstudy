package com.example.mytest.layouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mytest.Fragment.Fragment_my;
import com.example.mytest.R;
import com.example.mytest.firebaseclass.firebaseuser;
import com.example.mytest.sharedclass.Shared_Studies;
import com.example.mytest.sharedclass.Shared_class;
import com.example.mytest.sharedclass.Shared_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.mytest.layouts.MainActivity.iv_UserPhoto;

public class idpw extends AppCompatActivity {
    public static String 사용자;
    EditText edit_email, edit_pw;
    Button btn, btn2;
    String input_email = "asdf@asdf";
    String input_pw = "1234";
    SharedPreferences sh_user;
    SharedPreferences.Editor sh_userEditor;
    SharedPreferences shared_whoami;
    SharedPreferences.Editor whoamiEditor;
    SharedPreferences sh_studies;
    SharedPreferences.Editor sh_studiesEditor;
    //String 사용자;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    Shared_Studies shared_studies;
    SharedPreferences.Editor sh_studiesEdit;
    String userid;
    firebaseuser firebaseuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef, docRefStudies;
    Map.Entry<String, Object> entry;
    public static String fireEmail;
    SharedPreferences sh_alluser;
    SharedPreferences.Editor alluser_Editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2idpw);



        Intent intent = getIntent();
        if(intent.getExtras() != null){ //회원가입 안통하고 들어왔을때 오류 방지
            Bundle bundle = intent.getExtras(); //액스트라에서 받아주는 것
            input_email = bundle.getString("email");
            input_pw = bundle.getString("pw");
        }

        edit_email = (EditText) findViewById(R.id.text_이메일주소); //로그인시 입력값
        edit_pw = (EditText) findViewById(R.id.text_비밀번호); //로그인시 입력값



        //firebase 채팅위한 로그인 부분


        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();





        sh_studies = getSharedPreferences(Shared_Studies.Studies, MODE_PRIVATE);
        sh_studiesEdit = sh_studies.edit();
        shared_studies = new Shared_Studies(sh_studies);


        docRefStudies = db.collection("Lets_study").document("Studies");
        docRefStudies.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Map<String, Object> changestu = task.getResult().getData();
                    if(changestu != null){
                        for(String key : changestu.keySet()){
                            String study = key.replace("_", ".");
                            sh_studiesEdit.putString(study, changestu.get(key).toString());
                        }
                        sh_studiesEdit.apply();
                    }
                }
            }
        });

        sh_alluser = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
        alluser_Editor = sh_alluser.edit();

        //store에서 확인
        docRef = db.collection("Lets_study").document("Users");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    Map<String, Object> document = task.getResult().getData();
                    if (document == null) {
                        //Toast.makeText(idpw.this, "회원가입을 해주세요.", Toast.LENGTH_SHORT).show();
                    } else { //기존값이 있을때
                        for (String key : document.keySet()) {
                            String name = key.replace("_", ".");
                            alluser_Editor.putString(name, document.get(key).toString());
                        }
                        alluser_Editor.apply();
                    }
                }
            }
        });


        btn2 = findViewById(R.id.button15);
        btn2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences prefs2 = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
                SharedPreferences.Editor editor2 = prefs2.edit();
                editor2.clear().commit();
                SharedPreferences prefs = getSharedPreferences(Shared_Studies.Studies, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear().commit();
                Log.i("preference","clear!!!");
                return false;
            }
        });

        btn = (Button) findViewById(R.id.btn_로그인확인);
        btn.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                fireEmail = (edit_email.getText().toString()).replace(".","_");
                //로그인하면 모든 유저정보 다 가져오기




                mAuth.signInWithEmailAndPassword(edit_email.getText().toString(), edit_pw.getText().toString()).addOnCompleteListener(idpw.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(idpw.this, "firebase 로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });




                //store에서 확인
                docRef = db.collection("Lets_study").document("Users");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Map<String, Object> document = task.getResult().getData();
                            if(document == null){
                                Toast.makeText(idpw.this, "회원가입을 해주세요.", Toast.LENGTH_SHORT).show();
                            }else{ //기존값이 있을때
                                for(String key : document.keySet()){
                                    String name = key.replace("_",".");
                                    alluser_Editor.putString(name, document.get(key).toString());
                                }
                                alluser_Editor.apply();

                                if(!document.containsKey(fireEmail)){//회원가입 요망
                                    Toast.makeText(idpw.this, "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();
                                }else {
                                    //해당하는 유저인 json을 가져와서 객체화 해준다.
                                    String user = document.get(fireEmail).toString();
                                    Shared_class shared_class = new Shared_class();
                                    Shared_user shared_user = shared_class.fromuser(user);
                                    if(!shared_user.getmPassword().equals(edit_pw.getText().toString())) {
                                        Toast.makeText(idpw.this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                                    }else{ //정상 로그인
                                        Intent intent = new Intent(idpw.this, MainActivity.class);
                                        startActivity(intent);


                                        사용자 = edit_email.getText().toString();
                                        //활동할때 주키로 사용할 쉐어드 이메일 값 저장하기
                                        shared_whoami = getSharedPreferences("shared_whoami", MODE_PRIVATE);
                                        whoamiEditor = shared_whoami.edit();

                                        whoamiEditor.putString("whoami",사용자);
                                        whoamiEditor.apply();


                                        logindate(shared_user);








                                    }

                                }

                            }
                        }
                    }
                });






                //firebase
                String fireEmail2 = edit_email.getText().toString().trim();
                String firePw = edit_pw.getText().toString().trim();
                //파이어베이스를 사용하려면 꼭 생성해야하는 인스턴스
                //mAuth = FirebaseAuth.getInstance();
                //파이어베이스 로그인 절차
//                mAuth.signInWithEmailAndPassword(fireEmail, firePw)
//                .addOnCompleteListener(idpw.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        userid = user.getUid();
//                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
//
////                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users");
//
//                        logindate();
//
//                        if(task.isSuccessful()){
//
//                        }else{
//                            Toast.makeText(idpw.this, "이메일과 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                    }
//                });


                //shared 정보 가져오기
                sh_user = getSharedPreferences(Shared_user.user,MODE_PRIVATE);

                Gson gson = new Gson();

                String login = sh_user.getString(edit_email.getText().toString(), null);
                Shared_user shared_user = gson.fromJson(login, Shared_user.class);

                if(login == null){

                    //Toast.makeText(idpw.this, "존재하지 않는 회원입니다. 회원가입 부터 해주세요", Toast.LENGTH_SHORT).show();
                    //return;
                }else if(login != null && !shared_user.getmPassword().equals(edit_pw.getText().toString())){
                    //Toast.makeText(idpw.this, "비밀번호를 확인 해주세요", Toast.LENGTH_SHORT).show();
                }else{//로그인 통과!


                    //유저가 오늘 처음 접속했는지 판단하기
                }


//                if(input_email.equals(edit_email.getText().toString()) && input_pw.equals(edit_pw.getText().toString())){
//                    Intent intent = new Intent(idpw.this, MainActivity.class);
//                    startActivity(intent);
//                    //연결된 루트 액티비티 모두 종료
//                    finishAffinity(); //해당 앱의 루트 액티비티를 종료시킴
//                        System.runFinalization(); //현재 작업중인 쓰레드가 다 종료되면 종료시키라는 명령어
////                        System.exit(0); //현재 액티비티를 종료시킨다.
//
//                }else{
//                    Toast.makeText(idpw.this, "이메일과 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        btn = (Button) findViewById(R.id.btn_idpw_x);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //접속했는지 판단하기 위한 메소드 생성
    public void logindate(Shared_user shared__user){

        Calendar calendar = Calendar.getInstance();
        Fragment_my.mYear = calendar.get(Calendar.YEAR);
        Fragment_my.mMonth = calendar.get(Calendar.MONTH);
        Fragment_my.mDay = calendar.get(Calendar.DAY_OF_MONTH);
        final String date = Integer.toString(Fragment_my.mYear) + Integer.toString(Fragment_my.mMonth) + Integer.toString(Fragment_my.mDay);

        //우선 처음접속 맞는지 확인하기
        final Shared_user shared_user = shared__user;
        final String recent = shared_user.getmLoginDate();

        Map<String, Object> changeuser;
        Shared_class shared_class = new Shared_class();

        if(shared_user.getmLoginDate().equals("first")){
            Gson gson = new Gson();
            shared_user.setmLoginDate(date);
            Toast.makeText(idpw.this, "가입을 축하합니다 \n그룹을 만들어보세요", Toast.LENGTH_SHORT).show();
            String tojson = gson.toJson(shared_user);
            sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
            sh_userEditor = sh_user.edit();
            sh_userEditor.putString(사용자, tojson);
            sh_userEditor.apply();

            changeuser = new HashMap<>();
            changeuser.put(fireEmail, shared_class.touser(shared_user));
            docRef.set(changeuser, SetOptions.merge());
            //reference.child("mLoginDate").setValue(date);
            //연결된 루트 액티비티 모두 종료
            finishAffinity(); //해당 앱의 루트 액티비티를 종료시킴
            System.runFinalization(); //현재 작업중인 쓰레드가 다 종료되면 종료시키라는 명령어

        }else if(shared_user.getmLoginDate().equals(date)){
            //두번째 접속했다면 그냥 패스

            //연결된 루트 액티비티 모두 종료
            finishAffinity(); //해당 앱의 루트 액티비티를 종료시킴
            System.runFinalization(); //현재 작업중인 쓰레드가 다 종료되면 종료시키라는 명령어

        }else{
            Gson gson = new Gson();
            //if(shared_user.getmLoginDate().length() == )
            int dday;
            try{
                dday = Integer.parseInt(shared_user.getmLoginDate().substring(5,7));
            }catch (Exception e){
                dday = Integer.parseInt(shared_user.getmLoginDate().substring(5,6));
            }

            if (dday > Fragment_my.mDay){
                shared_user.setmLoginDate(date);
                String tojson = gson.toJson(shared_user);
                sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
                sh_userEditor = sh_user.edit();
                sh_userEditor.putString(사용자, tojson);
                sh_userEditor.apply();

                changeuser = new HashMap<>();
                changeuser.put(fireEmail, shared_class.touser(shared_user));
                docRef.set(changeuser, SetOptions.merge());
                //reference.child("mLoginDate").setValue(date);
            }else {
                shared_user.setmLoginDate(date);
                String tojson = gson.toJson(shared_user);
                sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
                sh_userEditor = sh_user.edit();
                sh_userEditor.putString(사용자, tojson);
                sh_userEditor.apply();

                changeuser = new HashMap<>();
                changeuser.put(fireEmail, shared_class.touser(shared_user));
                docRef.set(changeuser, SetOptions.merge());


                //reference.child("mLoginDate").setValue(date);
                //복사해야하는 구간

                docRefStudies = db.collection("Lets_study").document("Studies");
                Map<String, Object> changeStudies = new HashMap<>();


                final ArrayList<Shared_Studies> 복사리스트 = 이전데이터복사(사용자, recent);
                sh_studiesEditor = sh_studies.edit();
                for (int i = 0; i < 복사리스트.size(); i++) {
                    final String key = 복사리스트.get(i).makeKey();
                    복사리스트.get(i).setmDate(date);
                    복사리스트.get(i).setmTime("00:00");
                    final String tojson2 = gson.toJson(복사리스트.get(i));
                    String newkey = key.replace(".","_");
                    Map<String, Object> changesutdies = new HashMap<>();
                    changesutdies.put(newkey, tojson2);
                    final int finalI = i;
                    docRefStudies.set(changesutdies, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sh_studiesEditor.putString(key, tojson2);
                            sh_studiesEditor.apply();
                            if(finalI == 복사리스트.size()-1){
                                //연결된 루트 액티비티 모두 종료
                                finishAffinity(); //해당 앱의 루트 액티비티를 종료시킴
                                System.runFinalization(); //현재 작업중인 쓰레드가 다 종료되면 종료시키라는 명령어
                            }
                        }
                    });
                Log.i("idpw","복사완료");

                }
                Toast.makeText(idpw.this, "오늘도 열공 ^^", Toast.LENGTH_SHORT).show();


            }
        }
    }

    public ArrayList<Shared_Studies> 이전데이터복사 (String mail, String mDate){
        ArrayList<Shared_Studies> list = new ArrayList<>(); //반환할 어레이리스트
        ArrayList<Shared_Studies> allList = shared_studies.get공부목록(); //모든 공부 리스트

        //모든 공부목록 리스트 중에서 mail과 그룹명이 같다면 리스트에 추가할 것
        for(int i = 0; i < allList.size(); i++){
            //Shared_studies 의 변수중 받은 메일과 소속된 그룹명이 같다면
            if (mail.equals(allList.get(i).getmMakeUsermail()) && mDate.equals(allList.get(i).getmDate())){
                list.add(allList.get(i));
            }
        }
        //조건에 맞는 <Shared_Studies>만 리스트에 넣고 반환해요
        return list;
    }
}
