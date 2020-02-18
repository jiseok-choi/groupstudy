package com.example.mytest.Fragment;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mytest.BusProvider;
import com.example.mytest.R;
import com.example.mytest.adapter.Adapter_mystudy;
import com.example.mytest.adapteritems.itemdata_mystudy_child;
import com.example.mytest.adapteritems.itemdata_mystudy_parent;
import com.example.mytest.firebaseclass.firebaseuser;
import com.example.mytest.layouts.MainActivity;
import com.example.mytest.layouts.idpw;
import com.example.mytest.sharedclass.Shared_Group;
import com.example.mytest.sharedclass.Shared_Studies;
import com.example.mytest.sharedclass.Shared_class;
import com.example.mytest.sharedclass.Shared_user;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.app.Activity.RESULT_OK;
import static com.example.mytest.layouts.MainActivity.iv_UserPhoto;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_my extends Fragment implements View.OnClickListener {

    //본인사진찍기_변수생성해볼까
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    int sYear, sMonth, sDay;

    private Uri mImageCaptureUri;// 통합자원식별자, getData()함수로 사진이미지 가져올거임

    private int id_view;
    private String absoultePath;

    private File tempFile;

    Context context;

    /////////익스펜더블 리스트뷰 선언하기
    ExpandableListView listView;
    Adapter_mystudy adapter_mystudy;
    ArrayList<itemdata_mystudy_parent> GroupList = new ArrayList<>();
    ArrayList<ArrayList<itemdata_mystudy_child>> ChildList = new ArrayList<>();
    ArrayList<ArrayList<itemdata_mystudy_child>> group_management = new ArrayList<>();

    public static int mYear, mMonth, mDay;

    //firebase
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseuser;
    TextView name;
    String 사용자id;
    FirebaseUser user;
    Bitmap originalBm;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefUsers, docRefStudies;
    String whoami;
    public Fragment_my() {
        // Required empty public constructor
    }

    //프래그먼트 뷰 계층을 리턴
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        //뷰그룹을 리턴해주기 위함
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_fragment_my, container, false);

//        firebaseAuth = FirebaseAuth.getInstance();
//        final FirebaseUser user = firebaseAuth.getCurrentUser();
//        사용자id = user.getUid();
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(사용자id);

        firebaseAuth = FirebaseAuth.getInstance();


        listView = (ExpandableListView) rootView.findViewById(R.id.Expanded_mystudy);

        //어뎁터 적용하기

        adapter_mystudy = new Adapter_mystudy(rootView.getContext(), "nodata");
        adapter_mystudy.parentItems = GroupList;
        adapter_mystudy.childItems = ChildList;

        listView.setAdapter(adapter_mystudy);
        listView.setGroupIndicator(null); //listview 기본 아이콘 표시여부
        ///어뎁터 적용하기

        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);




        final TextView 오늘날짜 = (TextView) rootView.findViewById(R.id.txt_Today);
        오늘날짜.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
        final DatePickerDialog mDateSetlistener = new DatePickerDialog(rootView.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mYear = year; mMonth = month; mDay = dayOfMonth;
                오늘날짜.setText(String.format("%d-%d-%d", mYear, mMonth+1, mDay));
//                Shared_Studies.mYear = mYear;
//                Shared_Studies.mMonth = mMonth + 1;
//                Shared_Studies.mDay = mDay;
                Log.i("txt_Today", Integer.toString(mYear)+Integer.toString(mMonth)+Integer.toString(mDay)+"");
                setListItems();
            }
        },mYear,mMonth,mDay);
        calendar.set(mYear,mMonth,mDay);
        mDateSetlistener.getDatePicker().setMaxDate(calendar.getTimeInMillis());



        오늘날짜.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateSetlistener.show();

            }
        });




        //////////////////이미지뷰 인텐트로 사진지정하기
        iv_UserPhoto = (ImageView) rootView.findViewById(R.id.img_Myface);
        iv_UserPhoto.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialog, int which){
                       doTakephotoAction();
                   }
               };
               DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialog, int which){
                       doTakeAlbumAction();
                   }
               };
               DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialog, int which){
                       dialog.dismiss();
                   }
               };

               new AlertDialog.Builder(container.getContext())
                       .setTitle("업로드할 이미지 선택")
                       //.setPositiveButton("사진촬영", cameraListener)
                       .setNeutralButton("앨범선택", albumListener)
                       .setNegativeButton("취소",cancelListener)
                       .show();
           }
        });




        //사진 세팅하기
        context = rootView.getContext();
        SharedPreferences SP_user = context.getSharedPreferences(Shared_user.user,context.MODE_PRIVATE);

        Gson gson = new Gson();
        //주키 가져오기
        SharedPreferences shared_whoami = context.getSharedPreferences("shared_whoami", context.MODE_PRIVATE);
        String mainuser = shared_whoami.getString("whoami", "");
        //불러온 주키로 유저 클래스 부르기
        String json = SP_user.getString(mainuser, ""); //주키로 유저 스트링 부르기
        Shared_class shared_class = new Shared_class();
        final Shared_user shared_user = shared_class.fromuser(json);
        //firebase로 데이터 불러와서 넣어보기
