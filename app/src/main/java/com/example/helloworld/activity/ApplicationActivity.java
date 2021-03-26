package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.MyViewPagerAdapter;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.ConnectServer;
import com.example.helloworld.socket.SocketManager;
import com.example.helloworld.view.fragment_dialogMsg;
import com.example.helloworld.view.fragment_friend;
import com.example.helloworld.view.fragment_info;
import com.example.helloworld.view.fragment_my;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


public class ApplicationActivity extends MyAppCompatActivity implements fragment_my.testDataCallback {
    ViewPager viewPager;
    List<Fragment> fragmentList;
    fragment_dialogMsg fragment_dialogMsg;
    fragment_my fragment_my;
    fragment_friend fragment_friend;
    fragment_info fragment_info;
    MyViewPagerAdapter myViewPagerAdapter;
    TabLayout tabLayout;
    TextView titleBar;
    Handler handler;
    int[] dras = new int[]{R.drawable.chatselect,R.drawable.friednselected,R.drawable.infoselect,R.drawable.myselect};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ConnectServer(getApplicationContext()).start();
        setContentView(R.layout.activity_application);
        init();
    }

    public void init() {
        viewPager = findViewById(R.id.viewPage);
        tabLayout = findViewById(R.id.tabLayout);
        titleBar = findViewById(R.id.titleBar);
        fragmentList = new ArrayList<>();
        fragment_my = new fragment_my();
        fragment_dialogMsg = new fragment_dialogMsg();
        fragmentList.add(fragment_dialogMsg);
        fragment_info = new fragment_info();
        fragment_friend = new fragment_friend();
        fragmentList.add(fragment_friend);
        fragmentList.add(fragment_info);
        fragmentList.add(fragment_my);
        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setBackgroundColor(Color.parseColor("#36ffffff"));
        tabLayout.setupWithViewPager(viewPager);
        for(int i=0;i<4;i++){
            tabLayout.getTabAt(i).setIcon(dras[i]);
        }
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x10) {
                    new myThread().start();
                }
            }
        };
        Message message = Message.obtain();
        message.what = 0x10;
        handler.sendMessage(message);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断当点击的是返回键
        if (keyCode == event.KEYCODE_BACK) {
            exit();//退出方法
        }
        return true;
    }

    private long time = 0;

    //退出方法
    private void exit() {
//如果在两秒大于2秒
        if (System.currentTimeMillis() - time > 2000) {
//获得当前的时间
            time = System.currentTimeMillis();
            show_Toast("再点击一次退出应用程序");
        } else {
//点击在两秒以内
            removeALLActivity();//执行移除所以Activity方法
        }
    }

    @Override
    public void testData() {
        removeActivity();
    }

    public class myThread extends Thread {
        @Override
        public void run() {
            try {
                myThread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SocketManager.getSocketManager().sendUserId();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

}
