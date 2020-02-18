package com.example.mytest.layouts;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.BackPressCloseHandler;
import com.example.mytest.Fragment.Fragment_my;
import com.example.mytest.R;
import com.example.mytest.newFrends;
import com.example.mytest.new_groupstudy;
import com.example.mytest.new_mystudy;
import com.example.mytest.adapter.pageradapter;
import com.example.mytest.sharedclass.Shared_Group;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    PagerAdapter adapter;
    private TabLayout tabLayout; // 탭레이아웃 변수
    private ViewPager viewPager; // 뷰페이저 변수
    private int tabcount = 0;
    private BackPressCloseHandler backPressCloseHandler;
    //프래그먼트 찾기
    FragmentManager manager;
    public static ImageView iv_UserPhoto;
    //플로팅액션버튼 관련 객체들
    FloatingActionButton fab;
    FloatingActionButton fab_frends, fab_chat;
    Animation FabOpen, FabClose, FabRClockwise, FabRanticlockwise;
    TextView fab_친구추가, fab_채팅추가;
    boolean isFabOpen = false;

    public static boolean 수정가능;

    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

    }









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        수정가능 = true;
        ////////////////0507 talking test

//        TextView imageView = (TextView) findViewById(R.id.);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this ,"공부하자 클릭됨",Toast.LENGTH_SHORT);
//                Intent intent = new Intent(MainActivity.this, talking.class);
//                startActivity(intent);
//            }
//        });
        ////////////////0507 talking test


        //권한주기
        setContentView(R.layout.fragment_fragment_my);
        tedPermission();


        setContentView(R.layout.activity_main);
        backPressCloseHandler = new BackPressCloseHandler(this); //뒤로가기 두번 누르면 해당 액티비티 종료

        //Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar2);


        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        viewPager = (ViewPager)findViewById(R.id.viewPager);

        //탭페이저어댑터 생성 (이놈으로 액티비티에 프래그먼트 화면정보를 전달해주는 것임)
        pageradapter pagerAdapter = new pageradapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        //페이지체인지 리스너 설정 (
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {




            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //뷰페이저에 탭의 포지션에 해당하는 레이아웃 설정
                viewPager.setCurrentItem(tab.getPosition());
                //Toast.makeText(MainActivity.this, "페이지 출력"+ tab.getPosition(), Toast.LENGTH_SHORT).show();
                //탭위치에 따라 플로팅 액션버튼 나타내기
                //FloatingActionButton 눌렀을 때 페이지에 따른 기능다르게 하기
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingplus);
                if(tab.getPosition()==2){
                    tabcount = tab.getPosition();
                    fab.show();
                }
                else if(tab.getPosition() == 1){
                    fab.show();
                    tabcount = tab.getPosition();
                    if(isFabOpen){
                        fab_frends.startAnimation(FabClose);
                        fab_chat.startAnimation(FabClose);
                        fab_친구추가.startAnimation(FabClose);
                        fab_채팅추가.startAnimation(FabClose);
                        fab.startAnimation(FabRanticlockwise);
                        fab_frends.setClickable(false);
                        fab_chat.setClickable(false);
                        isFabOpen = false;}
                }
                else{
                    fab.show();
                    tabcount = tab.getPosition();
                    if(isFabOpen){
                        fab_frends.startAnimation(FabClose);
                        fab_chat.startAnimation(FabClose);
                        fab_친구추가.startAnimation(FabClose);
                        fab_채팅추가.startAnimation(FabClose);
                        fab.startAnimation(FabRanticlockwise);
                        fab_frends.setClickable(false);
                        fab_chat.setClickable(false);
                        isFabOpen = false;}
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        ViewPager viewPager = findViewById(R.id.viewPager);
//        adapter = new pageradapter(getSupportFragmentManager());
//        //뷰 페이저와 연관된 페이저 어댑터를 화면에 출력한다.
//        viewPager.setAdapter(adapter);



        //플로팅 액션버튼 애니메이션으로 표현하기
        fab = (FloatingActionButton) findViewById(R.id.floatingplus);
        fab_frends = findViewById(R.id.floatingfrend);
        fab_chat = findViewById(R.id.floatingchat);
        fab_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PeopleActivity.class);
                startActivity(intent);
            }
        });
        fab_친구추가 = findViewById(R.id.fab_친구추가);
        fab_채팅추가 = findViewById(R.id.fab_채팅추가);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        FabRanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        //fab.hide();
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "클릭", Toast.LENGTH_SHORT).show();
                if(tabcount == 0){
                    //향후 추가 예정
                    Intent intent = new Intent(MainActivity.this, new_groupstudy.class);
                    startActivity(intent);
                }else if(tabcount == 1){
                    Intent intent = new Intent(MainActivity.this, new_mystudy.class);
                    startActivityForResult(intent, 43);
                }else if(tabcount == 2){
                    if(isFabOpen){
                        fab_frends.startAnimation(FabClose);
                        fab_chat.startAnimation(FabClose);
                        fab_친구추가.startAnimation(FabClose);
                        fab_채팅추가.startAnimation(FabClose);
                        fab.startAnimation(FabRanticlockwise);
                        fab_frends.setClickable(false);
                        fab_chat.setClickable(false);

                        isFabOpen = false;

                    }else{
                        fab_frends.startAnimation(FabOpen);
                        fab_chat.startAnimation(FabOpen);
                        fab_친구추가.startAnimation(FabOpen);
                        fab_채팅추가.startAnimation(FabOpen);
                        fab.startAnimation(FabRClockwise);
                        fab_frends.setClickable(true);
                        fab_chat.setClickable(true);
                        isFabOpen = true;
                    }
                }
            }
        });

        fab_frends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "frend클릭됨",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, newFrends.class);
                startActivity(intent);
            }
        });

    }

    @Override public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 43 && resultCode == RESULT_OK){
            Log.i("MainActivity","onActivityResult");
        }

        if(requestCode == 56 && resultCode == RESULT_OK){
            Log.i("MainActivity", "onActivityResult(new group");





        }

    }

    public void onFragmentChanged(int index){
        if(index == 1){
            //getSupportFragmentManager().beginTransaction().replace(R.id.)
        }
    }



}
