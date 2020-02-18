package com.example.mytest.layouts;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mytest.R;
import com.example.mytest.adapter.Adapter_talking;
import com.example.mytest.adapteritems.itemdata_talking;
import com.example.mytest.firebaseclass.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class talking extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<itemdata_talking> dataList = new ArrayList<>();
    Adapter_talking adapter_talking;
    Button btn;
    EditText Edittext;

    private String destinationUid;

    private String uid;
    private String chatRoomUid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talking);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //채팅을 거는 아이디
        destinationUid = getIntent().getStringExtra("destinationUid"); //채팅을 당하는 아이디


        //리사이클러뷰 사용하기
        recyclerView = findViewById(R.id.recycle_talking);
        //리사이클러뷰 메니저만들기(listview와 다르게 필수)
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);//리스트뷰에 레이아웃 매니저를 셋팅해준다.




        //어댑터 설정
//        adapter_talking = new Adapter_talking(dataList, this);
//        recyclerView.setAdapter(adapter_talking);

        Edittext = (EditText) findViewById(R.id.talking_editText);

        btn = (Button) findViewById(R.id.talking_sendbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);


                if(chatRoomUid == null){
                    btn.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });

                }else{
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = Edittext.getText().toString();
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Edittext.setText("");
                        }
                    });

                }
                //채팅방 생성되게하기 위한 코드




                ////////////////////////////////////////
                String txt;

                txt = Edittext.getText().toString();

                if(Edittext.getText().length() > 0){
//                    dataList.add(new itemdata_talking(0, txt, null));
//                    adapter_talking.notifyDataSetChanged();
                    //Edittext.setText(null);
                }

            }
        });
        checkChatRoom();


    }
    void checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel chatModel = item.getValue(ChatModel.class); //데이터베이스에서 채팅정보를 받아와 아이디가 존재하는지 판단하는것
                    if(chatModel.users.containsKey(destinationUid)){
                        chatRoomUid = item.getKey();//방아이디
                        btn.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(talking.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel.Comment> comments;
        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            //FirebaseDatabase.getInstance().getReference().child("users")
            getMessageList();
        }
        void getMessageList(){
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();

                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    //메시지를 갱신시켜줌
                    notifyDataSetChanged();
                    recyclerView.scrollToPosition(comments.size() -1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view;
//            if(comments.get(i).uid.equals(uid)){
//
//            }else{
//                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_talking_you, viewGroup, false);
//            }

            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_talking_me, viewGroup, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder)viewHolder);
            //MessageViewHolder2 messageViewHolder2 = ((MessageViewHolder2)viewHolder);

            //messageViewHolder.textView_message.setText(comments.get(i).message);
            if(comments.get(i).uid.equals(uid)){
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.textView_message.setText(comments.get(i).message);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
            }else{
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(i).message);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
            }

        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public LinearLayout linearLayout_main;
            public MessageViewHolder(View view) {

                super(view);
                textView_message = (TextView) view.findViewById(R.id.talkingme_msg);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_main);
            }
        }
        private class MessageViewHolder2 extends RecyclerView.ViewHolder {
            public TextView textView_message;


            public MessageViewHolder2(View view) {

                super(view);
                textView_message = (TextView) view.findViewById(R.id.talkingyou_msg);

            }
        }
    }
}
