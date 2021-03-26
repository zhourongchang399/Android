package com.example.helloworld.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.HistoryMsgAdapter;
import com.example.helloworld.adapter.HistoryMsgGroupAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.MyCalendarDialogManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class HistoryMsgGroupActivity extends MyAppCompatActivity {

    UserInfo userInfo;
    UserInfo myUserInfo;
    TextView date,owner,type,alert,my_menber;
    EditText search;
    Button bt_search;
    Handler handler;
    List<MessageInfo> messageInfoList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    HistoryMsgGroupAdapter historyMsgAdapter;
    MyBroadcastForHisMsgReceive myBroadcastForHisMsgReceive;
    MyBroadcastHisMsgReceive myBroadcastHisMsgReceive;
    String search_date;
    List<UserInfo> userInfoList;
    List<UserInfo> msgUserInfos;
    String group;
    String name;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_msg);
        myUserInfo = Cookies.getCookies().getUserInfo();
        IntentFilter intentFilter = new IntentFilter("hisMsgPos");
        myBroadcastForHisMsgReceive = new MyBroadcastForHisMsgReceive();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(myBroadcastForHisMsgReceive,intentFilter);
        IntentFilter intentFilter1 = new IntentFilter("searchMenber");
        myBroadcastHisMsgReceive = new MyBroadcastHisMsgReceive();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(myBroadcastHisMsgReceive,intentFilter1);
        init();
    }

    public void init(){
        date = findViewById(R.id.date);
        search = findViewById(R.id.search);
        type = findViewById(R.id.photo);
        owner = findViewById(R.id.menber);
        bt_search = findViewById(R.id.bt_search);
        alert = findViewById(R.id.alert);
        my_menber = findViewById(R.id.mymenber);
        recyclerView = findViewById(R.id.search_content);
        Bundle bundle = getIntent().getExtras();
        userInfoList = (List<UserInfo>)bundle.getSerializable("userInfos");
        msgUserInfos = (List<UserInfo>)bundle.getSerializable("userInfoList");
        group = getIntent().getStringExtra("group");
        name = getIntent().getStringExtra("name");
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        handler = new Handler(){;
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    Bundle bundle = msg.getData();
                    if (bundle.getString("result").equals("succeed")){
                        historyMsgAdapter = new HistoryMsgGroupAdapter(messageInfoList,getApplicationContext(),msgUserInfos);
                        recyclerView.setAdapter(historyMsgAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        alert.setVisibility(View.GONE);
                    }
                    else {
                        if (recyclerView != null)
                            recyclerView.setVisibility(View.GONE);
                        alert.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        setOnClick();
    }

    static public void open(Context context, List<UserInfo> userInfos,String name,String group, List<UserInfo> userInfoList) {
        Intent intent = new Intent(context,HistoryMsgGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfos", (Serializable) userInfos);
        bundle.putSerializable("userInfoList", (Serializable) userInfoList);
        intent.putExtras(bundle);
        intent.putExtra("name",name);
        intent.putExtra("group",group);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    public void setOnClick(){
        bt_search.setOnClickListener(new onClick());
        date.setOnClickListener(new onClick());
        owner.setOnClickListener(new onClick());
        type.setOnClickListener(new onClick());
        my_menber.setOnClickListener(new onClick());
    }

    public class onClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.date:
                    new MyCalendarDialogManager(HistoryMsgGroupActivity.this, new MyCalendarDialogManager.OnClick() {
                        @Override
                        public void onClickCalendar(int year, int month, int dayOfMonth) {
                            search_date = Integer.toString(year)+"-"+Integer.toString(month+1)+"-"+Integer.toString(dayOfMonth);
                            new myThread(null,null).start();
                        }
                    }).show();
                    break;
                case R.id.mymenber:
                    new myThread(null,true).start();
                    break;
                case R.id.photo:
                    Intent intent = new Intent(HistoryMsgGroupActivity.this,PhotoActivity.class);
                    intent.putExtra("group",group);
                    intent.putExtra("option",true);
                    startActivity(intent);
                    break;
                case R.id.menber:
                    SearchByMenberActivity.open(HistoryMsgGroupActivity.this,userInfoList,name,group);
                    break;
                case R.id.bt_search:
                    String content = search.getText().toString();
                    if (!content.equals("") && content != null)
                        new myThread(content,null).start();
                    break;
            }
        }
    }

    public class myThread extends Thread {

        String search;
        Boolean aBoolean;

        public myThread(String search,Boolean aBoolean) {
            this.search = search;
            this.aBoolean = aBoolean;
        }

        @Override
        public void run() {
            if (search != null) {
                String result =
                        HttpUtil.initJson()
                                .url(new httpPathList().getPath()[49])
                                .addParam("group", group)
                                .addParam("userId", Integer.toString(myUserInfo.getUserId()))
                                .addParam("search", search)
                                .method(HttpUtil.RequestMethod.POST)
                                .invoke();
                Message message = new Message();
                message.what = 0x11;
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MessageInfo>>() {
                }.getType();
                messageInfoList = gson.fromJson(result, listType);
                if (messageInfoList == null || result == null)
                    bundle.putString("result", "defect");
                else
                    bundle.putString("result", "succeed");
                message.setData(bundle);
                handler.sendMessage(message);
            } else
                if (aBoolean != null){
                String send = null;
                String receive = group;
                String owner = Integer.toString(Cookies.getCookies().getUserId());
                if (aBoolean == true) {
                    send = Integer.toString(myUserInfo.getUserId());
                } else if (aBoolean == false) {
                    send = userId;
                }
                String result =
                        HttpUtil.initJson()
                                .url(new httpPathList().getPath()[46])
                                .addParam("group", receive)
                                .addParam("userId", send)
                                .addParam("owner",owner)
                                .method(HttpUtil.RequestMethod.POST)
                                .invoke();
                Message message = new Message();
                message.what = 0x11;
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MessageInfo>>() {
                }.getType();
                    System.out.println(result);
                messageInfoList = gson.fromJson(result, listType);
                if (messageInfoList == null || result == null)
                    bundle.putString("result", "defect");
                else
                    bundle.putString("result", "succeed");
                message.setData(bundle);
                handler.sendMessage(message);
            }
                else
                if (aBoolean == null) {
                    String result =
                            HttpUtil.initJson()
                                    .url(new httpPathList().getPath()[47])
                                    .addParam("group", group)
                                    .addParam("userId", Integer.toString(Cookies.getCookies().getUserId()))
                                    .addParam("date", search_date)
                                    .method(HttpUtil.RequestMethod.POST)
                                    .invoke();
                    Message message = new Message();
                    message.what = 0x11;
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<MessageInfo>>() {
                    }.getType();
                    messageInfoList = gson.fromJson(result, listType);
                    if (messageInfoList == null || result == null)
                        bundle.putString("result", "defect");
                    else
                        bundle.putString("result", "succeed");
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
        }
    }


    public class MyBroadcastForHisMsgReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    public class MyBroadcastHisMsgReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            userId = intent.getStringExtra("userId");
            new myThread(null,false).start();
        }
    }

}
