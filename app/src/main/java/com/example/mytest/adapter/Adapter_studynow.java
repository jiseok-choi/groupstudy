package com.example.mytest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mytest.Fragment.Fragment_my;
import com.example.mytest.R;
import com.example.mytest.adapteritems.studynowItem;
import com.example.mytest.layouts.idpw;
import com.example.mytest.layouts.studynewText;
import com.example.mytest.layouts.studynewTitle;
import com.example.mytest.layouts.studynow;
import com.example.mytest.sharedclass.Shared_Studies;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mytest.layouts.MainActivity.수정가능;

public class Adapter_studynow extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    int REQUEST_CHANGE = 4;
    View view;

    String inputText;

    //Shared_Studies shared_studies;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefStudies;

//    public Adapter_studynow(Context context) {
//        this.context = context;
//}

    private final ArrayList<studynowItem> mDataList;
    // 외부에서 에서 만들어준 클래스(adapteritem)를 리스트 형태로 받아주겠다
    public Adapter_studynow(ArrayList<studynowItem> dataList, Context context){
        mDataList = dataList; //받은만큼 getItemCount늘려줄수 있게하자
        this.context = context;
    }

    //첫번째로 만들어야 하는 내부 클래스(뷰홀더)
    public class TitleViewHolder extends RecyclerView.ViewHolder{

        //내가 만든 아이템에 들어있는 텍스트나 이미지를 변수로 선언해주자
        private TextView Title;
        private Button itembtntitle;


        public TitleViewHolder(@NonNull final View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.item_title);
            itembtntitle = itemView.findViewById(R.id.item_title_btn);



        }
    }
    public class TextViewHolder extends RecyclerView.ViewHolder{

        //내가 만든 아이템에 들어있는 텍스트나 이미지를 변수로 선언해주자
        private TextView Text;
        private Button itembtntext;

        public TextViewHolder(@NonNull final View itemView) {
            super(itemView);

            Text = itemView.findViewById(R.id.item_text);

            itembtntext = itemView.findViewById(R.id.item_text_btn);


        }
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder{

        //내가 만든 아이템에 들어있는 텍스트나 이미지를 변수로 선언해주자
        private ImageView ImageView;
        private Button itembtnimage;

        public ImageViewHolder(@NonNull final View itemView) {
            super(itemView);

            ImageView = itemView.findViewById(R.id.item_picture);

            itembtnimage = itemView.findViewById(R.id.item_picture_btn);

        }
    }

    //뷰타입 가져오기
//    @Override
//    public int getItemViewType(int position){
//        return super.getItemViewType(position);
//    }


    @Override
    public int getItemViewType(int position){
        switch (mDataList.get(position).mType) {
            case 0:
                return studynowItem.TITLE_TYPE;
            case 1:
                return studynowItem.TEXT_TYPE;
            case 2:
                return studynowItem.EMAGE_TYPE;
            default:
                return -1;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {//뷰홀더를 만드는 부분

        switch (viewType){
            case studynowItem.TITLE_TYPE:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_studynow_title, viewGroup,false);
                return new TitleViewHolder(view);
            case studynowItem.TEXT_TYPE:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_studynow_text, viewGroup,false);
                return new TextViewHolder(view);
            case studynowItem.EMAGE_TYPE:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_studynow_picture, viewGroup,false);
                return new ImageViewHolder(view);
        }

        return null;

    }

    //외부에서 데이터를 받을수 있게 컨스트럭터를 만들어줄거임
   @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder Holder, final int position) {// 위에서 리턴한 뷰홀더를 바인딩 해주는 부분
       studynowItem item = mDataList.get(position); //아이템


       switch (item.mType){
           case studynowItem.TITLE_TYPE:
               ((TitleViewHolder)Holder).Title.setText(item.getTitle()); //받아온 홀더에 내가만든 타이틀변수를 세팅해주자

               //아이템의 버튼 클릭했을때 다이얼로그로 수정아님 삭제를 해보자
               ((TitleViewHolder)Holder).itembtntitle.setOnClickListener(new View.OnClickListener(){
                   @Override
                   public void onClick(View v) {
                       context = v.getContext();


                       AlertDialog.Builder dialog = new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                       String choice[] = new String[2];
                       choice[0] = "수정";
                       choice[1] = "삭제";
                       dialog.setTitle("작업을 선택하세요");
                       dialog.setItems(choice, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               if(which == 0){//수정 눌렀을때
                                   Handler handler = new Handler();
                                   final Runnable r = new Runnable() {
                                       @Override
                                       public void run() {
                                           String txt = mDataList.get(position).getTitle();
                                           Intent intent = new Intent(context, studynewTitle.class);
                                           intent.putExtra("수정번호",position);
                                           intent.putExtra("수정내용",txt);
                                           ((Activity)context).startActivityForResult(intent, REQUEST_CHANGE);

                                       }
                                   };
                                   handler.post(r);

                               }else if(which == 1){//삭제 눌렀을때
                                   Handler handler = new Handler();
                                   final Runnable r = new Runnable() {
                                       @Override
                                       public void run() {
                                           try {
                                               mDataList.remove(position);
                                               sharedstudies(mDataList);
                                               notifyItemRemoved(position);
                                           }catch (Exception e){
                                               e.printStackTrace();
                                           }
                                       }
                                   };
                                   handler.post(r);
                               }
                           }
                       });
                       dialog.show();


                       //Toast.makeText(context, position+1+"번째 아이템", Toast.LENGTH_SHORT).show();
                   }
               });
               break;

           case studynowItem.TEXT_TYPE: //텍스트 타입이면
               ((TextViewHolder)Holder).Text.setText(item.getTxt()); //받아온 홀더에 내가만든 타이틀변수를 세팅해주자
               ((TextViewHolder)Holder).itembtntext.setOnClickListener(new View.OnClickListener(){
                   @Override
                   public void onClick(View v) {
                       context = v.getContext(); //뷰의 정보를 넣어놓고

                       if(수정가능){
                       AlertDialog.Builder dialog = new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert); //다이얼로그를 띄워서 보여주기
                       String choice[] = new String[2];
                       choice[0] = "수정";
                       choice[1] = "삭제";
                       dialog.setTitle("작업을 선택하세요");
                       dialog.setItems(choice, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               if(which == 0){                          //수정 눌렀을때
                                   Handler handler = new Handler();     //작업하는건 핸들러로 타 액티비티와 관계없이 진행하기 위해 핸들러 사용
                                   final Runnable r = new Runnable() {
                                       @Override
                                       public void run() {
                                           String txt = mDataList.get(position).getTxt();
                                           Intent intent = new Intent(context, studynewText.class);
                                           intent.putExtra("수정번호",position);
                                           intent.putExtra("수정내용",txt);
                                           ((Activity)context).startActivityForResult(intent, REQUEST_CHANGE);
                                       }
                                   };
                                   handler.post(r);

                               }else if(which == 1){//삭제 눌렀을때
                                   Handler handler = new Handler();
                                   final Runnable r = new Runnable() {
                                       @Override
                                       public void run() {
                                           try {
                                               mDataList.remove(position);
                                               sharedstudies(mDataList);
                                               notifyItemRemoved(position);
                                           }catch (Exception e){
                                               e.printStackTrace();
                                           }

                                       }
                                   };
                                   handler.post(r);
                               }
                           }
                       });
                       dialog.show();
                       }

                       //Toast.makeText(context, position+1+"번째 아이템", Toast.LENGTH_SHORT).show();
                   }
               });
            break;

           case studynowItem.EMAGE_TYPE:
               //((ImageViewHolder)Holder).ImageView.setImageBitmap(item.getImage(context)); //받아온 홀더에 내가만든 타이틀변수를 세팅해주자
               try {
                   URL newurl = new URL(item.getmUri());
                   Glide.with(context).load(newurl).into(((ImageViewHolder)Holder).ImageView);
                   Log.i("uploadImage","Adapter 정상 작동");
               } catch (MalformedURLException e) {
                   e.printStackTrace();
                   Log.e("uploadImage","Adapter 비정상 작동");
               }




               //아이템의 버튼 클릭했을때 다이얼로그로 수정아님 삭제를 해보자
               ((ImageViewHolder)Holder).itembtnimage.setOnClickListener(new View.OnClickListener(){
                   @Override
                   public void onClick(View v) {
                       context = v.getContext();


                       AlertDialog.Builder dialog = new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                       String choice[] = new String[2];
                       choice[0] = "수정";
                       choice[1] = "삭제";
                       dialog.setTitle("작업을 선택하세요");
                       dialog.setItems(choice, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               if(which == 0){//수정 눌렀을때
                                   Handler handler = new Handler();
                                   final Runnable r = new Runnable() {
                                       @Override
                                       public void run() {
                                           Intent intent = new Intent(Intent.ACTION_PICK);
                                           //intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                                           intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                           intent.putExtra("whatisthis",3);
                                           intent.putExtra("itemposition",position);
                                           studynow.popopopo = position;
                                           Log.e("포지션값 확인",position+"" );
                                           ((Activity)context).startActivityForResult(intent, REQUEST_CHANGE);




//                                         String txt = mDataList.get(position).getTitle();
//                                         Intent intent = new Intent(context, studynewTitle.class);
//                                         intent.putExtra("수정번호",position);
//                                         intent.putExtra("수정내용",txt);
//                                         ((Activity)context).startActivityForResult(intent, REQUEST_CHANGE);

                                       }
                                   };
                                   handler.post(r);


                               }else if(which == 1){//삭제 눌렀을때
                                   Handler handler = new Handler();
                                   final Runnable r = new Runnable() {
                                       @Override
                                       public void run() {
                                           try {
                                               mDataList.remove(position);
                                               sharedstudies(mDataList);
                                               notifyItemRemoved(position);
                                           }catch (Exception e){
                                               e.printStackTrace();
                                           }
                                       }
                                   };
                                   handler.post(r);
                               }
                           }
                       });
                       dialog.show();


                       //Toast.makeText(context, position+1+"번째 아이템", Toast.LENGTH_SHORT).show();
                   }
               });
               break;

       }

