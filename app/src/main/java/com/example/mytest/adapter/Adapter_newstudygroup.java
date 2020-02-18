package com.example.mytest.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.mytest.R;
import com.example.mytest.adapteritems.itemdata_frends;

import java.util.ArrayList;

public class Adapter_newstudygroup extends RecyclerView.Adapter<Adapter_newstudygroup.itemViewHolder>{



    ArrayList<itemdata_frends> mDataList;
    ArrayList<String> partyone;

    public Adapter_newstudygroup(ArrayList<itemdata_frends> dataList) {
        mDataList = dataList;
    }

    //뷰홀더 객체는 뷰를 담아두는 역할을 하면서 동시에 뷰에 표시될 데이터를 설정하는 역할을 맡을 수 있습니다.
    public class itemViewHolder extends RecyclerView.ViewHolder{

        TextView mail, name;
        CheckBox checkBox;
        public itemViewHolder(@NonNull View itemView) {
            super(itemView);
            mail = itemView.findViewById(R.id.txt_mail);
            name = itemView.findViewById(R.id.txt_name);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_newstudygroup, viewGroup, false);
        return new itemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder itemViewHolder, final int position) {
        final itemdata_frends[] item = {mDataList.get(position)};

        itemViewHolder.mail.setText(item[0].mail);
        final String mMail;
        itemViewHolder.name.setText(item[0].name);
        itemViewHolder.checkBox.setChecked(item[0].incheck);
        itemViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox)v;
                mDataList.get(position).setIncheck(cb.isChecked());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public ArrayList<itemdata_frends> getmDataList(){
        return mDataList;
    }
}
