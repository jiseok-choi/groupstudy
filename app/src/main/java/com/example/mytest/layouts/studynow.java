package com.example.mytest.layouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mytest.Fragment.Fragment_my;
import com.example.mytest.Progress;
import com.example.mytest.R;
import com.example.mytest.adapter.Adapter_studynow;
import com.example.mytest.adapteritems.studynowItem;
import com.example.mytest.new_mystudy;
import com.example.mytest.newstudynow;
import com.example.mytest.sharedclass.Shared_Studies;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.gun0912.tedpermission.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mytest.layouts.MainActivity.수정가능;


public class studynow extends AppCompatActivity {
    public static int popopopo;

    Button btn, btn3;
    FloatingActionButton btn2;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<studynowItem> dataList = new ArrayList<>();
    Adapter_studynow studyadapter;
    String inputTitle;
    private int REQUEST_NEW = 2, REQUEST_REMOVE = 3, REQUEST_CHANGE = 4;
    private File tempFile;
    public static String 공부명;
    public static Shared_Studies shared_studies;

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefStudies;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Uri downloadUri;
    String date;
    String whoami;
    Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studynow);

        firebaseAuth = FirebaseAuth.getInstance();


        final SharedPreferences Sh_Studies = getSharedPreferences(Shared_Studies.Studies, MODE_PRIVATE);
        final SharedPreferences.Editor Sh_Studiesedit = Sh_Studies.edit();
        docRefStudies = db.collection("Lets_study").document("Studies");
        docRefStudies.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> allStudies = task.getResult().getData();
                if(!ObjectUtils.isEmpty(allStudies)){
                    for(String key : allStudies.keySet()){
                        String newkey = key.replace("_",".");
                        Sh_Studiesedit.putString(newkey, allStudies.get(key).toString());
                    }
                    Sh_Studiesedit.apply();





                }



                //공부명, 목표, 소속그룹명 데이터 바인딩



                Intent intent = getIntent();
                if(intent.getExtras() == null){ //회원가입 안통하고 들어왔을때 오류 방지
                    finish();
                }
                Bundle bundle = intent.getExtras(); //액스트라에서 받아주는 것
                String itsme = bundle.getString("친구메일", "itsme");
                if(itsme.equals("itsme")){
                    SharedPreferences Shared_whoami = getSharedPreferences("shared_whoami", MODE_PRIVATE);
                    whoami = Shared_whoami.getString("whoami", "");
                    date = Integer.toString(Fragment_my.mYear) + Integer.toString(Fragment_my.mMonth) + Integer.toString(Fragment_my.mDay);
                }else{
                    whoami = itsme;
                    date = Integer.toString(FrendsActivity.mYear) + Integer.toString(FrendsActivity.mMonth) + Integer.toString(FrendsActivity.mDay);
                }
                공부명 = bundle.getString("공부명");

                String json = Sh_Studies.getString(whoami+공부명+date, "");
                Gson gson = new Gson();
                shared_studies = gson.fromJson(json, Shared_Studies.class);



                TextView studynow_주제 = findViewById(R.id.studynow_주제);
                studynow_주제.setText(shared_studies.getmTitle());
                TextView studynow_그룹 = findViewById(R.id.studynow_그룹);
                studynow_그룹.setText(shared_studies.getmGroup());
                TextView studynow_목표 = findViewById(R.id.studynow_목표);
                studynow_목표.setText(shared_studies.getmGoal());

                dataList = shared_studies.getmStudynowItem();

                //리사이클러뷰 사용하기
                recyclerView = findViewById(R.id.recycle_studynow);
                //리사이클러뷰 메니저만들기(listview와 다르게 필수)
                layoutManager = new LinearLayoutManager(studynow.this);
                recyclerView.setLayoutManager(layoutManager);//리스트뷰에 레이아웃 매니저를 셋팅해준다.


                //어뎁터 설정
                studyadapter = new Adapter_studynow(dataList, studynow.this);
                recyclerView.setAdapter(studyadapter);

                studyadapter.notifyDataSetChanged();
            }
        });















        btn = (Button) findViewById(R.id.btn_수정);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(studynow.this, new_mystudy.class);
                startActivity(intent);
            }
        });


        btn2 = (FloatingActionButton) findViewById(R.id.studynow_plus);
        btn2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(studynow.this, newstudynow.class);
                startActivityForResult(intent, REQUEST_NEW);
            }
        });

        //다른사람이 들어왔을때
        if(!수정가능){
            btn2.setEnabled(false);
            btn2.hide();
        }


