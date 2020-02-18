package com.example.mytest;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytest.adapter.Adapter_newFrends;
import com.example.mytest.adapteritems.itemdata_frends;
import com.example.mytest.firebaseclass.firebaseuser;
import com.example.mytest.layouts.idpw;
import com.example.mytest.sharedclass.Shared_class;
import com.example.mytest.sharedclass.Shared_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

public class newFrends extends AppCompatActivity {

    EditText member1, member2, member3, member4, member5;
    Button btn_친구신청;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<itemdata_frends> dataList = new ArrayList<>();
    Adapter_newFrends frendsAdapter;

    //firebase
//    private FirebaseAuth mAuth;
//    private DatabaseReference 사용자reference, 유저reference;
//    private firebaseuser firebaseuser;
    ArrayList<String> newFrendList;
    ArrayList<firebaseuser> 미수락친구목록2;
    boolean flag = true; //한번만실행할수 있게
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefUsers;
    Map.Entry<String, Object> entry;
    Shared_user 사용자클래스;
    SharedPreferences sh_user;
    String 사용자;
    Shared_class shared_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_frends);

        Log.i("유저reference", "실행됨");
        ArrayList<String> 미수락친구목록 = new ArrayList<>();

        //firebase
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        assert user != null;
//        String userid = user.getUid();


        //리사이클러뷰 사용하기
        recyclerView = findViewById(R.id.recycle_새친구);
        //리사이클러뷰 매니저만들기(리사이클러뷰는 매니저필수)
        layoutManager = new LinearLayoutManager(newFrends.this);
        recyclerView.setLayoutManager(layoutManager);
        //adapter setting
        frendsAdapter = new Adapter_newFrends(dataList, newFrends.this);
        recyclerView.setAdapter(frendsAdapter);

        //사용자 메일 불러오기
        SharedPreferences shared_whoami = getSharedPreferences("shared_whoami", MODE_PRIVATE);
        사용자 = shared_whoami.getString("whoami", null);
        //유저 쉐어드
        sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
        final SharedPreferences.Editor newfrend_Editor = sh_user.edit();
        final Gson gson = new Gson();



        //firebase 사용자 정보 가져오기
        docRefUsers = db.collection("Lets_study").document("Users");
        docRefUsers.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //모든 유저 복사
                Map<String, Object> allUsers = task.getResult().getData();
                for(String key : allUsers.keySet()){
                    String mail = key.replace("_","."); //매우중요
                    newfrend_Editor.putString(mail, allUsers.get(key).toString());
                }
                newfrend_Editor.apply();
                //유저중 사용자 유저 가져오기
                //해당하는 유저인 json을 가져와서 객체화 해준다.
                String user = allUsers.get(idpw.fireEmail).toString();
                shared_class = new Shared_class();
                사용자클래스 = shared_class.fromuser(user);
                newFrendList = 사용자클래스.getmAreyoufrend();
                dataList.clear();
                //데이터 바인딩
                for(int i = 0; i < newFrendList.size(); i++){
                    String newjson = sh_user.getString(newFrendList.get(i), "");
                    Shared_user newuser = gson.fromJson(newjson, Shared_user.class);

                    String mail = newuser.getmMail();
                    String name = newuser.getmName();
                    dataList.add(new itemdata_frends(mail, name));
                }
                frendsAdapter.notifyDataSetChanged();
                //값이 바뀔때마다 리스너 만들어줘서 계속 리사이클러뷰 수정할 것
                docRefUsers.addSnapshotListener(newFrends.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        //frendsAdapter.notifyDataSetChanged();
                        if (e != null) {
                            Log.w("newFrends","Listen failed.", e);
                            return;
                        }
                        if(snapshot.exists()) {
                            docRefUsers.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    //모든 유저 복사
                                    Map<String, Object> allUsers = task.getResult().getData();
                                    for(String key : allUsers.keySet()){
                                        String mail = key.replace("_","."); //매우중요
                                        newfrend_Editor.putString(mail, allUsers.get(key).toString());
                                    }
                                    newfrend_Editor.apply();
                                }
                            });

