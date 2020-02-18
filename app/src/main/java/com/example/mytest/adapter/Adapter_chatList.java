package com.example.mytest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.R;
import com.example.mytest.adapteritems.itemdata_chatList;
import com.example.mytest.layouts.talking;

import java.util.List;

public class Adapter_chatList extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    Context context;


    private final List<itemdata_chatList> mDataList;
    public Adapter_chatList(List<itemdata_chatList> mDataList, Context context){
        this.mDataList = mDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_chatlist, viewGroup, false);


        return new chatListViewHolder(view);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, talking.class);
        ((Activity)context).startActivity(intent);
        Toast.makeText(v.getContext(),"클릭클릭",Toast.LENGTH_SHORT);
    }


    public class chatListViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView itemName;
        private TextView itemText;
        private TextView itemday;

        public chatListViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.chatlist_Image);
            itemName = itemView.findViewById(R.id.chatlist_name);
            itemText = itemView.findViewById(R.id.chatlist_text);
            itemday = itemView.findViewById(R.id.chatlist_day);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        itemdata_chatList item = mDataList.get(position);

        ((chatListViewHolder)viewHolder).itemName.setText(item.getName());
        ((chatListViewHolder)viewHolder).itemText.setText(item.getTxt());
        ((chatListViewHolder)viewHolder).itemday.setText(item.getDay());

        ((chatListViewHolder)viewHolder).itemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, talking.class);
                ((Activity)context).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

}
