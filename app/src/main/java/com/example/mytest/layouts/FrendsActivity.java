package com.example.mytest.layouts;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import com.bumptech.glide.Glide;
import com.example.mytest.R;
import com.example.mytest.adapter.Adapter_mystudy;
import com.example.mytest.adapteritems.itemdata_mystudy_child;
import com.example.mytest.adapteritems.itemdata_mystudy_parent;
import com.example.mytest.sharedclass.Shared_Group;
import com.example.mytest.sharedclass.Shared_Studies;
import com.example.mytest.sharedclass.Shared_class;
import com.example.mytest.sharedclass.Shared_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class FrendsActivity extends AppCompatActivity {

    /****************************
     *Firebase Store 관련 변수 선언
     *****************************/
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefUsers, docRefStudies;
    Shared_user 메인클래스;


    /****************************
    *그룹원이 소속한 그룹과 공부내용을 볼 수 있는 익스펜더블 리스트뷰 변수 선언
    *****************************/

    /////////익스펜더블 리스트뷰 선언하기
    ExpandableListView listView;
    Adapter_mystudy adapter_mystudy;
    ArrayList<itemdata_mystudy_parent> GroupList = new ArrayList<>();
    ArrayList<ArrayList<itemdata_mystudy_child>> ChildList = new ArrayList<>();
    ArrayList<ArrayList<itemdata_mystudy_child>> group_management = new ArrayList<>();

    public static int mYear, mMonth, mDay;

    /****************************
     *사진 관련 변수 선언
     *****************************/
    private ImageView iv_UserPhoto;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frends);

        //주키 가져오기
        SharedPreferences SP_user = getSharedPreferences(Shared_user.user,MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String mainkey = bundle.getString("frendMail", "name");

        //불러온 주키로 유저 클래스 부르기
        String json = SP_user.getString(mainkey, ""); //주키로 유저 스트링 부르기
        Shared_class shared_class = new Shared_class();
        메인클래스 = shared_class.fromuser(json);

        TextView name;
        name = findViewById(R.id.textView2);
        name.setText(메인클래스.getmName());

        //익스펜더블 리스트뷰 만들기
        listView = (ExpandableListView) findViewById(R.id.Expanded_Frendstudy);

        //어뎁터 적용하기
        adapter_mystudy = new Adapter_mystudy(this, mainkey);
        adapter_mystudy.parentItems = GroupList;
        adapter_mystudy.childItems = ChildList;
        listView.setAdapter(adapter_mystudy);
        listView.setGroupIndicator(null); //listview 기본 아이콘 표시여부


        //날짜 기능 만들기
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        final TextView 오늘날짜 = (TextView) findViewById(R.id.txt_Today);
        오늘날짜.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
        final DatePickerDialog mDateSetlistener = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mYear = year; mMonth = month; mDay = dayOfMonth;
                오늘날짜.setText(String.format("%d-%d-%d", mYear, mMonth+1, mDay));
                Log.i("txt_Today", Integer.toString(mYear)+Integer.toString(mMonth)+Integer.toString(mDay)+"");
                setListItems();
            }
        },mYear,mMonth,mDay);
        calendar.set(mYear,mMonth,mDay);
        mDateSetlistener.getDatePicker().setMaxDate(calendar.getTimeInMillis());



        오늘날짜.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateSetlistener.show();

            }
        });








    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
        final SharedPreferences.Editor edituser = sh_user.edit();

        docRefUsers = db.collection("Lets_study").document("Users");
        docRefUsers.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null){
                    Map<String, Object> allUsers = task.getResult().getData();
                    for(String key : allUsers.keySet()){
                        String newkey = key.replace("_",".");
                        edituser.putString(newkey, allUsers.get(key).toString());
                    }
                    edituser.apply();
                    //친구 사진 보여주기
                    iv_UserPhoto = (ImageView) findViewById(R.id.img_Myface2);
                    if(!메인클래스.getmmUri().equals("nodata")){
                        URL newurl = null;
                        try {
                            newurl = new URL(메인클래스.getmmUri());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        Glide.with(FrendsActivity.this).load(newurl).into(iv_UserPhoto);
                    }
                    setListItems();
            }
            }
        });
    }

    ////////어뎁터 데이터 추가(임시)
    public void setListItems(){
        group_management.clear();
        GroupList.clear();
        ChildList.clear();

        //사용자의 스터디 그룹 목록인 parents 아이템 가져오기(fragment_group과 동일)
        SharedPreferences sh = getSharedPreferences(Shared_Group.Group, MODE_PRIVATE);
        final SharedPreferences.Editor shedit = sh.edit();
        final Shared_Group groupList = new Shared_Group(sh);

        final SharedPreferences sh_studies = getSharedPreferences(Shared_Studies.Studies, MODE_PRIVATE);
        final SharedPreferences.Editor shstudy = sh.edit();

        //firebase 검색
        docRefStudies = db.collection("Lets_study").document("Studies");
        docRefStudies.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null){
                    Map<String, Object> allStudies = task.getResult().getData();
                    for(String key : allStudies.keySet()){
                        String newkey = key.replace("_",".");
                        shstudy.putString(newkey, allStudies.get(key).toString());
                    }
                    shstudy.apply();



                    //유저가 속한 데이터 그룹을 반환한다.
                    ArrayList<Shared_Group> 유저의소속그룹 = groupList.get유저가속한그룹(메인클래스.getmMail());

                    for (int i = 0; i<유저의소속그룹.size(); i++){
                        //페런트 공간 확보하는 부분
                        group_management.add(new ArrayList<itemdata_mystudy_child>());
                    }
                    ChildList.addAll(group_management);


                    for (int i = 0; i < 유저의소속그룹.size(); i++){
                        //parent의 데이터 바인딩
                        GroupList.add(new itemdata_mystudy_parent(유저의소속그룹.get(i).getmGroupname()));
                        //parent에 바인딩되어있는 텍스트를 기준으로 해당 공부목록 파일을 조회위한 변수
                        String 소속그룹명 = 유저의소속그룹.get(i).getmGroupname();
                        //저장한 공부록록이 있는지 확인해보기
                        SharedPreferences Sh_Studies = getSharedPreferences(Shared_Studies.Studies, MODE_PRIVATE);
                        Shared_Studies shared_studies = new Shared_Studies(sh_studies);
                        //유저의 메일과 그룹명을 가지고 List<Shared_Studies>를 반환한다.
                        ArrayList<Shared_Studies> 유저의공부목록 = shared_studies.get그룹의공부목록(메인클래스.getmMail(), 소속그룹명);

                        //차일드의 데이터 바인딩
                        for(int j = 0; j < 유저의공부목록.size(); j++){
                            itemdata_mystudy_child item = new itemdata_mystudy_child(1, 유저의공부목록.get(j).getmTime(), 유저의공부목록.get(j).getmTitle());

                            group_management.get(i).add(item);
                        }
                    }
                    adapter_mystudy.notifyDataSetChanged();
                }
            }
        });

    }





}
