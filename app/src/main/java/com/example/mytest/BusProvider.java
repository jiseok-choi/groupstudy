package com.example.mytest;

import com.squareup.otto.Bus;

public final class BusProvider { //프래그먼트 이미지 띄우기 사용하기 위한 class
    private static  final Bus BUS = new Bus();

    public static Bus getInstance(){
        return BUS;
    }
    private BusProvider(){

    }
}
