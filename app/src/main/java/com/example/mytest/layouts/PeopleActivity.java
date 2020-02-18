package com.example.mytest.layouts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mytest.Progress;
import com.example.mytest.R;
import com.example.mytest.firebaseclass.UserModel;
import com.example.mytest.sharedclass.Shared_class;
import com.example.mytest.sharedclass.Shared_user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SharedPreferences sh_user;
    SharedPreferences sh_사용자;
    String 사용자;
    ArrayList<String> frendlist;
    Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        //유저 쉐어드에서 사용자 클래스 불러오기
        sh_user = getSharedPreferences(Shared_user.user, MODE_PRIVATE);
        sh_사용자 = getSharedPreferences("shared_whoami", MODE_PRIVATE);
        사용자 = sh_사용자.getString("whoami", "");
        String fromjson =  sh_user.getString(사용자, "");

        //사용자 클래스에서 친구 목록 가져오기
        Shared_class shared_class = new Shared_class();
        Shared_user userclass = shared_class.fromuser(fromjson);
        frendlist = userclass.getmFrands();

        //리사이클러뷰 적용
        recyclerView = findViewById(R.id.people_recyclerview);
        recyclerView.setAdapter(new PeopleRecyclerViewAdapter());
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        progress = new Progress();
        progress.showProgressDialog(this);

    }

    class PeopleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<UserModel> userModels;
        //db를 검색할 생성자
        public PeopleRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModels.clear();


                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if(frendlist.contains(userModel.mail)){
                            userModels.add(userModel);
                        }
                    }
                    notifyDataSetChanged();
                    progress.closeProgressDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(PeopleActivity.this).inflate(R.layout.item_people, viewGroup, false);
            //RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.people_recyclerview);
            //recyclerView.setLayoutManager(new LinearLayoutManager(getLayoutInflater().getContext()));
            //recyclerView.setAdapter(new PeopleRecyclerViewAdapter());

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            //Glide.with(viewHolder.itemView.getContext()).load(userMo)
            ((CustomViewHolder)viewHolder).textView.setText(userModels.get(i).userName);
            ((CustomViewHolder)viewHolder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), talking.class);
                    intent.putExtra("destinationUid", userModels.get(i).uid);
                    startActivity(intent);
                }
            });
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder{
            public ImageView imageView;
            public TextView textView;
            public Button button;

            public CustomViewHolder(View view){
                super(view);
                imageView = (ImageView) view.findViewById(R.id.people_imageview);
                textView = (TextView) view.findViewById(R.id.people_textview);
                button = (Button) view.findViewById(R.id.People_chatbtn);
            }
        }
    }

}
