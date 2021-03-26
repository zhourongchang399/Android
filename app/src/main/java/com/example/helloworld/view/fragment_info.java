package com.example.helloworld.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.helloworld.R;
import com.example.helloworld.activity.AddFriendActivity;
import com.example.helloworld.activity.BlackListActivity;
import com.example.helloworld.adapter.FriendStraggerAdapter;
import com.example.helloworld.adapter.InfoDialogAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.Info;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.ui.MyDialogManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stfalcon.chatkit.dialogs.DialogsList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class fragment_info extends Fragment {

    RecyclerView recyclerView;
    String username;
    DialogsList dialogsListView;
    ImageButton deleteAllInfo;
    LinearLayout blackOrders;
    InfoDialogAdapter infoDialogAdapter;
    Handler handler;
    MyBroadcastReceive myBroadcastReceive;
    Context context;
    List<Info> infoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info,container,false);
        context = view.getContext();
        myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter("updateInfo");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReceive,intentFilter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        username = Cookies.getCookies().getUsername();
        recyclerView = view.findViewById(R.id.if_rc);
        dialogsListView = view.findViewById(R.id.dialogsList);
        deleteAllInfo = view.findViewById(R.id.deleteAllInfo);
        blackOrders = view.findViewById(R.id.blackOrders);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        new myThread().start();
        setOnClickListeren();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                if (msg.what == 0x10){
                    infoList = Cookies.getCookies().getInfoList();
                }
                if (msg.what == 0x11){
                    new MyDialogManager(context,"清空成功！",1).show();
                    Cookies.getCookies().setInfoList(null);
                    infoList = Cookies.getCookies().getInfoList();
                }
                if (infoList == null)
                    infoList = new ArrayList<>();
                infoDialogAdapter = new InfoDialogAdapter(context, infoList);
                recyclerView.setAdapter(infoDialogAdapter);
            }
        };
    }

    public void setOnClickListeren(){
        MyOnClickListeren myOnClickListeren = new MyOnClickListeren();
        deleteAllInfo.setOnClickListener(myOnClickListeren);
        blackOrders.setOnClickListener(myOnClickListeren);
    }

    public class MyOnClickListeren implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case  R.id.deleteAllInfo:
                    new myDeleteForAllThread().start();
                    break;
                case R.id.blackOrders:
                    Intent intent = new Intent(context, BlackListActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    public class myDeleteForAllThread extends Thread{
        @Override
        public void run() {
                    HttpUtil.initJson()
                            .url(new httpPathList().getPath()[29])
                            .method(HttpUtil.RequestMethod.POST)
                            .addParam("userId",Integer.toString(Cookies.getCookies().getUserId()))
                            .invoke();
            android.os.Message message = handler.obtainMessage();
            message.what = 0x11;
            handler.sendMessage(message);
        }
    }

    public class myThread extends Thread{
        @Override
        public void run() {
            String result =
                    HttpUtil.initJson()
                            .url(new httpPathList().getPath()[28])
                            .method(HttpUtil.RequestMethod.POST)
                            .addParam("userId",Integer.toString(Cookies.getCookies().getUserId()))
                            .invoke();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Info>>(){}.getType();
            List<Info> infoList = new ArrayList<>();
            if (result != null) {
                infoList = gson.fromJson(result, listType);
            }
            Cookies.getCookies().setInfoList(infoList);
            android.os.Message message = handler.obtainMessage();
            message.what = 0x10;
            handler.sendMessage(message);
        }
    }

    public class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new myThread().start();
        }
    }

}
