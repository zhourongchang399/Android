package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.load.Option;
import com.example.helloworld.R;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserAccount;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.ClientInfo;
import com.example.helloworld.socket.ConnectServer;
import com.example.helloworld.socket.SocketManager;
import com.example.helloworld.ui.LoadingDialogManager;
import com.example.helloworld.ui.MyDialogManager;
import com.example.helloworld.view.fragment_register;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.CollationKey;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OtherLoginActivity extends MyAppCompatActivity {
    EditText user, password;
    Button bt_login;
    MyDialogManager myDialogManager;
    String string_user, string_password;
    String context;
    Handler handler;
    List<UserInfo> UserInfos;
    LoadingDialogManager loadingDialogManager;
    Gson gson;
    Type listType;
    SharedPreferences sharedPreferences;
    boolean isLogin;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_login_activyty);
        user = findViewById(R.id.ed_user_login);
        password = findViewById(R.id.ed_password_login);
        bt_login = findViewById(R.id.login);
        setOnClickListeren();
        context = getIntent().getStringExtra("context");
        sharedPreferences = getApplication().getSharedPreferences("share", MODE_PRIVATE);
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x10) {
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
                    final int userId = bundle.getInt("userId");
                    if (result.equals("succeed")) {
                        editor = sharedPreferences.edit();
                        editor.putBoolean("isLogin",true);
                        editor.putString("username", string_user);
                        editor.putString("password", string_password);
                        editor.putBoolean("isFirstOpen", false);
                        editor.commit();
                        Message message = handler.obtainMessage();
                        message.what = 0x11;
                        handler.sendMessage(message);
                        myDialogManager = new MyDialogManager(OtherLoginActivity.this, "登录成功", 1);
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
                        Intent intent = new Intent(OtherLoginActivity.this,ApplicationActivity.class);
                        startActivity(intent);
                        myDialogManager.dismiss();
                        loadingDialogManager.dismiss();
                    }
                    else if (result.equals("defeat")){
                        loadingDialogManager.dismiss();
                        myDialogManager = new MyDialogManager(OtherLoginActivity.this, "登录失败", 1);
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
    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login:
                    string_user = user.getText().toString();
                    string_password = password.getText().toString();
                    if(string_user == null || string_password == null || string_user.equals("") || string_password.equals("")){
                        myDialogManager = new MyDialogManager(OtherLoginActivity.this, "账号或密码为空！", 1);
                        myDialogManager.show();
                    }
                    else {
                        loadingDialogManager = new LoadingDialogManager(OtherLoginActivity.this);
                        loadingDialogManager.show();
                        final JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.putOpt("password", string_password);
                            jsonObject.putOpt("username", string_user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new Thread() {
                            @Override
                            public void run() {
                                String path = new httpPathList().getPath()[0];
                                Message message = handler.obtainMessage();
                                Bundle bundle = new Bundle();
                                UserAccount userAccount = HttpUtil.initJson().url(path).method(HttpUtil.RequestMethod.POST).body(jsonObject).invokeClass(UserAccount.class);
                                if (userAccount == null)
                                    bundle.putString("result", "defeat");
                                else {
                                    bundle.putString("result", "succeed");
                                    bundle.putInt("userId", userAccount.getUserid());
                                    Cookies.getCookies().setUserId(userAccount.getUserid());
                                    Cookies.getCookies().setUsername(userAccount.getUsername());
                                }
                                message.setData(bundle);
                                message.what = 0x10;
                                handler.sendMessage(message);
                            }
                        }.start();
                    }

//                    Callable<UserAccount> accountCallable = new Callable<UserAccount>() {
//                        @Override
//                        public UserAccount call() throws Exception {
//                            String path = new httpPathList().getPath()[0];
//                            return HttpUtil.initJson()
//                                    .url(path)
//                                    .method(HttpUtil.RequestMethod.POST)
//                                    .body(jsonObject)
//                                    .invokeClass(UserAccount.class);
//                        }
//                    };
//                    ExecutorService executorService = Executors.newCachedThreadPool();
//                    Future<UserAccount> submit = executorService.submit(accountCallable);
//                    try {
//                        UserAccount userAccount1 = submit.get();
//                        System.out.println(userAccount1);
//                        executorService.shutdown();
//                    } catch (ExecutionException | InterruptedException e) {
//                        e.printStackTrace();
//                    }


//                    Gson gson = new Gson();
//                    UserAccount userAccount = gson.fromJson(json,UserAccount.class);
//                        Handler handler = new Handler();
////                      handler.postDelayed(new runnable(OtherLoginActivity.this), 500);
//                    else if (i == 0){
//                        myDialogManager = new MyDialogManager(OtherLoginActivity.this, "登录失败", 0);
//                        myDialogManager.show();
//                    }
                    break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}
