package com.example.helloworld.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.helloworld.R;
import com.example.helloworld.adapter.GroupMenberStraggerAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.ui.MyDialogManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchByMenberActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<UserInfo> userInfoList;
    String name,group;
    TextView succeed;
    Handler handler;
    MyDialogManager myDialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_menber);
        Bundle bundle = getIntent().getExtras();
        userInfoList = (List<UserInfo>)bundle.getSerializable("userInfos");
        group = getIntent().getStringExtra("group");
        name = getIntent().getStringExtra("name");
        recyclerView = findViewById(R.id.group_menber);
        succeed = findViewById(R.id.succeed);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        if (userInfoList == null)
            userInfoList = new ArrayList<>();
        recyclerView.setAdapter(new GroupMenberStraggerAdapter(SearchByMenberActivity.this, userInfoList, false, new GroupMenberStraggerAdapter.OnClickListener() {
            @Override
            public void OnClick(String i) {
                Intent intent = new Intent("searchMenber");
                intent.putExtra("userId",i);
                LocalBroadcastManager.getInstance(SearchByMenberActivity.this).sendBroadcast(intent);
                finish();
            }
        }));
        succeed.setVisibility(View.GONE);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

            }
        };
    }

    static public void open(Context context, List<UserInfo> userInfos, String name, String group) {
        Intent intent = new Intent(context, SearchByMenberActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfos", (Serializable) userInfos);
        intent.putExtras(bundle);
        intent.putExtra("name",name);
        intent.putExtra("group",group);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

}
