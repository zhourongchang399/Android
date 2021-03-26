package com.example.helloworld.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.User;
import com.example.helloworld.data.UserAccount;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.AddFriendDialogManager;
import com.example.helloworld.ui.AlterDialogManager;
import com.example.helloworld.ui.MyDialogManager;
import com.example.helloworld.utils.ImageUtils;
import com.example.helloworld.view.selfInfoDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class FriendCardActivity extends MyAppCompatActivity {
    ImageView userFace;
    TextView username;
    Button deleteFriend,chatNow,addFriend,bt_addBlackList;
    TextView city,age,sex,myself,name;
    Bundle bundle;
    String my_username,my_city,my_myself,my_sex,my_name;
    Handler handler;
    int my_age;
    UserInfo userInfo;
    boolean isSelf;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        init();
        setOnClickListeren();
        handler = new MyHandler();
    }

    private void init() {
        Intent intent = getIntent();
        if (intent.getStringExtra("ifSelf").equals("true"))
            isSelf = true;
        userInfo = (UserInfo) intent.getSerializableExtra("UserInfo");
        username = findViewById(R.id.my_username);
        city = findViewById(R.id.my_user_city);
        name = findViewById(R.id.my_user_name);
        age = findViewById(R.id.my_user_age);
        sex = findViewById(R.id.my_user_sex);
        myself = findViewById(R.id.my_user_myself);
        deleteFriend = findViewById(R.id.bt_deleteFriend);
        userFace = findViewById(R.id.my_user_face);
        chatNow = findViewById(R.id.bt_chatNow);
        addFriend = findViewById(R.id.bt_addFriend);
        bt_addBlackList = findViewById(R.id.bt_addBlackList);
        userId = userInfo.getUserId();
        my_username = userInfo.getUsername();
        my_age = userInfo.getAge();
        my_city = userInfo.getCity();
        my_name = userInfo.getName();
        my_myself = userInfo.getPersonalProfile();
        my_sex = userInfo.getSex();
        username.setText(my_username);
        name.setText(my_name);
        age.setText(Integer.toString(my_age));
        sex.setText(my_sex);
        city.setText(my_city);
        myself.setText(my_myself);
        Glide.with(FriendCardActivity.this).load(new httpPathList().getUrl()+"/"+userInfo.getImg()).into(userFace);
//        userFace.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getImg()));
        if (isSelf) {
            addFriend.setVisibility(View.GONE);
            chatNow.setVisibility(View.GONE);
            deleteFriend.setVisibility(View.GONE);
            bt_addBlackList.setVisibility(View.GONE);
        }
    }

    public void setOnClickListeren(){
        MyOnClickListeren myOnClickListeren = new MyOnClickListeren();
        deleteFriend.setOnClickListener(myOnClickListeren);
        addFriend.setOnClickListener(myOnClickListeren);
        chatNow.setOnClickListener(myOnClickListeren);
        bt_addBlackList.setOnClickListener(myOnClickListeren);
    }

    public class MyOnClickListeren implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_deleteFriend:
                    new myThreadDeleteFriend().start();
                    break;
                case R.id.bt_addFriend:
                    new AddFriendDialogManager(FriendCardActivity.this, null, new AddFriendDialogManager.onClockListner() {
                        @Override
                        public void onClockListner(int i, String text) {
                            if (i == 0){
                                new myThreadAddFriend(text).start();
                            }
                        }
                    }).show();
                    break;
                case R.id.bt_chatNow:
                    ChatMessageActivity.open(getApplicationContext(),userInfo);
                    break;
                case R.id.bt_addBlackList:
                    new AlterDialogManager(FriendCardActivity.this, "添加黑名单，将接收不到该用户任何消息", new AlterDialogManager.onClockListner() {
                        @Override
                        public void onClockListner(int i) {
                            if (i == 0){
                                new myThreadAddBlack().start();
                            }
                        }
                    }).show();
                    break;
            }
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x17) {
                Bundle bundle1 = msg.getData();
                String result1 = bundle1.getString("result");
                if (result1.equals("succeed")) {
                    Intent intent = new Intent("updateMessage");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    Toast.makeText(FriendCardActivity.this, "删除好友成功!", Toast.LENGTH_SHORT).show();
                    addFriend.setVisibility(View.VISIBLE);
                    deleteFriend.setVisibility(View.GONE);
                    chatNow.setEnabled(false);
                    new myThread().start();
                } else {
                    Toast.makeText(FriendCardActivity.this, "删除好友失败!", Toast.LENGTH_SHORT).show();
                }
            }
            if (msg.what == 0x18) {
                Toast.makeText(FriendCardActivity.this, "已发送!", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == 0x19) {
                Toast.makeText(FriendCardActivity.this, "已添加!", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == 0x20) {
                Toast.makeText(FriendCardActivity.this, "已存在!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class myThreadDeleteFriend extends Thread {
        @Override
        public void run() {
            Message message;
            Bundle bundle;
            message = handler.obtainMessage();
            bundle = new Bundle();
            String result;
            Map<String, String> map = new HashMap<>();
            map.put("hostUserId", Integer.toString(Cookies.getCookies().getUserId()));
            map.put("userId", Integer.toString(userInfo.getUserId()));
            if ((result = HttpUtil.initJson().url(new httpPathList().getPath()[7]).method(HttpUtil.RequestMethod.GET).addParams(map).invoke()) != null) {
                bundle.putString("result", result);
            }
            message.setData(bundle);
            message.what = 0x17;
            handler.sendMessage(message);
        }
    }

     public class myThread extends Thread {
        @Override
        public void run() {
            String friendList = HttpUtil.initJson().url(new httpPathList().getPath()[5]).method(HttpUtil.RequestMethod.GET).addParam("userId",Integer.toString(Cookies.getCookies().getUserId())).invoke();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserInfo>>(){}.getType();
            List<UserInfo> userInfoList = gson.fromJson(friendList,listType);
            Cookies.getCookies().setUserInfos(userInfoList);
            Intent intent = new Intent("updateFriendList");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    public class myThreadAddFriend extends Thread {

        String content;

        public myThreadAddFriend(String text){
            content = text;
        }

        @Override
        public void run() {
            Message message;
            message = handler.obtainMessage();
            Map<String, String> map = new HashMap<>();
            map.put("userId", Integer.toString(Cookies.getCookies().getUserId()));
            map.put("userId2", Integer.toString(userId));
            map.put("text", content);
            HttpUtil.initJson().url(new httpPathList().getPath()[25]).method(HttpUtil.RequestMethod.POST).addParams(map).invoke();
            message.setData(bundle);
            message.what = 0x18;
            handler.sendMessage(message);
        }
    }

    public class myThreadAddBlack extends Thread {

        @Override
        public void run() {
            Message message;
            message = handler.obtainMessage();
            Map<String, String> map = new HashMap<>();
            map.put("hostId", Integer.toString(Cookies.getCookies().getUserId()));
            map.put("userId", Integer.toString(userId));
            String result = HttpUtil.initJson().url(new httpPathList().getPath()[30]).method(HttpUtil.RequestMethod.POST).addParams(map).invoke();
            if (result.equals("succeed")){
                message.what = 0x19;
                handler.sendMessage(message);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("updateBlackList"));
            }
            else {
                message.what = 0x20;
                handler.sendMessage(message);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
