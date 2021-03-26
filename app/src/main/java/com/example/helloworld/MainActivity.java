package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.helloworld.activity.ApplicationActivity;
import com.example.helloworld.activity.LoginActivity;
import com.example.helloworld.activity.OtherLoginActivity;
import com.example.helloworld.activity.PermissionActivity;
import com.example.helloworld.activity.splash;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserAccount;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.SocketManager;
import com.example.helloworld.socket.chatSocket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends MyAppCompatActivity {
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问
    static int status = 0;
    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    SharedPreferences sharedPreferences;
    boolean isFirstOpen,isLogin;
    SharedPreferences.Editor editor;
    ImageView imageView;
    Handler handler;
    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (status == 0)
            myRequetPermission();
        setContentView(R.layout.activity_splash);
        imageView = findViewById(R.id.splash_image);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackgroundResource(R.drawable.splash);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x12){
                    if (msg.getData().get("result").equals("succeed"))
                        new logonThread().start();
                    else {
                        removeALLActivity();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
    }

    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            new myThread().start();//启动线程
            status = 1;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                    removeActivity();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                        intent.setData(uri);
                                        startActivityForResult(intent, NOT_NOTICE);
                                    }
                                });
                        mDialog = builder.create();
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();


                    } else {//选择禁止
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (alertDialog != null && alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }

                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOT_NOTICE) {
            myRequetPermission();//由于不知道是否选择了允许所以需要再次判断
        }
    }

    public class myThread extends Thread {//创建子线程

        @Override
        public void run() {
            try {
                sharedPreferences = getApplication().getSharedPreferences("share", MODE_PRIVATE);
                isFirstOpen = sharedPreferences.getBoolean("isFirstOpen", true);
                isLogin = sharedPreferences.getBoolean("isLogin", false);
                editor = sharedPreferences.edit();
                sleep(1500);
                if (isFirstOpen) {
                    editor.putBoolean("isFirstOpen", false);
                    editor.commit();
                    Intent intent1 = new Intent(MainActivity.this, splash.class);
                    startActivity(intent1);
                    finish();
                } else
                    if(!isLogin){
                    Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    }
                    else {
                        username = sharedPreferences.getString("username",null);
                        password = sharedPreferences.getString("password",null);
                        new thread().start();
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class thread extends Thread {
        @Override
        public void run() {
            super.run();
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("password", password);
                jsonObject.putOpt("username", username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String path = new httpPathList().getPath()[0];
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            UserAccount userAccount = null;
            userAccount = HttpUtil.initJson().url(path).method(HttpUtil.RequestMethod.POST).body(jsonObject).invokeClass(UserAccount.class);
            if (userAccount == null)
                bundle.putString("result", "defeat");
            else {
                bundle.putString("result", "succeed");
                Cookies.getCookies().setUserId(userAccount.getUserid());
                Cookies.getCookies().setUsername(userAccount.getUsername());
            }
            message.setData(bundle);
            message.what = 0x12;
            handler.sendMessage(message);
        }
    }

    public class logonThread extends Thread{
        @Override
        public void run() {
            super.run();
            UserInfo userInfo = HttpUtil.initJson().url(new httpPathList().getPath()[2]).method(HttpUtil.RequestMethod.GET).addParam("userId", Integer.toString(Cookies.getCookies().getUserId())).invokeClass(UserInfo.class);
            String friendList = HttpUtil.initJson().url(new httpPathList().getPath()[5]).method(HttpUtil.RequestMethod.GET).addParam("userId", Integer.toString(Cookies.getCookies().getUserId())).invoke();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserInfo>>() {
            }.getType();
            List<UserInfo> UserInfos = gson.fromJson(friendList, listType);
            Cookies.getCookies().setUserInfos(UserInfos);
            Cookies.getCookies().setName(userInfo.getName());
            Cookies.getCookies().setUsername(userInfo.getUsername());
            Cookies.getCookies().setPersonalProfile(userInfo.getPersonalProfile());
            Cookies.getCookies().setCity(userInfo.getCity());
            Cookies.getCookies().setAge(userInfo.getAge());
            Cookies.getCookies().setSex(userInfo.getSex());
            Cookies.getCookies().setImg(userInfo.getImg());
            removeALLActivity();
            Intent intent = new Intent(MainActivity.this, ApplicationActivity.class);
            startActivity(intent);
        }
    }
}