/*


*/



    }
    //쉐어드 메서드 만들기
    public void sharedstudies(ArrayList<studynowItem> dataList ) {
        SharedPreferences Shared_whoami = context.getSharedPreferences("shared_whoami", context.MODE_PRIVATE);
        final String whoami = Shared_whoami.getString("whoami", "");

        //final String date = Integer.toString(Fragment_my.mYear) + Integer.toString(Fragment_my.mMonth) + Integer.toString(Fragment_my.mDay);
//
        SharedPreferences Sh_Studies = context.getSharedPreferences(Shared_Studies.Studies, context.MODE_PRIVATE);
        final String date = Integer.toString(Fragment_my.mYear) + Integer.toString(Fragment_my.mMonth) + Integer.toString(Fragment_my.mDay);

        String json = Sh_Studies.getString(whoami + studynow.공부명 + date, "");
        Gson gson;//= new Gson();
        //shared_studies = gson.fromJson(json, Shared_Studies.class);


        studynow.shared_studies.setmStudynowItem(dataList);
        gson = new Gson();
        final String json1 = gson.toJson(studynow.shared_studies);
        final SharedPreferences.Editor editor = Sh_Studies.edit();

        docRefStudies = db.collection("Lets_study").document("Studies");
        Map<String, Object> changestudies = new HashMap<>();
        changestudies.put(idpw.fireEmail+studynow.공부명+date, json1);
        docRefStudies.set(changestudies, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                editor.putString(whoami+studynow.공부명+date, json1);
                editor.apply();
                Log.i("uploadImage","db에 정상 저장");
            }
        });



    }




    @Override
    public int getItemCount() {//어뎁터가 가지고 있는 아이템의 개수를 지정해주는 것
        return mDataList.size();
    }



}
