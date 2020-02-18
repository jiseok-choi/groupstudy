package com.example.mytest;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mytest.layouts.studynewText;
import com.example.mytest.layouts.studynewTitle;

import java.io.BufferedOutputStream;

public class newstudynow extends AppCompatActivity {


    FloatingActionButton btn, btn1, btn2;
    private int REQUEST_ACT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newstudynow);


        btn = (FloatingActionButton) findViewById(R.id.actionbtn_Title);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(newstudynow.this, studynewTitle.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        btn1 = (FloatingActionButton) findViewById(R.id.actionbtn_Text);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(newstudynow.this, studynewText.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        btn2 = (FloatingActionButton) findViewById(R.id.actionbtn_Camera);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.putExtra("whatisthis",3);
                    startActivity(intent);
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }
}
