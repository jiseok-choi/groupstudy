package com.example.mytest.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mytest.R;
import com.example.mytest.adapter.Adapter_chatList;
import com.example.mytest.adapteritems.itemdata_chatList;
import com.example.mytest.firebaseclass.ChatModel;
import com.example.mytest.firebaseclass.UserModel;
import com.example.mytest.layouts.talking;
import com.example.mytest.sharedclass.Shared_class;
import com.example.mytest.sharedclass.Shared_user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.example.mytest.layouts.MainActivity.iv_UserPhoto;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Frends extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    List<itemdata_chatList> dataList = new ArrayList<>();
    Adapter_chatList adapter_chatList;
    ChatRecyclerViewAdapter adapter;
    SharedPreferences sharedPreferences;

    public Fragment_Frends() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_fragment__frends, container, false);

        recyclerView = rootView.findViewById(R.id.recycle_chatlist);
        adapter = new ChatRecyclerViewAdapter(rootView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

//        ImageView imageView = rootView.findViewById(R.id.chatlist_Image);
//        recyclerView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(rootView.getContext(),"공부하자 클릭됨",Toast.LENGTH_SHORT);
//                Intent intent = new Intent(rootView.getContext(), talking.class);
//                startActivity(intent);
//            }
//        });



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        SharedPreferences sh_user, whoami;

        private List<ChatModel> chatModels = new ArrayList<>();
        private String uid;
        private ArrayList<String> destinationUsers = new ArrayList<>(); //대화 할 사람들의 데이터가 담기는 부분
        Context context;
        Shared_class shared_class;

        public ChatRecyclerViewAdapter(Context context) {//채팅목록 가져오기
            this.context = context;
            whoami = context.getSharedPreferences("shared_whoami", context.MODE_PRIVATE);
            String 사용자 = whoami.getString("whoami", "");
            sh_user = context.getSharedPreferences(Shared_user.user, context.MODE_PRIVATE);
            shared_class = new Shared_class();
            //Shared_user 사용자클래스 = shared_class.fromuser(사용자);

            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatModels.clear();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        chatModels.add(item.getValue(ChatModel.class));

                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {//받아왔으니 보여주는 놈
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chatlist, viewGroup, false);


            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

            final CustomViewHolder customViewHolder = (CustomViewHolder)viewHolder;
            String destinationUid = null;

            //챗방에 있는 유저를 체크 모두 다
            for(String user: chatModels.get(i).users.keySet()){
                if(!user.equals(uid)){
                    destinationUid = user;
                    destinationUsers.add(destinationUid);
                }
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel userModeld = dataSnapshot.getValue(UserModel.class);
                    customViewHolder.textView_title.setText(userModeld.userName);
                    sh_user = context.getSharedPreferences(Shared_user.user, context.MODE_PRIVATE);
                    String user = sh_user.getString(userModeld.mail, "");
                    Shared_user 사용자클래스 = shared_class.fromuser(user);
                    if(!사용자클래스.getmmUri().equals("nodata")){
                        URL newurl = null;
                        try {
                            newurl = new URL(사용자클래스.getmmUri());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        Glide.with(context).load(newurl).into(customViewHolder.imageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //메시지를 내림 차순으로 정렬 후 마지막 메세지릐 키값을 가져옴
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.<String>reverseOrder());
            commentMap.putAll(chatModels.get(i).comments);
            try {
                String lastMessageKey = (String) commentMap.keySet().toArray()[0];
                customViewHolder.textView_last_message.setText(chatModels.get(i).comments.get(lastMessageKey).message);
            }catch (Exception e){
                e.printStackTrace();
            }


            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), talking.class);
                    intent.putExtra("destinationUid", destinationUsers.get(i));

                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_title;
            public TextView textView_last_message;
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.chatlist_Image);
                textView_title = (TextView) itemView.findViewById(R.id.chatlist_name);
                textView_last_message = (TextView) itemView.findViewById(R.id.chatlist_text);
            }
        }
    }


}