//        ValueEventListener userListener = new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                firebaseuser = dataSnapshot.getValue(firebaseuser.class);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w("fragment_my", "loadPost:onCancelled", databaseError.toException());
//            }
//        };
//        reference.addValueEventListener(userListener);


//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                firebaseuser = dataSnapshot.getValue(firebaseuser.class);
//                name.setText(firebaseuser.getmName());
//                Log.i("fireuser", "fireuser data binding");
//
//                //String url = dataSnapshot.child("mUrl").getValue().toString();
//                if(!firebaseuser.getmUrl().equals("first")){
//                    Log.i("urlll",firebaseuser.getmUrl());
////                    String m = firebaseuser.getmUrl().replace("https://","");
////                    Uri a = Uri.parse(m);
////
////                    iv_UserPhoto.setImageURI(a);
////                    try {
////                        byte[] newurl1 = firebaseuser.getmUrl().getBytes();
////                        Bitmap b =
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
//
//                    URL newurl = null;
//                    try {
//                        newurl = new URL(firebaseuser.getmUrl());
//
//                        Glide.with(getContext()).load(newurl).into(iv_UserPhoto);
////                        HttpsURLConnection connection = (HttpsURLConnection) newurl.openConnection();
////                        connection.setDoInput(true);
////                        connection.connect();
////                        InputStream input = connection.getInputStream();
////                        Bitmap mIcon_val = BitmapFactory.decodeStream(input);
////                        iv_UserPhoto.setImageBitmap(mIcon_val);
//                        Log.i("urlll","제발");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.i("urlll","으젠장");
//                    }
//
//
//                }
//
//
//
//                //setImage(a, false);
//
////                FirebaseStorage storage = FirebaseStorage.getInstance();
////                StorageReference storageRef = storage.getReference();
////                final StorageReference userRef = storageRef.child("Users").child(firebaseuser.getmMail()+".jpg");
////                Glide.with(context)
////                        .load(userRef)
////                        .into(iv_UserPhoto);
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(),"firebase error", Toast.LENGTH_SHORT).show();
//            }
//        });
//

//        reference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                firebaseuser = dataSnapshot.getValue(firebaseuser.class);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                firebaseuser = dataSnapshot.getValue(firebaseuser.class);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                firebaseuser = dataSnapshot.getValue(firebaseuser.class);
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                firebaseuser = dataSnapshot.getValue(firebaseuser.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//
//        });



        //shared_user = gson.fromJson(json, Shared_user.class); //제이슨 클래스로 변경





//
//        try{
//            if(shared_user.getmUri() != null){
//                setImage(shared_user.getmUri(), false);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        if(!shared_user.getmmUri().equals("nodata")){
            URL newurl = null;
            try {
                newurl = new URL(shared_user.getmmUri());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Glide.with(Fragment_my.this).load(newurl).into(iv_UserPhoto);
        }



        name = rootView.findViewById(R.id.textView2);
        name.setText(shared_user.getmName());
        //name.setText(firebaseuser.getmName());


        Log.i("Fragment_my","onCreateView");
        return rootView;
    }

///////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("Fragment_my","온액티비티크래이트");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("Fragment_my","온스타트");
        try {
            setListItems();
            Log.i("Fragment_my","onStart");
        }catch (Exception e){
            e.printStackTrace();
        }
        MainActivity.수정가능 = true;
