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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.helloworld.R;
import com.example.helloworld.activity.AddFriendActivity;
import com.example.helloworld.activity.ChatMessageActivity;
import com.example.helloworld.adapter.FriendStraggerAdapter;
import com.example.helloworld.adapter.chatDialogAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stfalcon.chatkit.dialogs.DialogsList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class fragment_friend extends Fragment {

    RecyclerView recyclerView;
    String username;
    DialogsList dialogsListView;
    ImageButton bt_searchFriend;
    FriendStraggerAdapter friendStraggerAdapter;
    Handler handler;
    MyBroadcastReceive myBroadcastReceive;
    Context context;
    List<UserInfo> friendList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_friend,container,false);
        context = view.getContext();
        myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter("updateFriendList");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReceive,intentFilter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        username = Cookies.getCookies().getUsername();
        friendList = Cookies.getCookies().getUserInfos();
        recyclerView = view.findViewById(R.id.af_rcv);
        dialogsListView = view.findViewById(R.id.dialogsList);
        bt_searchFriend = view.findViewById(R.id.searchFriend);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        if (friendList == null)
            friendList = new ArrayList<>();
        friendStraggerAdapter = new FriendStraggerAdapter(context, friendList);
        recyclerView.setAdapter(friendStraggerAdapter);
        setOnClickListeren();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                if (msg.what == 0x10){
                    refresh();
                }
            }
        };
    }

    public void setOnClickListeren(){
        MyOnClickListeren myOnClickListeren = new MyOnClickListeren();
        bt_searchFriend.setOnClickListener(myOnClickListeren);
    }

    public class MyOnClickListeren implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case  R.id.searchFriend:
                    Intent intent4 = new Intent(context, AddFriendActivity.class);
                    startActivity(intent4);
                    break;
            }
        }
    }

    public void refresh(){
        friendList = Cookies.getCookies().getUserInfos();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        friendStraggerAdapter = new FriendStraggerAdapter(context, friendList);
        recyclerView.setAdapter(friendStraggerAdapter);
    }

    public class myThread extends Thread{
        @Override
        public void run() {
            String friendList = HttpUtil.initJson().url(new httpPathList().getPath()[5]).method(HttpUtil.RequestMethod.GET).addParam("userId",Integer.toString(Cookies.getCookies().getUserId())).invoke();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserInfo>>(){}.getType();
            List<UserInfo> UserInfos = gson.fromJson(friendList,listType);
            Cookies.getCookies().setUserInfos(UserInfos);
            android.os.Message message = handler.obtainMessage();
            message.what = 0x10;
            handler.sendMessage(message);
        }

    }

    public class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }

}
