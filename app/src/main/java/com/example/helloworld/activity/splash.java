package com.example.helloworld.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.helloworld.MainActivity;
import com.example.helloworld.R;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.SocketManager;
import com.example.helloworld.socket.chatSocket;

import java.io.IOException;
import java.net.Socket;
import com.bumptech.glide.Glide;

public class splash extends MyAppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_splash);
        imageView = findViewById(R.id.splash_image);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load("https://cdn.pixabay.com/photo/2019/09/19/20/24/city-4490237_960_720.jpg").into(imageView);
        Thread myThread=new Thread(){//创建子线程
            @Override
            public void run() {
                try{
                    sleep(3000);//使程序休眠五秒
                    Intent it=new Intent(getApplicationContext(), MainActivity.class);//启动MainActivity
                    startActivity(it);
                    finish();//关闭当前活动
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程
    }

}
