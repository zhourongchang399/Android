package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.helloworld.R;
import com.example.helloworld.adapter.PhotoAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    GridLayoutManager gridLayoutManager;
    PhotoAdapter photoAdapter;
    RecyclerView recyclerView;
    List<MessageInfo> messageInfos;
    UserInfo userInfo;
    Handler handler;
    Boolean option;
    String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        if (!getIntent().getBooleanExtra("option",false))
            userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        else
            group = getIntent().getStringExtra("group");
        recyclerView = findViewById(R.id.photo_content);
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    Bundle bundle = msg.getData();
                    if (!bundle.getString("result").equals("defect")) {
                        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
                        photoAdapter = new PhotoAdapter(messageInfos, getApplicationContext(),width / 4);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setAdapter(photoAdapter);
                    }
                }
            }
        };
        new MyThread().start();
    }


    public class MyThread extends Thread {
        @Override
        public void run() {
            String result;
            if (group != null) {
                result =
                        HttpUtil.initJson()
                                .url(new httpPathList().getPath()[48])
                                .addParam("group", group)
                                .addParam("userId", Integer.toString(Cookies.getCookies().getUserId()))
                                .method(HttpUtil.RequestMethod.POST)
                                .invoke();
            }
            else {
                result =
                        HttpUtil.initJson()
                                .url(new httpPathList().getPath()[21])
                                .addParam("send", Integer.toString(userInfo.getUserId()))
                                .addParam("receive", Integer.toString(Cookies.getCookies().getUserId()))
                                .addParam("type", null)
                                .method(HttpUtil.RequestMethod.POST)
                                .invoke();
            }
            Message message = new Message();
            message.what = 0x11;
            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<MessageInfo>>() {
            }.getType();
            messageInfos = gson.fromJson(result, listType);
            if (messageInfos == null)
                bundle.putString("result", "defect");
            else
                bundle.putString("result", "succeed");
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }

}
