package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helloworld.R;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;

import java.io.Serializable;
import java.util.List;

import static com.example.helloworld.http.HttpUtil.initJson;

public class ChangeGroupNameActivity extends AppCompatActivity {

    EditText editText;
    TextView succeed;
    String group,name;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chang_group_name);
        name = getIntent().getStringExtra("name");
        group = getIntent().getStringExtra("group");
        editText = findViewById(R.id.editText);
        editText.setText(name);
        succeed = findViewById(R.id.succeed);
        succeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.equals(editText.getText().toString())) {
                    name = editText.getText().toString();
                    new myThread().start();
                }
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    Toast.makeText(ChangeGroupNameActivity.this,"修改成功!",Toast.LENGTH_LONG);
                    Intent intent = new Intent("updateGroupChatName");
                    intent.putExtra("name",name);
                    LocalBroadcastManager.getInstance(ChangeGroupNameActivity.this).sendBroadcast(intent);
                    finish();
                }
            }
        };
    }

    static public void open(Context context,String name, String group) {
        Intent intent = new Intent(context, ChangeGroupNameActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("group",group);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    public class myThread extends Thread {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            String path = new httpPathList().getPath()[43];
            initJson().url(path).method(HttpUtil.RequestMethod.POST).addParam("group",group).addParam("name",name).invoke();
            message.what = 0x11;
            handler.sendMessage(message);
        }
    }
}
