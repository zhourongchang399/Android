package com.example.helloworld.view;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helloworld.R;
import com.example.helloworld.activity.LoginActivity;
import com.example.helloworld.activity.registerActivity;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.ConnectServer;
import com.example.helloworld.socket.SocketManager;
import com.example.helloworld.ui.MyDialogManager;
import com.example.helloworld.ui.TimeCount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static android.content.ContentValues.TAG;


public class fragment_register extends Fragment {
    Button confirm_register,confirm_verify;
    Toolbar toolbar;
    EditText ed_user,ed_password,ed_confirm_password,ed_phone,ed_verify;
    String user,password,confrim_password,phone;
    Boolean result = true;
    MyDialogManager myDialogManager = null;
    Context context;
    int i ;
    Handler handler;
    EventHandler eh;
    TimeCount time;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        confirm_register = view.findViewById(R.id.confirm_register);
        toolbar = view.findViewById(R.id.toolbar);
        ed_user = view.findViewById(R.id.ed_user);
        ed_password  = view.findViewById(R.id.ed_password);
        ed_confirm_password = view.findViewById(R.id.ed_confirm_password);
        ed_phone = view.findViewById(R.id.ed_phone);
        ed_verify = view.findViewById(R.id.ed_verify);
        confirm_verify = view.findViewById(R.id.confirm_verify);
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                msg.getData();
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (msg.arg2 == -1) {
                        if (verify()) {
                            myThread();
                        }
                    }
                    else
                        Toast.makeText(context,"???????????????",Toast.LENGTH_LONG);
                } else if (msg.arg1 == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                }
            }
        };
        time = new TimeCount(60000,1000,confirm_verify);
        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO ????????????????????????UI??????????????????????????????????????????????????????
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };

//?????????????????????????????????????????????SMSSDK?????????????????????
        SMSSDK.registerEventHandler(eh);
        confirm_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ed_phone.getText().toString().isEmpty()) {
                    time.start();
                    SMSSDK.getVerificationCode("86", ed_phone.getText().toString());
                }
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String result;
                if (msg.what == 0x11) {
                    Bundle bundle = msg.getData();
                    result = bundle.getString("result");
                    if (result.equals("succeed")){
                        myDialogManager = new MyDialogManager(view.getContext(), "????????????", 1);
                        myDialogManager.show();
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        myDialogManager.dismiss();
                    }
                    else if (result.equals("defeat")){
                        myDialogManager = new MyDialogManager(view.getContext(), "????????????", 0);
                        myDialogManager.show();
                    }
                }
            }
        };
        confirm_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSSDK.submitVerificationCode("86", ed_phone.getText().toString(), ed_verify.getText().toString());
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
            view.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    switch (event.getAction()){
                        case KeyEvent.KEYCODE_BACK:
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            });
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }


    public void setResult(Boolean b){
        result = b;
    }

    public void myThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = new httpPathList().getPath()[1];
                String result = HttpUtil
                        .initJson()
                        .url(path)
                        .addParam("password", password)
                        .addParam("username", user)
                        .addParam("phone", phone)
                        .method(HttpUtil.RequestMethod.POST)
                        .invokeClass(String.class);
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("result", result);
                message.setData(bundle);
                message.what = 0x11;
                handler.sendMessage(message);
            }
        }).start();
    }

    public boolean verify(){
        user = ed_user.getText().toString();
        password = ed_password.getText().toString();
        confrim_password = ed_confirm_password.getText().toString();
        phone = ed_phone.getText().toString();
        String verify = ed_verify.getText().toString();
        if (!password.equals(confrim_password)) {
            MyDialogManager myDialogManager = new MyDialogManager(context,"?????????????????????",0);
            myDialogManager.show();
            return false;
        }
        else if (user.isEmpty() || password.isEmpty() || confrim_password.isEmpty()){
            MyDialogManager myDialogManager = new MyDialogManager(context,"??????????????????????????????",0);
            myDialogManager.show();
            return false;
        }
        else if (verify.isEmpty()){
            MyDialogManager myDialogManager = new MyDialogManager(context,"?????????????????????",0);
            myDialogManager.show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}
