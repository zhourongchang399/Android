package com.example.helloworld.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.helloworld.R;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserAccount;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.SocketManager;
import com.example.helloworld.ui.LoadingDialogManager;
import com.example.helloworld.ui.MyDialogManager;
import com.example.helloworld.ui.TimeCount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class PhoneLoginActivity extends MyAppCompatActivity {
    EditText user, password;
    Button bt_login,verify;
    MyDialogManager myDialogManager;
    String string_user, string_password,uc_user,uc_password;
    String context;
    Handler handler;
    List<UserInfo> UserInfos;
    LoadingDialogManager loadingDialogManager;
    Gson gson;
    Type listType;
    EventHandler eh;
    TimeCount time;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login_activyty);
        user = findViewById(R.id.ed_user_login);
        password = findViewById(R.id.ed_password_login);
        bt_login = findViewById(R.id.login);
        verify = findViewById(R.id.verify);
        setOnClickListeren();
        context = getIntent().getStringExtra("context");
        time = new TimeCount(60000,1000,verify);
        sharedPreferences = getApplication().getSharedPreferences("share", MODE_PRIVATE);

        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };

//注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eh);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (msg.arg2 == -1) {
                        new thread().start();
                    } else {
                        myDialogManager = new MyDialogManager(PhoneLoginActivity.this, "验证码错误！", 1);
                        myDialogManager.show();
                        loadingDialogManager.dismiss();
                    }
                }
                if (msg.what == 0x10) {
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
                    final int userId = bundle.getInt("userId");
                    if (result.equals("succeed")) {
                        editor = sharedPreferences.edit();
                        editor.putBoolean("isLogin",true);
                        editor.putString("username", uc_user);
                        editor.putString("password", uc_password);
                        editor.putBoolean("isFirstOpen", false);
                        editor.commit();
                        Message message = handler.obtainMessage();
                        message.what = 0x11;
                        handler.sendMessage(message);
                        myDialogManager = new MyDialogManager(PhoneLoginActivity.this, "登录成功", 1);
                        myDialogManager.show();
                        new Thread() {
                            @Override
                            public void run() {
                                UserInfo userInfo = HttpUtil.initJson().url(new httpPathList().getPath()[2]).method(HttpUtil.RequestMethod.GET).addParam("userId",Integer.toString(userId)).invokeClass(UserInfo.class);
                                String friendList = HttpUtil.initJson().url(new httpPathList().getPath()[5]).method(HttpUtil.RequestMethod.GET).addParam("userId",Integer.toString(userId)).invoke();
                                gson = new Gson();
                                listType = new TypeToken<List<UserInfo>>(){}.getType();
                                UserInfos = gson.fromJson(friendList,listType);
                                Cookies.getCookies().setUserInfos(UserInfos);
                                Cookies.getCookies().setName(userInfo.getName());
                                Cookies.getCookies().setUsername(userInfo.getUsername());
                                Cookies.getCookies().setPersonalProfile(userInfo.getPersonalProfile());
                                Cookies.getCookies().setCity(userInfo.getCity());
                                Cookies.getCookies().setAge(userInfo.getAge());
                                Cookies.getCookies().setSex(userInfo.getSex());
                                Cookies.getCookies().setImg(userInfo.getImg());
                            }
                        }.start();
                        removeALLActivity();
                        Intent intent = new Intent(PhoneLoginActivity.this,ApplicationActivity.class);
                        startActivity(intent);
                        myDialogManager.dismiss();
                        loadingDialogManager.dismiss();
                    }
                    else if (result.equals("defeat")){
                        loadingDialogManager.dismiss();
                        myDialogManager = new MyDialogManager(PhoneLoginActivity.this, "登录失败", 1);
                        myDialogManager.show();
                    }
                }
                if (msg.what == 0x11) {

                }
            }
        };
    }

    public void setOnClickListeren() {
        OnClick onClick = new OnClick();
        bt_login.setOnClickListener(onClick);
        verify.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.verify:
                    if (user.getText().toString().isEmpty()){
                        Toast.makeText(PhoneLoginActivity.this,"手机号不能为空！",Toast.LENGTH_LONG);
                    }
                    else {
                        time.start();
                        SMSSDK.getVerificationCode("86", user.getText().toString());
                    }
                    break;
                case R.id.login:
                    string_user = user.getText().toString();
                    string_password = password.getText().toString();
                    SMSSDK.submitVerificationCode("86",string_user,string_password);
                    loadingDialogManager = new LoadingDialogManager(PhoneLoginActivity.this);
                    loadingDialogManager.show();
                    break;
            }
        }
    }

    public class thread extends Thread{
        @Override
        public void run() {
            super.run();
            String path = new httpPathList().getPath()[34];
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            UserAccount userAccount = HttpUtil
                    .initJson()
                    .url(path)
                    .method(HttpUtil.RequestMethod.POST)
                    .addParam("phone",string_user)
                    .invokeClass(UserAccount.class);
            if (userAccount == null)
                bundle.putString("result", "defeat");
            else {
                bundle.putString("result", "succeed");
                bundle.putInt("userId", userAccount.getUserid());
                Cookies.getCookies().setUserId(userAccount.getUserid());
                Cookies.getCookies().setUsername(userAccount.getUsername());
                uc_user = userAccount.getUsername();
                uc_password = userAccount.getPassword();
            }
            message.setData(bundle);
            message.what = 0x10;
            handler.sendMessage(message);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}
