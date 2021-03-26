package com.example.helloworld.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworld.R;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;

public class changeUserAccountDialogManager extends Dialog {

    Button change, back;
    EditText now, confirm;
    String pas, pas2;
    Handler handler;
    Context context;

    public changeUserAccountDialogManager(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public changeUserAccountDialogManager(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected changeUserAccountDialogManager(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_useraccount);
        init();
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x12) {
                    MyDialogManager myDialogManager = new MyDialogManager(context,"修改成功!",1);
                    myDialogManager.show();
                }
            }

        };
    }

    private void init() {
        change = findViewById(R.id.self_save);
        back = findViewById(R.id.self_cancel);
        now = findViewById(R.id.new_pas);
        confirm = findViewById(R.id.confirm_pas);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pas = now.getText().toString();
                pas2 = confirm.getText().toString();
                if (now.getText().toString().isEmpty() || confirm.getText().toString().isEmpty())
                    new MyDialogManager(context,"密码不能为空!",0).show();
                else
                    if (!pas.equals(pas2))
                        new MyDialogManager(context,"密码两次不一样!",0).show();
                    else
                        new myThread().start();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public class myThread extends Thread {
        @Override
        public void run() {
            super.run();
            Message message = handler.obtainMessage();
            HttpUtil.initJson()
                    .url(new httpPathList().getPath()[35])
                    .method(HttpUtil.RequestMethod.POST)
                    .addParam("username", Cookies.getCookies().getUsername())
                    .addParam("password", pas)
                    .invoke();
            message.what = 0x12;
            handler.sendMessage(message);
        }
    }

}
