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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.HistoryMsgAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.MyCalendarDialogManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class HistoryMsgActivity extends MyAppCompatActivity {

    UserInfo userInfo;
    UserInfo myUserInfo;
    TextView date,owner,type,alert,my_menber;
    EditText search;
    Button bt_search;
    Handler handler;
    List<MessageInfo> messageInfoList;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    HistoryMsgAdapter historyMsgAdapter;
    MyBroadcastForHisMsgReceive myBroadcastForHisMsgReceive;
    String search_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_msg);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        myUserInfo = Cookies.getCookies().getUserInfo();
        IntentFilter intentFilter = new IntentFilter("hisMsgPos");
        myBroadcastForHisMsgReceive = new MyBroadcastForHisMsgReceive();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(myBroadcastForHisMsgReceive,intentFilter);
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
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        handler = new Handler(){;
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    Bundle bundle = msg.getData();
                    if (bundle.getString("result").equals("succeed")){
                        historyMsgAdapter = new HistoryMsgAdapter(messageInfoList,getApplicationContext(),userInfo);
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
                    new MyCalendarDialogManager(HistoryMsgActivity.this, new MyCalendarDialogManager.OnClick() {
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
                    Intent intent = new Intent(HistoryMsgActivity.this,PhotoActivity.class);
                    intent.putExtra("option",false);
                    intent.putExtra("userInfo",userInfo);
                    startActivity(intent);
                    break;
                case R.id.menber:
                    new myThread(null,false).start();
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
                                .url(new httpPathList().getPath()[20])
                                .addParam("send", Integer.toString(userInfo.getUserId()))
                                .addParam("receive", Integer.toString(myUserInfo.getUserId()))
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
                if (messageInfoList == null)
                    bundle.putString("result", "defect");
                else
                    bundle.putString("result", "succeed");
                message.setData(bundle);
                handler.sendMessage(message);
            } else
                if (aBoolean != null){
                String send = null;
                String receive = null;
                if (aBoolean == true) {
                    receive = Integer.toString(userInfo.getUserId());
                    send = Integer.toString(myUserInfo.getUserId());
                } else if (aBoolean == false) {
                    receive = Integer.toString(myUserInfo.getUserId());
                    send = Integer.toString(userInfo.getUserId());
                }
                String result =
                        HttpUtil.initJson()
                                .url(new httpPathList().getPath()[22])
                                .addParam("send", send)
                                .addParam("receive", receive)
                                .addParam("owner", Integer.toString(myUserInfo.getUserId()))
                                .method(HttpUtil.RequestMethod.POST)
                                .invoke();
                Message message = new Message();
                message.what = 0x11;
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<MessageInfo>>() {
                }.getType();
                messageInfoList = gson.fromJson(result, listType);
                if (messageInfoList == null)
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
                                    .url(new httpPathList().getPath()[23])
                                    .addParam("send", Integer.toString(userInfo.getUserId()))
                                    .addParam("receive", Integer.toString(myUserInfo.getUserId()))
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
                    if (messageInfoList == null)
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

}
