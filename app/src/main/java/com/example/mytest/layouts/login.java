package com.example.mytest.layouts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mytest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class login extends AppCompatActivity {
    TextView login;
    Button btn;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1login);

        //자동로그인
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        if(firebaseUser != null){
//            Intent intent = new Intent(login.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }

        login = (TextView) findViewById(R.id.txt_로그인하기);
        login.setOnClickListener(new View.OnClickListener(){

        @Override
       public void onClick(View v) {
           Intent intent = new Intent(login.this, idpw.class);
           startActivity(intent);
        }
    });

        btn = (Button) findViewById(R.id.btn_이메일로가입);
        btn.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(login.this, newuser.class);
            startActivity(intent);
            }
        });

    }






}
