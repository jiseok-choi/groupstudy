package com.example.mytest.layouts;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mytest.R;

public class groupstudy extends AppCompatActivity{ //} implements SensorEventListener {

//            private SensorManager sensorManager;
//    private Sensor gyroSensor;
//    int rotation;
//    TextView tvXaxis, tvYaxis, tvZaxis, tvRotation;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_3groupstudy);
// 
//
//
//        tvXaxis = (TextView)findViewById(R.id.tvXaxis);
//        tvYaxis = (TextView)findViewById(R.id.tvYaxis);
//        tvZaxis = (TextView)findViewById(R.id.tvZaxis);
//        tvRotation = (TextView)findViewById(R.id.tvRotation);
// 
//        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
// 
//    }
// 
//            void getSurfaceRotation() {
//        String windowsService = Context.WINDOW_SERVICE;
//        WindowManager windowManager = (WindowManager)getSystemService(windowsService);
//        Display display = windowManager.getDefaultDisplay();
//        rotation = display.getRotation();
// 
//        int xAxis = SensorManager.AXIS_X;
//        int yAxis = SensorManager.AXIS_Y;
//        int zAxis = SensorManager.AXIS_Z;
// 
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                tvRotation.setText("Rotation : 0");
//                break;
//            case Surface.ROTATION_90:
//                xAxis = SensorManager.AXIS_Y;
//                yAxis = SensorManager.AXIS_MINUS_X;
//                tvRotation.setText("Rotation : 90");
//                break;
//            case Surface.ROTATION_180:
//                yAxis = SensorManager.AXIS_MINUS_Y;
//                tvRotation.setText("Rotation : 180");
//                break;
//            case Surface.ROTATION_270:
//                xAxis = SensorManager.AXIS_MINUS_Y;
//                yAxis = SensorManager.AXIS_X;
//                tvRotation.setText("Rotation : 270");
//                break;
//            default:
//                break;
//        }
//        tvXaxis.setText("X axis : " + String.valueOf(xAxis));
//        tvYaxis.setText("Y axis : " + String.valueOf(yAxis));
//        tvZaxis.setText("Z axis : " + String.valueOf(zAxis));
//    }
// 
//            @Override
//    protected void onResume() {
//        super.onResume();
//        sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
//    }
// 
//            @Override
//    public void onSensorChanged(SensorEvent event) {
//        if(event.sensor ==gyroSensor) {
//            getSurfaceRotation();
//        }
//    }
// 
//            @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// 
//    }
}