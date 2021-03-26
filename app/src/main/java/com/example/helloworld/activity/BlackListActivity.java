package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import com.example.helloworld.R;
import com.example.helloworld.adapter.BlackListAdapter;
import com.example.helloworld.adapter.FriendStraggerAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.AlterDialogManager;
import com.example.helloworld.ui.MyDialogManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class BlackListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    BlackListAdapter blackListAdapter;
    List<UserInfo> userInfoList;
    Handler handler;
    MyBroadcastReceive myBroadcastReceive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter("updateBlackLsit");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(myBroadcastReceive,intentFilter);
        init();
    }

    public void init(){
        recyclerView = findViewById(R.id.bl_rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0x11){
                    blackListAdapter = new BlackListAdapter(getApplicationContext(),userInfoList);
                    recyclerView.setAdapter(blackListAdapter);
                }
            }
        };
        new MyThread().start();
    }

    public class MyThread extends Thread {

        @Override
        public void run() {
            String result = HttpUtil.initJson()
                    .url(new httpPathList().getPath()[31])
                    .addParam("hostId", Integer.toString(Cookies.getCookies().getUserId()))
                    .method(HttpUtil.RequestMethod.POST)
                    .invoke();
            Gson gson = new Gson();
            Type type = new TypeToken<List<UserInfo>>() {
            }.getType();
            List<UserInfo> userInfos = gson.fromJson(result,type);
            if (userInfos != null) {
                userInfoList = userInfos;
                android.os.Message message = handler.obtainMessage();
                message.what = 0x11;
                handler.sendMessage(message);
            }
        }
    }

    public class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new MyThread().start();
        }
    }

}
