package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.R;
import com.example.helloworld.adapter.GroupMenberAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.data.historyMessage;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.AlterDialogManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class GroupChatSettingActivity extends MyAppCompatActivity {

    RecyclerView recyclerView;
    LinearLayout ly_group_chat_name,ly_search_chat,ly_delete_chat,ly_delete_group,ly_add_group;
    TextView other_title,group_chat_name;
    List<UserInfo> userInfoList;
    List<UserInfo> msgUserInfos;
    String group_name;
    String group;
    int hostUserId;
    Handler handler;
    MyBroadcastReceive myBroadcastReceive;
    GroupMenberAdapter groupMenberAdapter;
    MyBroadcastChatNameReceive myBroadcastChatNameReceive;
    MyBroadcastForHisMsgReceive myBroadcastForHisMsgReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_setting);
        myBroadcastChatNameReceive = new MyBroadcastChatNameReceive();
        IntentFilter intentFilter1 = new IntentFilter("updateGroupChatName");
        LocalBroadcastManager.getInstance(GroupChatSettingActivity.this).registerReceiver(myBroadcastChatNameReceive,intentFilter1);
        myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter("updateGroupChat");
        LocalBroadcastManager.getInstance(GroupChatSettingActivity.this).registerReceiver(myBroadcastReceive,intentFilter);
        IntentFilter intentFilter2 = new IntentFilter("hisMsgPos");
        myBroadcastForHisMsgReceive = new MyBroadcastForHisMsgReceive();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(myBroadcastForHisMsgReceive,intentFilter2);
        Bundle bundle = getIntent().getExtras();
        userInfoList = (List<UserInfo>)bundle.getSerializable("userInfos");
        msgUserInfos = (List<UserInfo>)bundle.getSerializable("userInfoList");
        group = getIntent().getStringExtra("group");
        group_name = getIntent().getStringExtra("name");
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    new historyMessageThread().start();
                }
                if (msg.what == 0x12){
                    Toast.makeText(GroupChatSettingActivity.this,"删除成功!",Toast.LENGTH_LONG);
                    Intent intent = new Intent("deleteMessageGroup");
                    intent.putExtra("message","delMsg");
                    LocalBroadcastManager.getInstance(GroupChatSettingActivity.this).sendBroadcast(intent);
                }
                if (msg.what == 0x13){
                    Toast.makeText(GroupChatSettingActivity.this,"退出成功!",Toast.LENGTH_LONG);
                    Intent intent = new Intent("deleteMessageGroup");
                    intent.putExtra("message","delGroup");
                    LocalBroadcastManager.getInstance(GroupChatSettingActivity.this).sendBroadcast(intent);
                    finish();
                }
            }
        };
        init();
    }

    public void init(){
        hostUserId = Cookies.getCookies().getUserId();
        ly_delete_chat = findViewById(R.id.ly_delete_chat);
        ly_group_chat_name = findViewById(R.id.ly_group_chat_name);
        ly_search_chat = findViewById(R.id.ly_search_chat);
        ly_delete_group = findViewById(R.id.ly_delete_group);
        ly_add_group = findViewById(R.id.ly_add_group);
        other_title = findViewById(R.id.other_title);
        recyclerView = findViewById(R.id.group_menber);
        group_chat_name = findViewById(R.id.group_chat_name);
        other_title.setText("聊天信息（"+userInfoList.size()+")");
        group_chat_name.setText(group_name);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(5,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        groupMenberAdapter = new GroupMenberAdapter(userInfoList,GroupChatSettingActivity.this);
        recyclerView.setAdapter(groupMenberAdapter);
        setOnclick();
    }

    static public void open(Context context, List<UserInfo> userInfos, String name, String group, List<UserInfo> userInfoList) {
        Intent intent = new Intent(context, GroupChatSettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfos", (Serializable) userInfos);
        bundle.putSerializable("userInfoList", (Serializable) userInfoList);
        intent.putExtras(bundle);
        intent.putExtra("name",name);
        intent.putExtra("group",group);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    public void setOnclick(){
        ly_delete_chat.setOnClickListener(new onClick());
        ly_group_chat_name.setOnClickListener(new onClick());
        ly_search_chat.setOnClickListener(new onClick());
        ly_delete_group.setOnClickListener(new onClick());
        ly_add_group.setOnClickListener(new onClick());
    }

    public class onClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ly_delete_group:
                    new AlterDialogManager(GroupChatSettingActivity.this, "是否要清空聊天记录？", new AlterDialogManager.onClockListner() {
                        @Override
                        public void onClockListner(int i) {
                            if (i == 0){
                                new MyDeleteHisMsgThread().start();
                            }
                        }
                    }).show();
                    break;
                case R.id.ly_group_chat_name:
                    ChangeGroupNameActivity.open(GroupChatSettingActivity.this,group_name,group);
                    break;
                case R.id.ly_search_chat:
                    HistoryMsgGroupActivity.open(GroupChatSettingActivity.this,userInfoList,group_name,group,msgUserInfos);
                    break;
                case R.id.ly_delete_chat:
                    new AlterDialogManager(GroupChatSettingActivity.this, "删除并退出后，将不再接收此群聊信息", new AlterDialogManager.onClockListner() {
                        @Override
                        public void onClockListner(int i) {
                            if (i == 0){
                                new MyDeleteGroupChatThread().start();
                            }
                        }
                    }).show();
                    break;
                case R.id.ly_add_group:
                    AddGroupMenberActivity.open(GroupChatSettingActivity.this,userInfoList,group_name,group);
                    break;
            }
        }
    }

    public int width(){
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    public class MyDeleteHisMsgThread extends Thread {

        @Override
        public void run() {
            String result = HttpUtil.initJson()
                    .url(new httpPathList().getPath()[40])
                    .addParam("userId", Integer.toString(hostUserId))
                    .addParam("group", group)
                    .method(HttpUtil.RequestMethod.POST)
                    .invoke();
            if (result != null)
                if (result.equals("succeed")) {
                    Message message = handler.obtainMessage();
                    message.what = 0x11;
                    handler.sendMessage(message);
                }
        }
    }

    public class MyDeleteGroupChatThread extends Thread {

        @Override
        public void run() {
            String result = HttpUtil.initJson()
                    .url(new httpPathList().getPath()[45])
                    .addParam("userId", Integer.toString(hostUserId))
                    .addParam("group", group)
                    .method(HttpUtil.RequestMethod.POST)
                    .invoke();
            if (result != null)
                if (result.equals("succeed")) {
                    Message message = handler.obtainMessage();
                    message.what = 0x13;
                    handler.sendMessage(message);
                }
        }
    }

        public class historyMessageThread extends Thread {
            @Override
            public void run() {
                String path = new httpPathList().getPath()[36];
                Type type = new TypeToken<List<MessageInfo>>() {
                }.getType();
                String historyString = HttpUtil.initJson().url(path).method(HttpUtil.RequestMethod.POST).addParam("group",group).addParam("userId", Integer.toString(hostUserId)).invoke();
                if (historyString != null) {
                    List<MessageInfo> messageInfos = null;
                    Gson gson = new Gson();
                    messageInfos = gson.fromJson(historyString, type);
                    historyMessage.getHistoryMessage().setMessageInfos(messageInfos);
                } else {
                    historyMessage.getHistoryMessage().setMessageInfos(null);
                }
                Message message = handler.obtainMessage();
                message.what = 0x12;
                handler.sendMessage(message);
            }
        }

    public class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            userInfoList = (List<UserInfo>)bundle.getSerializable("userInfos");
            groupMenberAdapter = new GroupMenberAdapter(userInfoList,GroupChatSettingActivity.this);
            recyclerView.setAdapter(groupMenberAdapter);
        }
    }

    public class MyBroadcastChatNameReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            group_chat_name.setText(intent.getStringExtra("name"));
        }
    }

    public class     MyBroadcastForHisMsgReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myBroadcastReceive != null)
            LocalBroadcastManager.getInstance(GroupChatSettingActivity.this).unregisterReceiver(myBroadcastReceive);
    }
}
