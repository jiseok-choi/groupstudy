package com.example.mytest.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
import android.widget.ExpandableListView;

import com.example.mytest.R;
import com.example.mytest.adapter.Adapter_studygroup;
import com.example.mytest.adapteritems.itemdata_studygroup_child;
import com.example.mytest.adapteritems.itemdata_studygroup_parent;
import com.example.mytest.sharedclass.Shared_Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_group extends Fragment {

    ExpandableListView listView;
    Adapter_studygroup adapter_studygroup;
    ArrayList<itemdata_studygroup_parent> GroupList = new ArrayList<>();
    ArrayList<ArrayList<itemdata_studygroup_child>> ChildList = new ArrayList<>();
    ArrayList<ArrayList<itemdata_studygroup_child>> group_management = new ArrayList<>();

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefGroup, docRefStudies;
    private ListenerRegistration GroupListen;


    SharedPreferences sh;
    SharedPreferences.Editor shgroupedit;

    public Fragment_group() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //뷰그룹을 리턴해주기 위함
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_fragment_group, container, false);

        listView = (ExpandableListView) rootView.findViewById(R.id.Expanded_group);

        //어뎁터 적용하기

        adapter_studygroup = new Adapter_studygroup(getActivity(), getContext());
        adapter_studygroup.parentItems = GroupList;
        adapter_studygroup.childItems = ChildList;

        listView.setAdapter(adapter_studygroup);
        //Drawable icon = getResources().getDrawable(R.drawable.downarrow);
        listView.setGroupIndicator(null);// listview 기본 아이콘 표시 여부
        try {
            sh = getContext().getSharedPreferences(Shared_Group.Group, Context.MODE_PRIVATE);
            shgroupedit = sh.edit();
        }catch (Exception e){
            e.printStackTrace();
        }


        Log.i("Fragment_group","onCreateView");
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Fragment_group","onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment_group","onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        //firebase
        docRefGroup = db.collection("Lets_study").document("Group");
        docRefGroup.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null){ //그룹이 존재할때
                    Map<String, Object> allgroup = task.getResult().getData();
                    for(String key : allgroup.keySet()){
                        //String newKey = key.replace("_",".");
                        shgroupedit.putString(key, allgroup.get(key).toString());
                    }
                    shgroupedit.apply();
                    adapter_studygroup.notifyDataSetChanged();
                    Log.i("shared_group","update완료");
                }
            }
        });

        docRefStudies = db.collection("Lets_study").document("Studies");
        docRefStudies.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null){ //그룹이 존재할때
                    Map<String, Object> allgroup = task.getResult().getData();
                    for(String key : allgroup.keySet()){
                        String newKey = key.replace("_",".");
                        shgroupedit.putString(newKey, allgroup.get(key).toString());
                    }
                    shgroupedit.apply();
                    adapter_studygroup.notifyDataSetChanged();
                    Log.i("shared_group","update완료");
                }
                setListItems();
            }
        });

        Log.i("Fragment_group","onStart");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("Fragment_group","onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("Fragment_group","onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Fragment_group","onDestroy");
    }

    ////////////////////////////////////////////////////////////////////
    ////////어뎁터 데이터 추가(임시)
    public void setListItems(){
        group_management.clear();
        GroupList.clear();
        ChildList.clear();


        //사용자 주키 생성후 반환
        SharedPreferences Shared_whoami = getContext().getSharedPreferences("shared_whoami", Context.MODE_PRIVATE);
        String whoami = Shared_whoami.getString("whoami", "");

        SharedPreferences sh_group = getContext().getSharedPreferences(Shared_Group.Group, Context.MODE_PRIVATE);
        Shared_Group groupList = new Shared_Group(sh_group);
        //유저가 속한데이터 그룹을 반환한다.
        ArrayList<Shared_Group> list = groupList.get유저가속한그룹(whoami);


        for (int i = 0; i<list.size(); i++){
            //페런트 공간 확보하는 부분
            group_management.add(new ArrayList<itemdata_studygroup_child>());
        }
        ChildList.addAll(group_management);

        //익스펜더블 리스트뷰에 데이터 바인딩을 해줄거야
        for(int i = 0; i < list.size(); i++){
            //parent의 데이터 바인딩
            GroupList.add(new itemdata_studygroup_parent(list.get(i).getmGroupname()));

            //차일드의 데이터 바인딩
            for (int j = 0; j < list.get(i).getmMember().size(); j++){

                itemdata_studygroup_child item = new itemdata_studygroup_child(list.get(i).getmMember().get(j).getmMail(), list.get(i).getmMember().get(j).getmName(), list.get(i).getmMember().get(j).getmTime());

                group_management.get(i).add(item);

            }
        }
        adapter_studygroup.notifyDataSetChanged();



//        //for문
//
//        //for문 안에 for문 작성 예정
//        GroupList.add(new itemdata_studygroup_parent("팀노바 스터디 그룹"));
//
//        itemdata_studygroup_child item = new itemdata_studygroup_child("10:10 최지석");
//        itemdata_studygroup_child item3 = new itemdata_studygroup_child("7:12 김노바");
//        itemdata_studygroup_child item4 = new itemdata_studygroup_child("5:07 호시기");
//        itemdata_studygroup_child item5 = new itemdata_studygroup_child("2:40 김철민");
//
//        group_management.get(0).add(item);
//        group_management.get(0).add(item3);
//        group_management.get(0).add(item4);
//        group_management.get(0).add(item5);
//
//
//
//
//        GroupList.add(new itemdata_studygroup_parent("영어회화 스터디 그룹"));
//
//        itemdata_studygroup_child item2 = new itemdata_studygroup_child("11:12 최지석");
//        itemdata_studygroup_child item1 = new itemdata_studygroup_child("7:00 스미스킴");
//
//        group_management.get(1).add(item2);
//        group_management.get(1).add(item1);
//
//
//
//
//
//        adapter_studygroup.notifyDataSetChanged();


    }



    ////////어뎁터 데이터 추가(임시)


}
