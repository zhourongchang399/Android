package com.example.helloworld.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helloworld.R;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.ui.MyDialogManager;

import cn.smssdk.SMSSDK;

public class fragment_forgive_change extends Fragment {

    Handler handler;
    EditText password,confirm_password;
    Button bt_next_2;
    String phone,psd,cf_psd;
    Context context;

    public fragment_forgive_change(String phone){
        this.phone = phone;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgive_change,container,false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        password = view.findViewById(R.id.ed_forgive_phone_number);
        confirm_password = view.findViewById(R.id.ed_forgive_verification_code);
        bt_next_2 = view.findViewById(R.id.bt_next_2);

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    if (getActivity() instanceof testDataCallback2) {
                        ((testDataCallback2) getActivity()).testData2();
                    }
                }
            }
        };

        bt_next_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psd = password.getText().toString();
                cf_psd = confirm_password.getText().toString();
                if (psd.equals(cf_psd)){
                    new myThread().start();
                }
                else
                if (psd.isEmpty() || cf_psd.isEmpty()){
                    MyDialogManager myDialogManager = new MyDialogManager(context,"密码不能为空!",0);
                    myDialogManager.show();
                }
                else {
                    MyDialogManager myDialogManager = new MyDialogManager(context,"两次密码不一样!",0);
                    myDialogManager.show();
                }
            }
        });
    }

    public class myThread extends Thread{
        @Override
        public void run() {
            super.run();
            String path = new httpPathList().getPath()[33];
            HttpUtil
                    .initJson()
                    .url(path)
                    .addParam("password", password.getText().toString())
                    .addParam("phone", phone)
                    .method(HttpUtil.RequestMethod.POST)
                    .invokeClass(String.class);
            Message message = handler.obtainMessage();
            message.what = 0x11;
            handler.sendMessage(message);
        }
    }

    public interface testDataCallback2 {
        void testData2();
    }
}
