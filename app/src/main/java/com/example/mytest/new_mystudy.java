package com.example.mytest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mytest.Fragment.Fragment_my;
import com.example.mytest.layouts.idpw;
import com.example.mytest.sharedclass.Shared_Group;
import com.example.mytest.sharedclass.Shared_Studies;
import com.example.mytest.sharedclass.Shared_class;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class new_mystudy extends AppCompatActivity {

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefStudies;
    String whoami;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5new_mystudy);



        //spinner에 추가할 스터디 그룹명 불러오기 Fragment_group 부분에서 따오기
        //사용자 주키 생성후 반환
        final SharedPreferences Shared_whoami = getSharedPreferences("shared_whoami", MODE_PRIVATE);
        final String whoami = Shared_whoami.getString("whoami", "");

        final SharedPreferences sh = getSharedPreferences(Shared_Group.Group, MODE_PRIVATE);
        Shared_Group groupList = new Shared_Group(sh);
        //유저가 속한데이터 그룹을 반환한다.
        ArrayList<Shared_Group> list = new ArrayList<Shared_Group>();
        list = groupList.get유저가속한그룹(whoami);

        ArrayList<String> 그룹선택목록 = new ArrayList<>();
        if(list.size() == 0){
            Toast.makeText(new_mystudy.this, "그룹을 먼저 만들어주세요!",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        for(int i = 0; i < list.size(); i++){
            그룹선택목록.add(list.get(i).getmGroupname());
        }




        //String[] 그룹선택목록 = {"팀노바 스터디 그룹", "영어회화 스터디 그룹"};

        final Spinner sp그룹 = (Spinner)findViewById(R.id.spinner_그룹선택하기);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                그룹선택목록);
        sp그룹.setAdapter(adapter);
        sp그룹.setSelection(0);

        //multitext주제정하기
        final MultiAutoCompleteTextView multitext주제정하기 = findViewById(R.id.multitext주제정하기);
        final MultiAutoCompleteTextView multitext목표정하기 = findViewById(R.id.multitext목표정하기);
        String 목표 = multitext목표정하기.getText().toString();
        Button button = findViewById(R.id.btn_주제정하기확인);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String 주제 = multitext주제정하기.getText().toString();
                String 목표 = multitext목표정하기.getText().toString();
                //key 값이 될 주제는 반드시 입력해야한다.
                if(주제.equals("")){
                    Toast.makeText(new_mystudy.this, "주제는 필수 입력입니다", Toast.LENGTH_SHORT).show();
                    return;
                }



                //주제가 선정되면 필요한 값을 쉐어드에 넣어보자.
                //1. 값 가져오기 - 스피너에 선택된 그룹명 (주제와 목표는 위에서 다 넣어주었음)
                String spinner = sp그룹.getSelectedItem().toString();
                //2. Shared_Studies 클래스에 필요한 데이터 넣기
                final Shared_Studies shared_studies = new Shared_Studies();
                shared_studies.setmTitle(주제);
                shared_studies.setmMakeUsermail(whoami);
                shared_studies.setmGroup(spinner);
                shared_studies.setmTime("00:00");
                shared_studies.setmGoal(목표);
                shared_studies.setmComplete(false);
                shared_studies.setmDate(Integer.toString(Fragment_my.mYear) + Integer.toString(Fragment_my.mMonth) + Integer.toString(Fragment_my.mDay));

                //3. 쉐어드 선언하기
                SharedPreferences Sh_Studies = getSharedPreferences(Shared_Studies.Studies, MODE_PRIVATE);
                final SharedPreferences.Editor studies_edit = Sh_Studies.edit();
                //4. gson으로 shared_studies json으로 넣기
                Gson gson = new Gson();
                final String json = gson.toJson(shared_studies);


                //firebase 저장
                docRefStudies = db.collection("Lets_study").document("Studies");
                Map<String, Object> changeStudies = new HashMap<>();
                String newkey = shared_studies.makeKey().replace(".","_");
                changeStudies.put(newkey, json);
                docRefStudies.set(changeStudies, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        studies_edit.putString(shared_studies.makeKey(), json);
                        studies_edit.apply();
                        finish();
                    }
                });


                Log.i("shared_studies",json+"");

            }
        });





    }
}
