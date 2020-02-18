package com.example.mytest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mytest.R;
import com.example.mytest.adapteritems.itemdata_mystudy_child;
import com.example.mytest.adapteritems.itemdata_mystudy_parent;
import com.example.mytest.layouts.studynow;
import com.example.mytest.layouts.timer;
import com.example.mytest.layouts.timer3;
import com.example.mytest.layouts.timer4;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.example.mytest.layouts.MainActivity.수정가능;

public class Adapter_mystudy extends BaseExpandableListAdapter {

    Context Maincontext;
    String Frendmail;

    public ArrayList<itemdata_mystudy_parent> parentItems;
    public ArrayList<ArrayList<itemdata_mystudy_child>> childItems;


    public Adapter_mystudy(Context context, String frendmail) {
        this.Maincontext = context;
        this.Frendmail = frendmail;
    }

    //각 리스트의 크기 반환
    @Override
    public int getGroupCount(){
        return parentItems.size();
    }//ParentList의 원소 개수를 반환

    @Override
    public int getChildrenCount(int groupPosition) {
        return childItems.get(groupPosition).size();
    }//childList의 크기를 int형으로 반환

    //리스트의 아이템 반환
    @Override
    public itemdata_mystudy_parent getGroup(int groupPosition) {//parent리스트의 position을 받아 해당 TextView에 반영될 String을 반환
        return parentItems.get(groupPosition);
    }

    @Override
    public itemdata_mystudy_child getChild(int groupPosition, int childPosition) {//groupPosition과 childPosition을 통해 childList의 원소를 얻어옴
        return childItems.get(groupPosition).get(childPosition);
    }

    //리스트 아이템의 id반환
   @Override
    public long getGroupId(int groupPosition) { return groupPosition; }// ParentList의 position을 받아 long값으로 반환


    @Override
    public long getChildId(int groupPosition, int childPosition) { return childPosition; }// ChildList의 ID로 long 형 값을 반환





    //동일한 id가 항상 동일한 개체를 참조하는지 여부를 반환
    @Override
    public boolean hasStableIds() {
        return true;
    }


    //리스트 각각의 row에 view를 설정
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        Context context = parent.getContext();

        //convertView가 비어있을 경우 xml 파일을 inflate 해줌
        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_studymy_group, parent, false);
        }

        //View들은 반드시 아이템 레이아웃을 inflate한 뒤에 작성할 것
        TextView Grouptitle = (TextView) v.findViewById(R.id.item_mystudy_grouptitle);
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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        final Context context = parent.getContext();

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_studymy_list, parent, false);
        }



        Button time = (Button) v.findViewById(R.id.item_time_btn);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(수정가능){
                    Intent intent = new Intent(context, timer4.class);

                    intent.putExtra("공부명",getChild(groupPosition, childPosition).getText());
                    ((Activity)Maincontext).startActivityForResult(intent, 55);
                }

            }
        });

        Button text = (Button) v.findViewById(R.id.item_list_btn);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, studynow.class);
                if(!Frendmail.equals("nodata")){
                    intent.putExtra("친구메일",Frendmail);
                }
                intent.putExtra("공부명", getChild(groupPosition, childPosition).getText());
                Maincontext.startActivity(intent);

            }
        });
//        text.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//
//                return false;
//            }
//        });

        time.setText(getChild(groupPosition, childPosition).getTime());
        text.setText(getChild(groupPosition, childPosition).getText());


        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    } //특정 group의 child를 선택할 것인지 정하는 것 false면 선택되지 않음

    //아래는 새로운요소를 추가하거나 삭제하기 위한 임의의 함수이다.
    public void addItem(int groupPosition, itemdata_mystudy_child item){
        childItems.get(groupPosition).add(item);
    }
    public void removeChild(int groupPosition, int childPosition){
        childItems.get(groupPosition).remove(childPosition);
    }


}
