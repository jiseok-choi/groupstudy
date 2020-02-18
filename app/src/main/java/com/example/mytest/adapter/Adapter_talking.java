package com.example.mytest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.R;
import com.example.mytest.adapteritems.itemdata_talking;
import com.example.mytest.adapteritems.studynowItem;
import com.example.mytest.layouts.talking;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Adapter_talking extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    View view;
    SimpleDateFormat format;

    private final List<itemdata_talking> mDataList;


    public Adapter_talking(List<itemdata_talking> mDataList, Context context) {
        this.mDataList = mDataList;
        this.context = context;
    }

    ////////////////////////////////////////////////////0507 클릭이벤트 테스트
//    @Override
//    public void onClick(View v) {
//        Intent intent = new Intent(context, talking.class);
//        ((Activity)context).startActivity(intent);
//        Toast.makeText(context,"클릭클릭",Toast.LENGTH_SHORT);
//    }
    ////////////////////////////////////////////////////0507 클릭이벤트 테스트
    @Override
    public int getItemViewType(int position){
        switch (mDataList.get(position).Type) {
            case 0:
                return itemdata_talking.TALKING_ME;
            case 1:
                return itemdata_talking.TALKING_YOU;
            default:
                return -1;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType){
            case itemdata_talking.TALKING_ME:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_talking_me, viewGroup, false);
            return new MytalkViewHolder(view);

            case itemdata_talking.TALKING_YOU:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_talking_you, viewGroup, false);
            return new YourtalkViewHolder(view);
        }
        return null;
    }

    public class MytalkViewHolder extends RecyclerView.ViewHolder{
        private TextView mytextView, mydayView;


        public MytalkViewHolder(@NonNull View itemView) {
            super(itemView);

            mytextView = itemView.findViewById(R.id.talkingme_msg);
            //mydayView = itemView.findViewById(R.id.talkingme_time);
        }
    }
    public class YourtalkViewHolder extends RecyclerView.ViewHolder{
        private TextView yourtextView, yourdayView;

        public YourtalkViewHolder(@NonNull View itemView) {
            super(itemView);

            yourtextView = itemView.findViewById(R.id.talkingyou_msg);
            yourdayView = itemView.findViewById(R.id.talkingyou_time);
        }
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        itemdata_talking item = mDataList.get(position); //아이템

        switch(item.Type){
            case itemdata_talking.TALKING_ME:
                ((MytalkViewHolder)viewHolder).mytextView.setText(item.getText());
                format = new SimpleDateFormat("hh:mm");
                int 시 = Calendar.HOUR_OF_DAY;
                int 분 = Calendar.MINUTE;
                ((MytalkViewHolder)viewHolder).mydayView.setText(시+":"+분);
                break;
            case itemdata_talking.TALKING_YOU:
                ((YourtalkViewHolder)viewHolder).yourtextView.setText(item.getText());
                format = new SimpleDateFormat("hh:mm");
                int 너시 = Calendar.HOUR_OF_DAY;
                int 너분 = Calendar.MINUTE;
                ((YourtalkViewHolder)viewHolder).yourdayView.setText(너시+":"+너분);
                break;
        }


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
