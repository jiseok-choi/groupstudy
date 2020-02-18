package com.example.mytest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class timerservice extends Service {
    private final IBinder mBinder = new LocalBinder();
    private  int number;

    class LocalBinder extends Binder{
        timerservice getService(){
            return timerservice.this;
        }
    }




    public timerservice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
