package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.R;
import com.example.helloworld.adapter.FriendStraggerAdapter;
import com.example.helloworld.adapter.GroupMenberStraggerAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.ui.MyDialogManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddGroupMenberActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<UserInfo> userInfoList;
    String name,group;
    TextView succeed;
    Handler handler;
    MyDialogManager myDialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_menber);
        Bundle bundle = getIntent().getExtras();
        userInfoList = (List<UserInfo>)bundle.getSerializable("userInfos");
        group = getIntent().getStringExtra("group");
        name = getIntent().getStringExtra("name");
        recyclerView = findViewById(R.id.group_menber);
        succeed = findViewById(R.id.succeed);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        if (userInfoList == null)
            userInfoList = new ArrayList<>();
        recyclerView.setAdapter(new GroupMenberStraggerAdapter(AddGroupMenberActivity.this, userInfoList,true));
        succeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Cookies.getCookies().getInts() != null)
                    if (!name.equals(""))
                        new addGroupChatThread().start();
                    else
                        if (group.equals("0"))
                            new createGroupChatThread().start();
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x16){
                    myDialogManager = new MyDialogManager(AddGroupMenberActivity.this,"添加成功",1);
                    myDialogManager.show();
                    new selectGroupChatThread().start();
                }
                if (msg.what == 0x17){
                    Intent intent = new Intent("updateGroupChat");
                    Intent intent2 = new Intent("updateMessage");
                    LocalBroadcastManager.getInstance(AddGroupMenberActivity.this).sendBroadcast(intent2);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfos", (Serializable) userInfoList);
                    intent.putExtras(bundle);
                    LocalBroadcastManager.getInstance(AddGroupMenberActivity.this).sendBroadcast(intent);
                    myDialogManager.dismiss();
                    finish();
                }
            }
        };
    }

    static public void open(Context context, List<UserInfo> userInfos, String name, String group) {
        Intent intent = new Intent(context, AddGroupMenberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfos", (Serializable) userInfos);
        intent.putExtras(bundle);
        intent.putExtra("name",name);
        intent.putExtra("group",group);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    public class addGroupChatThread extends Thread {
        @Override
        public void run() {
            String s = "";
            List<Integer> ints = Cookies.getCookies().getInts();
            for (int i = 0; i < ints.size(); i++) {
                s = s + ints.get(i) + "|";
            }
            HttpUtil.initJson().url(new httpPathList().getPath()[41]).addParam("group",group).addParam("userIds",s).method(HttpUtil.RequestMethod.POST).invoke();
            android.os.Message message = handler.obtainMessage();
            message.what = 0x16;
            handler.sendMessage(message);
        }
    }

    public class createGroupChatThread extends Thread {
        @Override
        public void run() {
            String s = Cookies.getCookies().getUserId() + "|";
            List<Integer> ints = Cookies.getCookies().getInts();
            for (int i = 0; i < ints.size(); i++) {
                s = s + ints.get(i) + "|";
            }
            HttpUtil.initJson().url(new httpPathList().getPath()[41]).addParam("group",group).addParam("userIds",s).method(HttpUtil.RequestMethod.POST).invoke();
            android.os.Message message = handler.obtainMessage();
            message.what = 0x16;
            handler.sendMessage(message);
        }
    }

    public class selectGroupChatThread extends Thread {
        @Override
        public void run() {
            String result = HttpUtil.initJson().url(new httpPathList().getPath()[42]).addParam("group",group).method(HttpUtil.RequestMethod.POST).invoke();
            Gson gson = new Gson();
            Type type = new TypeToken<List<UserInfo>>(){}.getType();
            userInfoList = gson.fromJson(result,type);
            android.os.Message message = handler.obtainMessage();
            message.what = 0x17;
            handler.sendMessage(message);
        }
    }

}
