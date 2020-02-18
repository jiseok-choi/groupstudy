package com.example.mytest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mytest.Fragment.Fragment_Frends;
import com.example.mytest.Fragment.Fragment_group;
import com.example.mytest.Fragment.Fragment_my;

public class pageradapter extends FragmentPagerAdapter {
    //private ArrayList<Fragment> mData;
    //탭의 개수를 저장해줄 변수를 선언해줌
    private int tabcount;
    //탭페이저 어댑터 함수
    public pageradapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.tabcount = NumOfTabs;
        //추가
//        mData = new ArrayList<>();
//        mData.add(new Fragment_group());
//        mData.add(new Fragment_my());
//        mData.add(new Fragment_Frends());
    }


    @Override
    public Fragment getItem(int i) { //
        //return mData.get(i);
        switch (i) {
            case 0:
                Fragment_group tab1 = new Fragment_group();
                return tab1;
            case 1:
                Fragment_my tab2 = new Fragment_my();
                return tab2;
            case 2:
                Fragment_Frends tab3 = new Fragment_Frends();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabcount;
    }
}
