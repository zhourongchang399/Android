package com.example.helloworld.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helloworld.R;
import com.example.helloworld.activity.LoginActivity;
import com.example.helloworld.activity.registerActivity;
import com.example.helloworld.ui.TimeCount;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class fragment_forgive extends Fragment {

    EditText ed_forgive_phone_number,ed_forgive_verification_code;
    Button bt_next_verification,bt_next_1;
    Context context;
    String phone,verify;
    EventHandler eh;
    TimeCount time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgive,container,false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ed_forgive_phone_number = view.findViewById(R.id.ed_forgive_phone_number);
        ed_forgive_verification_code = view.findViewById(R.id.ed_forgive_verification_code);
        bt_next_verification = view.findViewById(R.id.bt_next_verification);
        bt_next_1 = view.findViewById(R.id.bt_next_1);
        time = new TimeCount(60000,1000,bt_next_verification);

        view.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (event.getAction()){
                    case KeyEvent.KEYCODE_BACK:
                        Toast.makeText(getActivity(),"返回",Toast.LENGTH_LONG);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
        bt_next_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(phone = ed_forgive_phone_number.getText().toString()).isEmpty()) {
                    time.start();
                    SMSSDK.getVerificationCode("86", phone);
                }
            }
        });

        bt_next_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(verify = ed_forgive_verification_code.getText().toString()).isEmpty())
                    SMSSDK.submitVerificationCode("86",phone,verify);
            }
        });

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (msg.arg2 == -1)
                    if (getActivity() instanceof testDataCallback) {
                        ((testDataCallback) getActivity()).testData(phone);
                    }
                    else {
                        Toast.makeText(context,"验证码错误",Toast.LENGTH_LONG);
                    }
                }
            }
        };
        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);

            }
        };

//注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eh);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    public interface testDataCallback {
        void testData(String phone);
    }
}
