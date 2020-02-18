package com.example.mytest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.Fragment.Fragment_Frends;
import com.example.mytest.Fragment.Fragment_my;
import com.example.mytest.R;
import com.example.mytest.adapteritems.itemdata_studygroup_child;
import com.example.mytest.adapteritems.itemdata_studygroup_parent;
import com.example.mytest.layouts.FrendsActivity;
import com.example.mytest.layouts.MainActivity;

import java.util.ArrayList;

import static com.example.mytest.layouts.MainActivity.수정가능;

public class Adapter_studygroup extends BaseExpandableListAdapter {

    Context rootcontext;
    MainActivity mainActivity;

    public ArrayList<itemdata_studygroup_parent> parentItems;
    public ArrayList<ArrayList<itemdata_studygroup_child>> childItems;

    public Adapter_studygroup(Activity activity, Context context) {
        mainActivity = (MainActivity)activity;
        rootcontext = context;
    }

    @Override
    public int getGroupCount() {
        return parentItems.size();
    }//ParentList의 원소 개수를 반환

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItems.get(groupPosition).size();
    }//childList의 크기를 int형으로 반환

    @Override
    public itemdata_studygroup_parent getGroup(int groupPosition) {
        return parentItems.get(groupPosition);
    }

    @Override
    public itemdata_studygroup_child getChild(int groupPosition, int childPosition) {
        return childItems.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        Context context = parent.getContext();


        //convertView 가 비어있을 경우 xml 파일을 inflate 해줌
        if(v== null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_studygroup_group, parent, false);
        }

        //View 들은 반드시 아이템 레이아웃을 inflate한 뒤에 작성할 것
        TextView Grouptitle = (TextView) v.findViewById(R.id.item_studygroup_grouptitle);
        ImageView imageView = (ImageView) v.findViewById(R.id.arrowdown);

        if(isExpanded) {
            imageView.setImageResource(R.drawable.uparrow);
        }else{
            imageView.setImageResource(R.drawable.arrowdown);
        }


        Grouptitle.setText(getGroup(groupPosition).getGroup());


        return v;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, final View convertView, final ViewGroup parent) {
        View v = convertView;
        final Context context = parent.getContext();

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_studygroup_text, parent, false);
        }

        Button text = (Button) v.findViewById(R.id.item_user_btn);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("adapter_studygroup","자식버튼 클릭됨");


                SharedPreferences sh_사용자 = context.getSharedPreferences("shared_whoami", Context.MODE_PRIVATE);
                String 사용자 = sh_사용자.getString("whoami", "");
                if(getChild(groupPosition, childPosition).getmMail().equals(사용자)){
                    Log.i("adapter_자식", "조건맞음");
                    수정가능 = true;
                    mainActivity.getSupportFragmentManager();
                }else{
                    Intent intent = new Intent(context, FrendsActivity.class);
                    intent.putExtra("frendMail", getChild(groupPosition, childPosition).getmMail());
                    context.startActivity(intent);
                    수정가능 = false;
                }

            }
        });

        text.setText(getChild(groupPosition, childPosition).getTime()+" "+getChild(groupPosition, childPosition).getmName());

        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
