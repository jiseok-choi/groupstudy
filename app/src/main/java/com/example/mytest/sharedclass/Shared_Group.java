package com.example.mytest.sharedclass;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mytest.adapteritems.itemdata_studygroup_child;
import com.example.mytest.adapteritems.itemdata_studygroup_parent;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

public class Shared_Group {
    public static String Group = "Group";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @SerializedName("groupname")
    private String mGroupname;
    @SerializedName("member")
    private ArrayList<itemdata_studygroup_child> mMember;

    public Shared_Group() {
    }
    public Shared_Group(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public String getmGroupname() {
        return mGroupname;
    }

    public ArrayList<itemdata_studygroup_child> getmMember() {
        if(mMember == null){
            return mMember = new ArrayList<>();
        }
        return mMember;
    }


    public void setmGroupname(String mGroupname) {
        this.mGroupname = mGroupname;
    }

    public void setmMember(ArrayList<itemdata_studygroup_child> mMember) {
        this.mMember = mMember;
    }


    //해당하는 팀원이 있는지 판단하여 익스펜더블 리스트뷰에 뿌려주는 메소드를 만들어보자
    // 모든 그룹 데이터를 얻는다.
    public ArrayList<Shared_Group> get그룹목록(){
        Gson gson = new Gson();
        ArrayList<Shared_Group> list = new ArrayList<Shared_Group>();
//        ArrayList<String> grouplist = new ArrayList<>();

        Map<String, ?> group = sharedPreferences.getAll();
        for(Map.Entry<String,?> entry : group.entrySet()){
            // 데이터로 바꿔서 데이터 타입리스트에 넣기
//            grouplist.add(entry.getKey());
            try {
                String json = sharedPreferences.getString(entry.getKey(), ""); // json 밸류
                Shared_Group sh1 = gson.fromJson(json, Shared_Group.class);
                list.add(sh1);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return list;
    }

    // 모든 그룹데이터 중에 유저가 속한 그룹을 반환한다.
    public ArrayList<Shared_Group> get유저가속한그룹(String mail) {

        ArrayList<Shared_Group> list = new ArrayList<Shared_Group>(); // 반환할 list
        ArrayList<Shared_Group> allList = get그룹목록(); //모든 그룹 리스트

        //모든 그룹리스트의
        for(int i = 0; i < allList.size(); i++){
            //모든 member 목록을 뒤져서
            try{
                for(int j = 0; j < allList.get(i).getmMember().size(); j++) {
                    //유저의 메일과 같다면
                    if (mail.equals(allList.get(i).getmMember().get(j).getmMail())) {
                        list.add(allList.get(i));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }


        // 유저가 포함된 <Shared_Group>만 리스트에 넣고 반환해요
        return list;
    }

    public ArrayList<itemdata_studygroup_child> 유저공부시간더하기(ArrayList<itemdata_studygroup_child> members, String email, int min, int sec){


        for(int i = 0; i< members.size(); i++){
            if(members.get(i).getmMail().equals(email)){

                String newtime = timeplus(members.get(i).getmTime(), min, sec);
                members.get(i).setmTime(newtime);
            }
        }
        return members;
    }

    //시간변환후 더하는 메소드
    public String timeplus(String oldtime, int min, int sec){
        String returntime;

        String[] array = oldtime.split(":");
        int oldmin = Integer.parseInt(array[0]);
        int oldsec = Integer.parseInt(array[1]);

        oldmin = oldmin + min;
        oldsec = oldsec + sec;
        returntime = String.format("%02d:%02d", oldmin, oldsec);
        return  returntime;
    }


//
//    public ArrayList<Shared_user> get각유저이름목록(String)


}
