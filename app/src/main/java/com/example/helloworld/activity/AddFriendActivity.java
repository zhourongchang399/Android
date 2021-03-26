package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.R;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserAccount;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.view.fragment_addFriend_search;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class AddFriendActivity extends FragmentActivity {
    TextView myUsername;
    EditText search_friend;
    Button bt_search;
    String searchUsername;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        handler = new MyHandler();
        init();
    }

    private void init() {
        myUsername = findViewById(R.id.tv_af_usename);
        search_friend = findViewById(R.id.ed_af_search);
        bt_search = findViewById(R.id.bt_af_search);
        myUsername.setText("我的账号："+ Cookies.getCookies().getUsername());
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchUsername = search_friend.getText().toString().trim();
                if(searchUsername.equals("") || searchUsername == null){
                    Toast.makeText(AddFriendActivity.this,"请输入要查找的账号或用户名",Toast.LENGTH_SHORT);
                }
                else {
                    new MyThread().start();
                }
            }
        });
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x15) {
                Gson gson = new Gson();
                List<UserInfo> UserInfos;
                Type listType;
                Bundle bundle = msg.getData();
                String result = bundle.getString("result");
                if (!result.equals("null")) {
                    listType = new TypeToken<List<UserInfo>>() {
                    }.getType();
                    UserInfos = gson.fromJson(result, listType);
                    replaceFragment(new fragment_addFriend_search(UserInfos));
                }
                else
                    Toast.makeText(AddFriendActivity.this,"未找到该用户",Toast.LENGTH_SHORT);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();   // 开启一个事务
        transaction.replace(R.id.fa_add_friend, fragment);
        transaction.commit();
    }

    public class MyThread extends Thread{
        @Override
        public void run() {
            String result;
            Message message;
            Bundle bundle;
            message = handler.obtainMessage();
            bundle = new Bundle();
            if ((result = HttpUtil.initJson().url(new httpPathList().getPath()[4]).method(HttpUtil.RequestMethod.GET).addParam("username",searchUsername).invoke()) != null) {
                bundle.putString("result",result);
            }
            else
                bundle.putString("result","null");
            message.setData(bundle);
            message.what = 0x15;
            handler.sendMessage(message);

        }
    }
}
