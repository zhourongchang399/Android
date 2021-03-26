package com.example.helloworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.ChatMessageActivity;
import com.example.helloworld.activity.FriendCardActivity;
import com.example.helloworld.activity.OtherLoginActivity;
import com.example.helloworld.activity.SearchOneActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.Dialog;
import com.example.helloworld.data.Info;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.AlterDialogManager;
import com.example.helloworld.ui.emoji.EmojiUtil;
import com.example.helloworld.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

public class InfoDialogAdapter extends RecyclerView.Adapter<InfoDialogAdapter.ViewHolder> {

    Context context;
    String username;
    List<Info> infoList;
    Handler handler;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView owner_image;
        TextView title,msg;
        Button status;
        RelativeLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            title= itemView.findViewById(R.id.chatTitle);
            msg= itemView.findViewById(R.id.chatLastMsg);
            status= itemView.findViewById(R.id.status);
            owner_image = itemView.findViewById(R.id.owen_image);
            linearLayout = itemView.findViewById(R.id.alllayout);
        }
    }

    public InfoDialogAdapter(Context context, List<Info> infoList) {
        this.context = context;
        username = Cookies.getCookies().getUsername();
        this.infoList = infoList;
    }

    @Override
    public InfoDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item,parent,false);
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("updateInfo"));
                    new myFriendThread().start();
                }
                if (msg.what == 0x12){
                    Intent intent = new Intent("updateFriendList");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("updateMessage"));
                }
            }
        };
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfoDialogAdapter.ViewHolder holder, int position) {
        if (infoList.size() != 0) {
            final Info info = infoList.get(position);
            Glide.with(context).load(new httpPathList().getUrl()+"/"+info.getUserInfo().getImg()).centerCrop().into(holder.owner_image);
//            holder.owner_image.setImageBitmap(ImageUtils.base64ToBitmap(info.getUserInfo().getImg()));
            holder.linearLayout.setOnClickListener(new onClick(info.getUserInfo()));
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlterDialogManager(context, "是否要删除？", new AlterDialogManager.onClockListner() {
                        @Override
                        public void onClockListner(int i) {
                            if (i == 0) {
                                new myDeleteThread(info.getUserInfo().getUserId()).start();
                                Intent intent = new Intent("updateInfo");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        }
                    }).show();
                    return true;
                }
            });
            holder.title.setText(info.getUserInfo().getName());
            if (info.getText() != null) {
                    try {
                        String content = info.getText().replace("\n", " ");
                        if (content.length() > 20) {
                            content = content.substring(0, 20);
                            content = content.concat("...");
                        }
                        EmojiUtil.handlerEmojiText(holder.msg, content, context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            if (info.getStatus().equals("true")){
                holder.status.setText("已添加");
                holder.status.setEnabled(false);
            }
            if (info.getStatus().equals("null")){
                holder.status.setText("确认");
                holder.status.setEnabled(true);
            }
            holder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeStatus(info.getUserInfo().getUserId(),Cookies.getCookies().getUserId(),"true");
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public class onClick implements View.OnClickListener {
        UserInfo userInfo;
        public onClick(UserInfo userInfo){
            this.userInfo = userInfo;
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.alllayout:
                    List<Info> userInfos = Cookies.getCookies().getInfoList();
                        if (userInfos.size() == 0) {
                            Intent intent = new Intent(context, SearchOneActivity.class);
                            intent.putExtra("UserInfo", userInfo);
                            intent.putExtra("ifSelf", "false");
                            context.startActivity(intent);
                        } else {
                            if (infoList.size() != 0) {
                                for (int j = 0; j < userInfos.size(); j++) {
                                    Info userInfoFri = userInfos.get(j);
                                    if (userInfo.getUserId() == userInfoFri.getUserInfo().getUserId()) {
                                        Intent intent = new Intent(context, FriendCardActivity.class);
                                        intent.putExtra("UserInfo", userInfo);
                                        intent.putExtra("ifSelf", "false");
                                        context.startActivity(intent);
                                        break;
                                    } else {
                                        Intent intent = new Intent(context, SearchOneActivity.class);
                                        intent.putExtra("UserInfo", userInfo);
                                        intent.putExtra("ifSelf", "false");
                                        context.startActivity(intent);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
            }
        }
    }

    public class myDeleteThread extends Thread{

        int userId;

        public myDeleteThread(int userId){
            this.userId = userId;
        }

        @Override
        public void run() {
            HttpUtil.initJson()
                    .url(new httpPathList().getPath()[27])
                    .method(HttpUtil.RequestMethod.POST)
                    .addParam("userId",Integer.toString(userId))
                    .addParam("userId2",Integer.toString(Cookies.getCookies().getUserId()))
                    .invoke();
        }
    }

    public void changeStatus(final int userId, final int userId2, final String status){

        new Thread(){
            @Override
            public void run() {
                super.run();
                String result = HttpUtil.initJson()
                        .url(new httpPathList().getPath()[26])
                        .addParam("userId",Integer.toString(userId))
                        .addParam("userId2",Integer.toString(userId2))
                        .addParam("status",status)
                        .method(HttpUtil.RequestMethod.POST)
                        .invoke();
                if (result != null && result.equals("succeed")){
                    Message message = handler.obtainMessage();
                    message.what = 0x11;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    public class myFriendThread extends Thread {
        @Override
        public void run() {
            String friendList = HttpUtil.initJson().url(new httpPathList().getPath()[5]).method(HttpUtil.RequestMethod.GET).addParam("userId",Integer.toString(Cookies.getCookies().getUserId())).invoke();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<UserInfo>>(){}.getType();
            List<UserInfo> userInfoList = gson.fromJson(friendList,listType);
            Cookies.getCookies().setUserInfos(userInfoList);
            if (userInfoList != null){
                Message message = handler.obtainMessage();
                message.what = 0x12;
                handler.sendMessage(message);
            }
        }
    }

}
