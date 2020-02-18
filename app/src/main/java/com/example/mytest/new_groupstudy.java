package com.example.mytest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytest.adapter.Adapter_newstudygroup;
import com.example.mytest.adapteritems.itemdata_frends;
import com.example.mytest.adapteritems.itemdata_studygroup_child;
import com.example.mytest.layouts.idpw;
import com.example.mytest.sharedclass.Shared_Group;
import com.example.mytest.sharedclass.Shared_user;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class new_groupstudy extends AppCompatActivity {

    Button btn그룹만들기;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<itemdata_frends> dataList = new ArrayList<>();
    Adapter_newstudygroup groupAdapter;
    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefUsers;
    DocumentReference docRefGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_groupstudy);


        final EditText edit_groupname = findViewById(R.id.edit_Groupname);

        //유저 쉐어드 선언해주기
        final SharedPreferences sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
        SharedPreferences shared_whoami = getSharedPreferences("shared_whoami", MODE_PRIVATE);
        final String 사용자 = shared_whoami.getString("whoami", null);
        final Gson gson = new Gson();

        final SharedPreferences sh_group = getSharedPreferences(Shared_Group.Group, MODE_PRIVATE);
        final SharedPreferences.Editor groupEdit = sh_group.edit();


        //리사이클러뷰 만들기
        recyclerView = findViewById(R.id.recycle_newgroup);
        //리사이클러뷰 매니저 만들기 (필수)
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //어뎁터세팅
        groupAdapter = new Adapter_newstudygroup(dataList);
        recyclerView.setAdapter(groupAdapter);


        //데이터 바인딩
        String fromjson = sh_user.getString(사용자, "");
        Shared_user user = gson.fromJson(fromjson, Shared_user.class);
        ArrayList<String> FrendList = user.getmFrands();

        for(int i = 0; i < FrendList.size(); i++){
            String mail = FrendList.get(i);
            String frendname = sh_user.getString(mail, "");
            Shared_user frend = gson.fromJson(frendname, Shared_user.class);
            String name = null;

            try{
                name = frend.getmName();
            }catch (Exception e){
                name = "test";
            }

            dataList.add(new itemdata_frends(mail, name));
        }
        groupAdapter.notifyDataSetChanged();





        //나중에 변경 요망 지금은 일일히 조건줘서 더해주는 방식
        ArrayList<String> member = new ArrayList<>();
        btn그룹만들기 = findViewById(R.id.btn_확인);
        btn그룹만들기.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<itemdata_frends> groupList = groupAdapter.getmDataList();
                ArrayList<itemdata_studygroup_child> 소속인원목록 = new ArrayList<>();
                Shared_user shared_user = new Shared_user(sh_user);
                shared_user = shared_user.get유저정보(사용자);

                itemdata_studygroup_child itemdata_studygroup_child = new itemdata_studygroup_child(shared_user.getmMail(), shared_user.getmName());
                소속인원목록.add(itemdata_studygroup_child);
                for(int i = 0; i <dataList.size(); i++){
                    itemdata_frends frend = groupList.get(i);
                    if (frend.incheck == true){
                        String 메일 = frend.getMail();
                        String 이름 = frend.getName();
                        itemdata_studygroup_child itemdata_studygroup_child2 = new itemdata_studygroup_child(메일, 이름);
                        소속인원목록.add(itemdata_studygroup_child2);
                    }
                }
                //
                groupAdapter.notifyDataSetChanged();


                final String title = edit_groupname.getText().toString();
                Shared_Group shared_group = new Shared_Group();
                shared_group.setmGroupname(title);
                shared_group.setmMember(소속인원목록);

                final String tojson = gson.toJson(shared_group);




                Map<String, Object> createGroup = new HashMap<>();
                createGroup.put(title, tojson);

                docRefGroup = db.collection("Lets_study").document("Group");
                docRefGroup.set(createGroup, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(new_groupstudy.this, "그룹이 생성되었습니다.",Toast.LENGTH_SHORT).show();
                            finish();
                            groupEdit.putString(title, tojson);
                            groupEdit.apply();
                        }
                        else{
                            Toast.makeText(new_groupstudy.this, "다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });



            }
        });




    }
}