//        int num = recyclerView.getChildViewHolder(v).getAdapterPosition();
    }

    //쉐어드 메서드 만들기
    public void sharedstudies(ArrayList<studynowItem> dataList ) {
        SharedPreferences Shared_whoami = getSharedPreferences("shared_whoami", MODE_PRIVATE);
        final String whoami = Shared_whoami.getString("whoami", "");

        //final String date = Integer.toString(Fragment_my.mYear) + Integer.toString(Fragment_my.mMonth) + Integer.toString(Fragment_my.mDay);
//
        SharedPreferences Sh_Studies = getSharedPreferences(Shared_Studies.Studies, MODE_PRIVATE);
        String json = Sh_Studies.getString(whoami + 공부명 + date, "");
        Gson gson;//= new Gson();
        //shared_studies = gson.fromJson(json, Shared_Studies.class);
        shared_studies.setmStudynowItem(dataList);
        gson = new Gson();
        final String json1 = gson.toJson(shared_studies);
        final SharedPreferences.Editor editor = Sh_Studies.edit();

        docRefStudies = db.collection("Lets_study").document("Studies");
        Map<String, Object> changestudies = new HashMap<>();
        changestudies.put(idpw.fireEmail+공부명+date, json1);
        docRefStudies.set(changestudies, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                editor.putString(whoami+공부명+date, json1);
                editor.apply();
                Log.i("uploadImage","db에 정상 저장");
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        firebaseAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("sign", "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent getIntent = getIntent();
//        if(getIntent.getExtras() != null){
//            Log.i("studynewTitle","수정시 intent넘어옴");
//            Bundle bundle = getIntent.getExtras();
//            String title = bundle.getString("title");
//
//            dataList.set(bundle.getInt("itemposition"), new studynowItem(0, title,"title"));
//        }


    }

    //리사이클러뷰 데이터 받아줄 onActivityResult 만들어보자
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("studynow_onActivityResult", "실행됨");

        //Log.e("테스트포지션", ""+data.getExtras().getInt("itemposition"));

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_NEW) {
            Log.i("추가테스트", "new 넘어옴");

            if (data.getIntExtra("whatisthis", 3) == 3) {
                progress = new Progress();
                progress.showProgressDialog(studynow.this);
                Uri photoUri = data.getData();


                uploadImage(photoUri, true);
//                dataList.add(new studynowItem(2, photoUri, studynow.this));
//                sharedstudies(dataList);
//                studyadapter.notifyDataSetChanged();
            } else if (data.getIntExtra("whatisthis", 1) == 1) {
                inputTitle = data.getStringExtra("title");
                dataList.add(new studynowItem(0, inputTitle, "title"));
                sharedstudies(dataList);
                studyadapter.notifyDataSetChanged();
                data.removeExtra("title");
            } else if (data.getIntExtra("whatisthis", 2) == 2) {
                inputTitle = data.getStringExtra("text");
                dataList.add(new studynowItem(1, inputTitle, "text"));
                sharedstudies(dataList);
                studyadapter.notifyDataSetChanged();
                data.removeExtra("text");

            }
        }
            if (requestCode == REQUEST_CHANGE) {
                if(data.getIntExtra("whatisthis", 3) == 3){
                    Uri photoUri = data.getData();
                    uploadImage(photoUri, false);

                }else if (data.getIntExtra("whatisthis", 1) == 1) {
                    Bundle bundle = data.getExtras();
                    String title = bundle.getString("title");
                    dataList.set(bundle.getInt("itemposition"), new studynowItem(0, title, "title"));
                    sharedstudies(dataList);
                    studyadapter.notifyDataSetChanged();
                } else if (data.getIntExtra("whatisthis", 2) == 2) {
                    Bundle bundle = data.getExtras();
                    String text = bundle.getString("text");
                    dataList.set(bundle.getInt("itemposition"), new studynowItem(1, text, "text"));
                    sharedstudies(dataList);
                    studyadapter.notifyDataSetChanged();
                }


            }
    }
    public void uploadImage(Uri photoUri, final boolean areyouadd){
        Cursor cursor = null;

        try {

            /*
             *  Uri 스키마를
             *  content:/// 에서 file:/// 로  변경한다.
             */
            String[] proj = { MediaStore.Images.Media.DATA };

            assert photoUri != null;
            cursor = getContentResolver().query(photoUri, proj, null, null, null);

            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            tempFile = new File(cursor.getString(column_index));

        } finally { //꼭 실행해야만 하는 구문을 넣자
            if (cursor != null) {
                cursor.close();
            }
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        //Uri file = Uri.fromFile(new File(photoUri))

        //스토리지에 저장하기  //: if request.auth != null 이거 규칙에서 삭제함
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference userRef = storageRef.child("Studies").child(idpw.fireEmail+SystemClock.elapsedRealtime()+".jpg");
        Log.i("uploadImage", "storage호출");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        originalBm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = userRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("uploadImage", "이미지 스토리지 업로드 실패");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            //@SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i("uploadImage", "이미지 스토리지 업로드 성공");
            }
        });
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }

                //Continuew with the task to get the download URL
                return userRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    downloadUri = task.getResult();
                    Log.i("downloadUrl",downloadUri+"");


                    //추가여부로 add, set 구분해주기기
                   if(areyouadd){
                        dataList.add(new studynowItem(2, downloadUri, studynow.this));
                    }else{
                        dataList.set(popopopo, new studynowItem(2, downloadUri, studynow.this));
                    }



                    sharedstudies(dataList);
                    studyadapter.notifyDataSetChanged();
                    progress.closeProgressDialog();
                    Log.i("uploadImage", "정상저장됨");
                }
            }
        });

    }



    //리사이클러뷰 수정 삭제 위해 만들 다이얼로그
//    final CharSequence[] Items = {"수정","삭제"};
//
//    AlertDialog.Builder dialog = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
//
//    dialog.setTitle("작업을 선택하세요").set


}
