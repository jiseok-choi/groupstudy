package com.example.mytest.adapteritems;

public class itemdata_mystudy_parent {

    public static final int TITLE = 0;

    //public int type;
    public String group;



    //컨스트럭터 만들기

    public itemdata_mystudy_parent(String group) {
        //this.type = type;
        this.group = group;
    }

    public String getGroup(){
        return this.group;
    }

}