//        firebaseuser = firebaseAuth.getCurrentUser();
//        if(user != null){
//
//        }else{
//            signInAnonymously();
//        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("Fragment_my","온리줌");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Fragment_my","onCreate");
    }



    @Override
    public void onStop() {
        super.onStop();
        Log.i("Fragment_my","onStop");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Fragment_my","onDestroy");
    }



    ////////////////////////////////////////////////////////////////////
    ////////어뎁터 데이터 추가(임시)
    public void setListItems(){
        group_management.clear();
        GroupList.clear();
        ChildList.clear();

        //사용자 주키 생성후 반환
        SharedPreferences Shared_whoami = getContext().getSharedPreferences("shared_whoami", Context.MODE_PRIVATE);
        whoami = Shared_whoami.getString("whoami", "");

        //사용자의 스터디 그룹 목록인 parents 아이템 가져오기(fragment_group과 동일)
        SharedPreferences sh = getContext().getSharedPreferences(Shared_Group.Group, Context.MODE_PRIVATE);
        final SharedPreferences.Editor shedit = sh.edit();
        final Shared_Group groupList = new Shared_Group(sh);




        //firebase 저장
        docRefStudies = db.collection("Lets_study").document("Studies");
        docRefStudies.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult() != null){
                    Map<String, Object> allStudies = task.getResult().getData();
                    for(String key : allStudies.keySet()){
                        String newKey = key.replace("_",".");
                        shedit.putString(newKey, allStudies.get(key).toString());
                    }
                    shedit.apply();



                    //유저가 속한 데이터 그룹을 반환한다.
                    ArrayList<Shared_Group> 유저의소속그룹 = groupList.get유저가속한그룹(whoami);

                    for (int i = 0; i<유저의소속그룹.size(); i++){
                        //페런트 공간 확보하는 부분
                        group_management.add(new ArrayList<itemdata_mystudy_child>());
                    }
                    ChildList.addAll(group_management);


                    for (int i = 0; i < 유저의소속그룹.size(); i++){
                        //parent의 데이터 바인딩
                        GroupList.add(new itemdata_mystudy_parent(유저의소속그룹.get(i).getmGroupname()));
                        //parent에 바인딩되어있는 텍스트를 기준으로 해당 공부목록 파일을 조회위한 변수
                        String 소속그룹명 = 유저의소속그룹.get(i).getmGroupname();
                        //저장한 공부록록이 있는지 확인해보기
                        SharedPreferences Sh_Studies = getContext().getSharedPreferences(Shared_Studies.Studies, Context.MODE_PRIVATE);
                        Shared_Studies shared_studies = new Shared_Studies(Sh_Studies);
                        //유저의 메일과 그룹명을 가지고 List<Shared_Studies>를 반환한다.
                        ArrayList<Shared_Studies> 유저의공부목록 = shared_studies.get그룹의공부목록(whoami, 소속그룹명);

                        //차일드의 데이터 바인딩
                        for(int j = 0; j < 유저의공부목록.size(); j++){
                            itemdata_mystudy_child item = new itemdata_mystudy_child(1, 유저의공부목록.get(j).getmTime(), 유저의공부목록.get(j).getmTitle());

                            group_management.get(i).add(item);
                        }
                    }


                    adapter_mystudy.notifyDataSetChanged();

                }
            }
        });

    }

    public void 이전데이터복사(){

        //사용자 주키 생성후 반환
        SharedPreferences Shared_whoami = getContext().getSharedPreferences("shared_whoami", Context.MODE_PRIVATE);
        String whoami = Shared_whoami.getString("whoami", "");

        //사용자의 스터디 그룹 목록인 parents 아이템 가져오기(fragment_group과 동일)
        SharedPreferences sh = getContext().getSharedPreferences(Shared_Group.Group, Context.MODE_PRIVATE);
        Shared_Group groupList = new Shared_Group(sh);
        //유저가 속한 데이터 그룹을 반환한다.
        ArrayList<Shared_Group> 유저의소속그룹 = groupList.get유저가속한그룹(whoami);

        for (int i = 0; i<유저의소속그룹.size(); i++){
            //페런트 공간 확보하는 부분
            group_management.add(new ArrayList<itemdata_mystudy_child>());
        }
        ChildList.addAll(group_management);


        for (int i = 0; i < 유저의소속그룹.size(); i++){
            //parent의 데이터 바인딩
            GroupList.add(new itemdata_mystudy_parent(유저의소속그룹.get(i).getmGroupname()));
            //parent에 바인딩되어있는 텍스트를 기준으로 해당 공부목록 파일을 조회위한 변수
            String 소속그룹명 = 유저의소속그룹.get(i).getmGroupname();
            //저장한 공부록록이 있는지 확인해보기
            SharedPreferences Sh_Studies = getContext().getSharedPreferences(Shared_Studies.Studies, Context.MODE_PRIVATE);
            Shared_Studies shared_studies = new Shared_Studies(Sh_Studies);
            //유저의 메일과 그룹명을 가지고 List<Shared_Studies>를 반환한다.
            ArrayList<Shared_Studies> 유저의공부목록 = shared_studies.get그룹의공부목록(whoami, 소속그룹명);

            //차일드의 데이터 바인딩
            for(int j = 0; j < 유저의공부목록.size(); j++){
                itemdata_mystudy_child item = new itemdata_mystudy_child(1, 유저의공부목록.get(j).getmTime(), 유저의공부목록.get(j).getmTitle());

                group_management.get(i).add(item);
            }
        }


        adapter_mystudy.notifyDataSetChanged();
    }









    ////////어뎁터 데이터 추가(임시)

    //카메라에서 사진 촬영
    public void doTakephotoAction(){// 카메라 촬영후 이미지 가져오기
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //임시로 사용할 파일의 경로를 생성
        String uri = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), uri));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    //앨범에서 이미지 가져오기!
    public void doTakeAlbumAction(){ //앨범에서 이미지 가져오기
        //앨범호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return; //값이 안들어왔다면 리턴시켜줌
///////////////////////////////////////////////////////////// 삭제
        if(requestCode == 43){
            Toast.makeText(getContext(), "마이로 정보 들어옴", Toast.LENGTH_SHORT);
        }





        if (requestCode == PICK_FROM_ALBUM) {



            Uri photoUri = data.getData();
            //////////shared 정보가져오기
            SharedPreferences SP_user = context.getSharedPreferences(Shared_user.user,context.MODE_PRIVATE);
            Gson gson = new Gson();
            //주키 불러오기
            SharedPreferences shared_whoami = context.getSharedPreferences("shared_whoami", context.MODE_PRIVATE);
            String mainuser = shared_whoami.getString("whoami", "");


            SharedPreferences.Editor SP_user_Editor = SP_user.edit();
            String json = SP_user.getString(mainuser, "");
            Shared_user shared_user = gson.fromJson(json, Shared_user.class);
            shared_user.setmUri(photoUri);



            String newSP_user = gson.toJson(shared_user);
            SP_user_Editor.putString(mainuser,newSP_user).apply();


            //////////shared




            /////////////////////////////////////////////

//            Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(photoUri, "image/*");
//
//                //crop할 이미지를 200*200 크기로 저장
//                intent.putExtra("outputX", 200);//crop한 이미지의 x축 크기
//                intent.putExtra("outputY", 200);//crop한 이미지의 y축 크기
//                intent.putExtra("aspectX", 1); //crop박스 x축비율
//                intent.putExtra("aspectY", 1); //crop박스 y축비율
//                intent.putExtra("scale",true);
//                intent.putExtra("return-data",true);
//                startActivityForResult(intent, CROP_FROM_IMAGE);
//            //크롭이 된 이후 이미지를 넘겨 받음
//                //이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후 임시파일을 삭제함
//                if(requestCode != RESULT_OK){
//                    return;
//                }
//
//                final Bundle extras = data.getExtras();
//
//                //crop된 이미지를 저장히기 위한 file경로
//                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel/"+System.currentTimeMillis()+".jpg";
//
//
//                //임시파일삭제
//                tempFile = new File(photoUri.getPath());
////                if(tempFile.exists()){
////                    tempFile.delete();
////                }

            /////////////////////////////////////////////

            setImage(photoUri, true);

        }

        if (requestCode == PICK_FROM_CAMERA) {
            doTakephotoAction();
        }
//////////////////////////////////////////////////////  삭제
//        switch (requestCode){
//            case PICK_FROM_ALBUM:{
//                //이후의 처리가 카메라와 같으므로 일단 break없이 진행
//                //다른 방법을 찾아야 할필요가 있음
//                mImageCaptureUri = data.getData();
//                Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
//
//            }
//            break; // 여기는 앨범선택
//            case PICK_FROM_CAMERA:{ // 여기는 카메라로 찍기
//                //이미지를 가져온 이후 리사이즈할 이미지 크기 결정함
//                //이후에 이미지 크롭 어플을 호출하게 됨
//                Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(mImageCaptureUri, "image/*");
//
//                //crop할 이미지를 200*200 크기로 저장
//                intent.putExtra("outputX", 200);//crop한 이미지의 x축 크기
//                intent.putExtra("outputY", 200);//crop한 이미지의 y축 크기
//                intent.putExtra("aspectX", 1); //crop박스 x축비율
//                intent.putExtra("aspectY", 1); //crop박스 y축비율
//                intent.putExtra("scale",true);
//                intent.putExtra("return-data",true);
////                startActivityForResult(intent, CROP_FROM_IMAGE);//crop_from_camera case문 이동
//                break;
//            }
//            case CROP_FROM_IMAGE:{
//                //크롭이 된 이후 이미지를 넘겨 받음
//                //이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후 임시파일을 삭제함
//                if(requestCode != RESULT_OK){
//                    return;
//                }
//
//                final Bundle extras = data.getExtras();
//
//                //crop된 이미지를 저장히기 위한 file경로
//                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel/"+System.currentTimeMillis()+".jpg";
//
//                if(extras != null){
//                    Bitmap photo = extras.getParcelable("data"); //crop된 bitmap
//                    iv_UserPhoto.setImageBitmap(photo);//레이아웃의 이미지칸에 crop된 bitmap을 보여줌
//                    //storeCropImage(photo, filePath);//crop된 이미지를 외부저장소, 앨범에 저장한다.
//                    absoultePath = filePath;
//                    break;
//                }
//                //임시파일삭제
//                File f = new File(mImageCaptureUri.getPath());
//                if(f.exists()){
//                    f.delete();
//                }
//            }
//        }
    }
    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        try {
//            tempFile = createImageFile();
//        } catch (IOException e) {
//            Toast.makeText(getActivity(), "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//
//            e.printStackTrace();
//        }
//        if (tempFile != null) {

            Uri photoUri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        //}
    }



    //////////////////////////////////////////////////////////////////////
    private void setImage(Uri photoUri, boolean upload) {


        Cursor cursor = null;

        try {

            /*
             *  Uri 스키마를
             *  content:/// 에서 file:/// 로  변경한다.
             */
            String[] proj = { MediaStore.Images.Media.DATA };

            assert photoUri != null;
            cursor = getActivity().getContentResolver().query(photoUri, proj, null, null, null);

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
        originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);



        iv_UserPhoto.setImageBitmap(originalBm);

        if(upload){
            uploadImage(photoUri);
        }

    }


    public void uploadImage(Uri photoUri){
        //스토리지에 저장하기  //: if request.auth != null 이거 규칙에서 삭제함
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference userRef = storageRef.child("Users").child(whoami+".jpg");

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
//.putBytes(data)
        //스토리지 url 가져오기

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    final Uri downloadUri = task.getResult();

                    docRefUsers = db.collection("Lets_study").document("Users");
                    docRefUsers.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            //db를 불러오는게 성공했을시 맵에다가 user들의 정보를 넣는다.
                            Map<String, Object> document = task.getResult().getData();
                            String user = document.get(idpw.fireEmail).toString();
                            Shared_class shared_class = new Shared_class();
                            Shared_user shared_user = shared_class.fromuser(user);
                            shared_user.setmUri(downloadUri);
                            Map<String, Object> changeuser = new HashMap<>();
                            changeuser.put(idpw.fireEmail, shared_class.touser(shared_user));
                            docRefUsers.set(changeuser, SetOptions.merge());
                        }
                    });
