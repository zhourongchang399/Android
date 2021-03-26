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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.activity.AddGroupMenberActivity;
import com.example.helloworld.activity.GroupChatSettingActivity;
import com.example.helloworld.adapter.chatDialogAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.Dialog;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class fragment_dialogMsg extends Fragment {
    List<Dialog> dialogList;
    List<UserInfo> userInfoList;
    Handler handler;
    updateMessageReceiver updateMessageReceiver;
    chatDialogAdapter chatDialogAdapter;
    RecyclerView dialogsRecyclerView;
    Context context;
    ImageView createGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat,container,false);
        context = view.getContext();
        IntentFilter intentFilter = new IntentFilter("updateMessage");
        updateMessageReceiver = new updateMessageReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateMessageReceiver,intentFilter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createGroup = view.findViewById(R.id.createGroup);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupMenberActivity.open(context,null,"","0");
            }
        });
        if (dialogList == null)
            dialogList = new ArrayList<>();
        dialogsRecyclerView = view.findViewById(R.id.dialogsList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        dialogsRecyclerView.setLayoutManager(layoutManager);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                if (msg.what == 0x10){
                    chatDialogAdapter = new chatDialogAdapter(context,dialogList);
                    dialogsRecyclerView.setAdapter(chatDialogAdapter);
                }
            }
        };
        new myThread().start();
    }

    public class myThread extends Thread{
        @Override
        public void run() {
            Type type;
            Gson gson = new Gson();
            type = new TypeToken<List<Dialog>>(){}.getType();
            userInfoList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("userid", Cookies.getCookies().getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String result = HttpUtil.initJson().url(new httpPathList().getPath()[11]).body(jsonObject).method(HttpUtil.RequestMethod.POST).invoke();
            if (result != null) {
                dialogList = gson.fromJson(result,type);
                android.os.Message message = handler.obtainMessage();
                message.what = 0x10;
                handler.sendMessage(message);
            }
        }
    }

    public class updateMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new myThread().start();
        }
    }

}
