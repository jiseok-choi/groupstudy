package com.example.mytest.layouts;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mytest.R;

public class studynewText extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;

    ImageButton imageButton;
    EditText editText;
    String Text;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studynew_text);

        toolbar = findViewById(R.id.toolbar일반글);
        setSupportActionBar(toolbar);
        toolbar.setTitle("글");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼 생성 true값 넣으면 버튼이 생김



        imageButton = (ImageButton) findViewById(R.id.text_check);
        editText = (EditText) findViewById(R.id.input_Text);


        ///////수정test///////
        Intent getintent = getIntent();
        intent = new Intent(studynewText.this, studynow.class);
        if(getintent.getExtras() != null){
            Log.i("studynewText","수정시 intent넘어옴");
            Bundle bundle = getintent.getExtras();
            editText.setText(bundle.getString("수정내용"));
            int itemnum = bundle.getInt("수정번호");
            intent.putExtra("itemposition",itemnum);
            intent.addFlags(intent.FLAG_ACTIVITY_FORWARD_RESULT);
            Text = editText.getText().toString();
            intent.putExtra("text", Text);
        }
        ///////수정test///////



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Text = editText.getText().toString();
                intent.putExtra("text", Text);
                intent.putExtra("whatisthis",2);
                setResult(RESULT_OK, intent);

                finish();

            }
        });



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //툴바의 메뉴버튼을 눌렀을 때 동작하는 case문들
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
