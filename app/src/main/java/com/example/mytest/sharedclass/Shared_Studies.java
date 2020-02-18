package com.example.mytest.sharedclass;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.mytest.Fragment.Fragment_my;
import com.example.mytest.adapteritems.studynowItem;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

public class Shared_Studies {
    public static String Studies = "Studies";
    //public static int mYear, mMonth, mDay;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SerializedName("title") //공부명
    private String mTitle;
    @SerializedName("makeUsermail") //만든유저
    private String mMakeUsermail;
    @SerializedName("group") //속한 스터디 그룹
    private String mGroup;
    @SerializedName("time") //공부한 시간
    private String mTime;
    @SerializedName("goal") //목표
    private String mGoal;
    @SerializedName("complete")
    private Boolean mComplete;
    @SerializedName("date")
    private String mDate;
    @SerializedName("studynowItem") //공부 내용물
    private ArrayList<studynowItem> mStudynowItem;


    //Constructor
    //null point error 방지
    public Shared_Studies() {
        this.mStudynowItem = new ArrayList<>();
    }
    public Shared_Studies(SharedPreferences sharedPreferences){
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }





    public String getmTitle() {
        return mTitle;
    }

    //getter
    public String getmMakeUsermail() {
        return mMakeUsermail;
    }

    public String getmGroup(){
        return mGroup;
    }

    public String getmTime() {
        if(mTime == null){
            return "first";
        }
        return mTime;
    }

    public String getmGoal() {
        return mGoal;
    }

    public String getmDate() {return mDate;}

    public ArrayList<studynowItem> getmStudynowItem(){ return mStudynowItem; }



    //setter
    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmMakeUsermail(String mMakeUsermail) {
        this.mMakeUsermail = mMakeUsermail;
    }

    public void setmGroup(String mGroup){
        this.mGroup = mGroup;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public void setmGoal(String mGoal) {
        this.mGoal = mGoal;
    }

    public void setmComplete(Boolean mComplete) {this.mComplete = mComplete;}

    public void setmDate(String mDate) {this.mDate = mDate;}

    public void setmStudynowItem(ArrayList<studynowItem> mStudynowItem) {
        this.mStudynowItem = mStudynowItem;
    }

    //method
    //key build
    public String makeKey(){
        String date = Integer.toString(Fragment_my.mYear) + Integer.toString(Fragment_my.mMonth) + Integer.toString(Fragment_my.mDay);
        String studieskey = getmMakeUsermail() + getmTitle() + date;
        return studieskey;
    }
    public String makeKey(int year, int month, int day){
        String date = Integer.toString(year) + Integer.toString(month) + Integer.toString(day);
        String studieskey = getmMakeUsermail() + getmTitle() + date;
        return studieskey;
    }


    //공부목록을 가져오기 위해 모든 공부목록 데이터를 얻는다.
    public ArrayList<Shared_Studies> get공부목록(){
        Gson gson = new Gson();
        ArrayList<Shared_Studies> list = new ArrayList<>();


        Map<String, ?> studies = sharedPreferences.getAll();
        for (Map.Entry<String,?> entry : studies.entrySet()){
            //
            String json = sharedPreferences.getString(entry.getKey(), "");
            Shared_Studies sh2 = gson.fromJson(json, Shared_Studies.class);
            list.add(sh2);
        }
        return list;
    }

    //모든 공부목록 데이터 중에서 유저의 이메일과 속한그룹 에 맞는 목록을 반환한다.
    public ArrayList<Shared_Studies> get그룹의공부목록(String mail, String 그룹명){

        ArrayList<Shared_Studies> list = new ArrayList<>(); //반환할 어레이리스트
        ArrayList<Shared_Studies> allList = get공부목록(); //모든 공부 리스트

        //모든 공부목록 리스트 중에서 mail과 그룹명이 같다면 리스트에 추가할 것
        for(int i = 0; i < allList.size(); i++){
            //Shared_studies 의 변수중 받은 메일과 소속된 그룹명이 같다면
            try{
                Log.i("Shared_Studies", "그룹의 공부목록"+i+"번째");
                Log.i("그룹의", mail+그룹명+allList.get(i).getmDate());
                if (mail.equals(allList.get(i).getmMakeUsermail()) && 그룹명.equals(allList.get(i).getmGroup()) && (Integer.toString(Fragment_my.mYear)+Integer.toString(Fragment_my.mMonth)+Integer.toString(Fragment_my.mDay)).equals(allList.get(i).getmDate())){
                    list.add(allList.get(i));
                    Log.i("Shared_Studies", "그룹의 공부목록 추가함");
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e("Shared_Studies", "그룹의 공부목록 에러");
            }

        }
        //조건에 맞는 <Shared_Studies>만 리스트에 넣고 반환해요
        return list;
    }

    public ArrayList<Shared_Studies> 이전데이터복사 (String mail, String mDate){

        ArrayList<Shared_Studies> list = new ArrayList<>(); //반환할 어레이리스트
        ArrayList<Shared_Studies> allList = get공부목록(); //모든 공부 리스트

        //모든 공부목록 리스트 중에서 mail과 그룹명이 같다면 리스트에 추가할 것
        for(int i = 0; i < allList.size(); i++){
            //Shared_studies 의 변수중 받은 메일과 소속된 그룹명이 같다면
            if (mail.equals(allList.get(i).getmMakeUsermail()) && mDate.equals(allList.get(i).getmDate()) ){

                list.add(allList.get(i));
            }
        }
        //조건에 맞는 <Shared_Studies>만 리스트에 넣고 반환해요
        return list;
    }

    public String 공부시간문자화 (int minutes, int seconds){

        return "";
    }




}