//                    firebaseuser.setmUrl(downloadURL);
//                    reference.child("mUrl").setValue(downloadURL);
                    Log.e("firebase", "사용자 이미지 db업로드 성공");
                }else{
                    Log.e("firebase", "사용자 이미지 db업로드 실패");
                }
            }
        });

    }

    ///////////////////////////////////////////////////////////////////////////////////

    //사진올리기1_알아서 생겨나는 매서드
    @Override
    public void onClick(View v) {
        id_view = v.getId();
    }

//    private void storeCropImage(Bitmap bitmap, String filePath){
//        //SmartWheel 폴더를 생성하여 이미지를 저장하는 방식
//        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartWheel";
//        File directory_SmartWheel = new File(dirPath);
//
//        if(!directory_SmartWheel.exists()) //디렉토리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속함
//            directory_SmartWheel.mkdir();
//
//        File copyFile = new File(filePath);
//        BufferedOutputStream out = null;
//
//        try {
//            copyFile.createNewFile();
//            out = new BufferedOutputStream(new FileOutputStream(copyFile));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//
//            // sendBroadcast를 통해 crop된 사진을 앨범에 보이도록 갱신한다.
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
//            out.flush();
//            out.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }




    //박상권 삽질 블로그 참고
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        super.onDestroyView();
        Log.i("Fragment_my","onDestroyView");
    }







}
