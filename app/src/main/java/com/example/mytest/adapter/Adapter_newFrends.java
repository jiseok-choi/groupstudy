package com.example.mytest.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.R;
import com.example.mytest.adapteritems.itemdata_frends;
import com.example.mytest.layouts.idpw;
import com.example.mytest.sharedclass.Shared_class;
import com.example.mytest.sharedclass.Shared_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_newFrends extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<itemdata_frends> mDataList;
    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefUsers;


    public Adapter_newFrends(ArrayList<itemdata_frends> dataList, Context context){
        this.mDataList = dataList;
        this.context = context;
    }

    public class newfrendsViewHolder extends RecyclerView.ViewHolder{
        private TextView mail, name;
        private Button accept;

        public newfrendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mail = itemView.findViewById(R.id.newfrendmail);
            name = itemView.findViewById(R.id.newfrendname);
            accept = itemView.findViewById(R.id.btn_myfrend);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_newfrends, viewGroup, false);
        return new newfrendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        itemdata_frends item = mDataList.get(position);

        ((newfrendsViewHolder)viewHolder).mail.setText(item.mail);
        final String newmail = item.mail;
        ((newfrendsViewHolder)viewHolder).name.setText(item.name);
        final Button btnaccept = ((newfrendsViewHolder)viewHolder).accept;
        ((newfrendsViewHolder)viewHolder).accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //수락버튼을 누르면 사용자(나의) 쉐어드에 친구리스트 추가하기

                SharedPreferences shared_whoami = v.getContext().getSharedPreferences("shared_whoami",Context.MODE_PRIVATE);
                final String whoami = shared_whoami.getString("whoami","");
                Gson gson = new Gson();

                SharedPreferences shared_user = v.getContext().getSharedPreferences(Shared_user.user, Context.MODE_PRIVATE);
                final SharedPreferences.Editor whoamiedit = shared_user.edit();

                String fromjson = shared_user.getString(whoami, "");
                Shared_user user = gson.fromJson(fromjson, Shared_user.class);
                ArrayList<String> 친구요청목록 = user.getmAreyoufrend();
                ArrayList<String> 수락친구목록 = user.getmFrands();
                //추가한 친구 목록에서 제거
                친구요청목록.remove(newmail);
                수락친구목록.add(newmail);

                user.setmAreyoufrend(친구요청목록);
                user.setmFrands(수락친구목록);

                final String tojson = gson.toJson(user);


                //db에 저장
                docRefUsers = db.collection("Lets_study").document("Users");
                Map<String, Object> changeme = new HashMap<>();
                changeme.put(idpw.fireEmail, tojson);
                docRefUsers.set(changeme, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "친구추가 했습니다.", Toast.LENGTH_SHORT).show();
                            whoamiedit.putString(whoami, tojson);
                            whoamiedit.apply();
                        }else{
                            Toast.makeText(context, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //수락버튼을 누르면 친구(너의) 쉐어드에 친구리스트 추가하기

                String fromjson2 = shared_user.getString(newmail, "");
                Shared_user user2 = gson.fromJson(fromjson2, Shared_user.class);
                ArrayList<String> 수락친구목록2 = user2.getmFrands();
                //추가한 친구 목록에서 제거
                수락친구목록2.add(whoami);

                user2.setmFrands(수락친구목록2);

                final String tojson2 = gson.toJson(user2);
                Map<String, Object> changeyou = new HashMap<>();
                String you = newmail.replace(".","_");
                changeyou.put(you, tojson2);
                docRefUsers.set(changeyou, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            whoamiedit.putString(newmail, tojson2);
                            whoamiedit.apply();
                        }else{
                            Toast.makeText(context, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });




                //완료
                Toast.makeText(v.getContext(), "수락했습니다", Toast.LENGTH_SHORT).show();
                btnaccept.setVisibility(v.INVISIBLE);

                btnaccept.setClickable(false);
                }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }




}