//                            sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
//                            shared_class = new Shared_class();
//                            사용자클래스 = shared_class.fromuser(sh_user.getString(사용자, ""));
//                            newFrendList = 사용자클래스.getmAreyoufrend();
//                            dataList.clear();
//                            //데이터 바인딩
//                            for(int i = 0; i < newFrendList.size(); i++){
//                                String newjson = sh_user.getString(newFrendList.get(i), "");
//                                Shared_user newuser = gson.fromJson(newjson, Shared_user.class);
//
//                                String mail = newuser.getmMail();
//                                String name = newuser.getmName();
//                                dataList.add(new itemdata_frends(mail, name));
//                            }
//                            frendsAdapter.notifyDataSetChanged();
                            Log.w("newFrends","누군가 신청함 리스너 실행됨..");
                        }else{
                            Log.d("newFrends","Current data: null");
                        }
                    }
                });
            }
        });

        //사용자의 미수락 친구 목록을 가져와서 리사이클러뷰에 바인딩해주는 부분



        //데이터 바인딩




        //전체 유저 정보가 담겨있는 클래스
        //dataList.clear();

        //frendsAdapter.notifyDataSetChanged();









        member1 = findViewById(R.id.member1);
        member2 = findViewById(R.id.member2);
        member3 = findViewById(R.id.member3);
        member4 = findViewById(R.id.member4);
        member5 = findViewById(R.id.member5);


        btn_친구신청 = findViewById(R.id.btn_친구신청);
        btn_친구신청.setText("신 청");
        btn_친구신청.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String txt_member1 = member1.getText().toString();
                String txt_member2 = member2.getText().toString();
                String txt_member3 = member3.getText().toString();
                String txt_member4 = member4.getText().toString();
                String txt_member5 = member5.getText().toString();

                Shared_user shared_user;
                //사용자 메일 불러오기
                SharedPreferences shared_whoami = getSharedPreferences("shared_whoami", MODE_PRIVATE);
                String 사용자 = shared_whoami.getString("whoami", null);

                SharedPreferences user = getSharedPreferences(Shared_user.user,MODE_PRIVATE);
                String pass = user.getString(txt_member1,"pass");
                if(!txt_member1.equals("") && pass.equals("pass")){ //입력값의 회원이 없을때
                    Toast.makeText(newFrends.this, "메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    try{
                        shared_user = gson.fromJson(pass, Shared_user.class);
                        for (int i = 0; i < shared_user.getmFrands().size(); i++) {
                            if (shared_user.getmFrands().get(i).equals(사용자)) {
                                Toast.makeText(newFrends.this, txt_member1 + "계정은 이미 친구입니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                pass = user.getString(txt_member2,"pass");
                if(!txt_member2.equals("") && pass.equals("pass")){
                    Toast.makeText(newFrends.this, "메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    try{
                        shared_user = gson.fromJson(pass, Shared_user.class);
                        for (int i = 0; i < shared_user.getmFrands().size(); i++) {
                            if (shared_user.getmFrands().get(i).equals(사용자)) {
                                Toast.makeText(newFrends.this, txt_member2 + "계정은 이미 친구입니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                pass = user.getString(txt_member3,"pass");
                if(!txt_member3.equals("") && pass.equals("pass")){
                    Toast.makeText(newFrends.this, "메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    try{
                        shared_user = gson.fromJson(pass, Shared_user.class);
                        for (int i = 0; i < shared_user.getmFrands().size(); i++) {
                            if (shared_user.getmFrands().get(i).equals(사용자)) {
                                Toast.makeText(newFrends.this, txt_member3 + "계정은 이미 친구입니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                pass = user.getString(txt_member4,"pass");
                if(!txt_member4.equals("") && pass.equals("pass")){
                    Toast.makeText(newFrends.this, "메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    try{
                        shared_user = gson.fromJson(pass, Shared_user.class);
                        for (int i = 0; i < shared_user.getmFrands().size(); i++) {
                            if (shared_user.getmFrands().get(i).equals(사용자)) {
                                Toast.makeText(newFrends.this, txt_member4 + "계정은 이미 친구입니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                pass = user.getString(txt_member5,"pass");
                if(!txt_member5.equals("") && pass.equals("pass")){
                    Toast.makeText(newFrends.this, "메일을 확인해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    try{
                        shared_user = gson.fromJson(pass, Shared_user.class);
                        for (int i = 0; i < shared_user.getmFrands().size(); i++) {
                            if (shared_user.getmFrands().get(i).equals(사용자)) {
                                Toast.makeText(newFrends.this, txt_member5 + "계정은 이미 친구입니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                //조건 통해서 어레이에 담기
                final ArrayList<String> member = new ArrayList<>();
                if(!txt_member1.equals("")){
                    member.add(txt_member1);
                }
                if(!txt_member2.equals("")){
                    member.add(txt_member2);
                }
                if(!txt_member3.equals("")){
                    member.add(txt_member3);
                }
                if(!txt_member4.equals("")){
                    member.add(txt_member4);
                }
                if(!txt_member5.equals("")){
                    member.add(txt_member5);
                }


                //유저 쉐어드
                sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
                final SharedPreferences.Editor newfrend_Editor = sh_user.edit();
                Gson gson = new Gson();
                //복사했음

                //동일한 이름이 있는지 판단하기
                for (int i = 0; i < member.size() - 1; i++){
                    for(int j = i + 1; j < member.size(); j++){
                        if(member.get(i).equals(member.get(j))){
                            Toast.makeText(newFrends.this, "동일한 메일이 있습니다", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                //본인에게 신청했는지 판단
                for(int i = 0; i < member.size(); i++){
                    if(member.get(i).equals(사용자)){
                        Toast.makeText(newFrends.this, "사용자 메일이 포함되었습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }




                ArrayList<String> newfrends;


            for (int i = 0; i < member.size(); i++){
                //i 번째의 친구의 미수락 친구목록을 가져오는 것
                String json = sh_user.getString(member.get(i), "");
                shared_user = gson.fromJson(json, Shared_user.class);
                newfrends = shared_user.getmAreyoufrend();

                //동일한 친구 신청 방지
                boolean sameperson = false;
                for(int j = 0; j < newfrends.size(); j++){
                    if(newfrends.get(j).equals(사용자)){
                    sameperson = true;}
                }
                //친구목록 미수락 친구목록 더하기
                if(!sameperson){
                newfrends.add(사용자);}

                //친구를 더했으면 다시 shared 저장하고 커밋하기
                final String tojson = gson.toJson(shared_user);


                Map<String, Object> changeyou = new HashMap<>();
                String you = member.get(i).replace(".","_");
                changeyou.put(you, tojson);
                final int finalI = i;
                docRefUsers.set(changeyou, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            newfrend_Editor.putString(member.get(finalI), tojson);
                            newfrend_Editor.apply();
                        }else{
                            Toast.makeText(newFrends.this, member.get(finalI)+" 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            Toast.makeText(newFrends.this, member.size()+"명에게 친구신청 했습니다", Toast.LENGTH_SHORT).show();

            }
        });



//        //리사이클러뷰 사용하기
//        recyclerView = findViewById(R.id.recycle_새친구);
//        //리사이클러뷰 매니저만들기(리사이클러뷰는 매니저필수)
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        //adapter setting
//        frendsAdapter = new Adapter_newFrends(dataList, this);
//        recyclerView.setAdapter(frendsAdapter);
//        //데이터 바인딩
//        String fromjson = sh_user.getString(사용자, "");
//        Shared_user user123 = gson.fromJson(fromjson, Shared_user.class);
//        ArrayList<String> newFrendList = user123.getmAreyoufrend();
//
//        for(int i = 0; i < newFrendList.size(); i++){
//            String newjson = sh_user.getString(newFrendList.get(i), "");
//            Shared_user newuser = gson.fromJson(newjson, Shared_user.class);
//
//            String mail = newuser.getmMail();
//            String name = newuser.getmName();
//            dataList.add(new itemdata_frends(mail, name));
//        }
//        frendsAdapter.notifyDataSetChanged();

    }
}
