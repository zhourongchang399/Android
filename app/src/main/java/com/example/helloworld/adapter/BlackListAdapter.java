package com.example.helloworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.FriendCardActivity;
import com.example.helloworld.activity.SearchOneActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.ui.AlterDialogManager;
import com.example.helloworld.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> {
    private Context context;
    List<UserInfo> userInfoList;
    ImageView userImage;
    OnClickListener onClickListener;
    UserInfo userInfo;
    Handler handler;

    public BlackListAdapter(Context context, List<UserInfo> userInfoList,OnClickListener onClickListener){
        this.context = context;
        this.userInfoList = userInfoList;
        this.onClickListener = onClickListener;
    }

    public BlackListAdapter(Context context, List<UserInfo> userInfoList){
        this.context = context;
        this.userInfoList = userInfoList;
    }
    @NonNull
    @Override
    public BlackListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("updateBlackList"));

                }
            }
        };
        return new BlackListAdapter.StraggerViewHolder(LayoutInflater.from(context).inflate(R.layout.friend_item,parent,false));
    }

    @Override
    public int getItemViewType(int position) {
        if(position != 0)
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull BlackListAdapter.ViewHolder holder, int position) {
        holder.action(position);
    }


    @Override
    public int getItemCount() {
        // 返回足够多的item
        return userInfoList.size();
    }

    public class StraggerViewHolder extends ViewHolder {
        TextView username;
        LinearLayout linearLayout;
        public StraggerViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            linearLayout =itemView.findViewById(R.id.item_one);
            userImage = itemView.findViewById(R.id.userImage);
        }

        @Override
        public void action(int position){
            userInfo = userInfoList.get(position);
            username.setText(userInfo.getName());
            Glide.with(context).load(new httpPathList().getUrl()+"/"+userInfo.getImg()).into(userImage);
//            userImage.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getImg()));
            linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlterDialogManager(v.getRootView().getContext(), "是否从黑名单是移除该用户？", new AlterDialogManager.onClockListner() {
                        @Override
                        public void onClockListner(int i) {
                            if (i == 0 ){
                                new MyThread().start();
                            }
                        }
                    }).show();
                    return true;
                }
            });
        }
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public abstract void action(int position);
    }

    public interface OnClickListener{
        public void OnClick();
    }

    public class MyThread extends Thread {

        @Override
        public void run() {
            String result = HttpUtil.initJson()
                    .url(new httpPathList().getPath()[32])
                    .addParam("hostId", Integer.toString(Cookies.getCookies().getUserId()))
                    .addParam("userId", Integer.toString(userInfo.getUserId()))
                    .method(HttpUtil.RequestMethod.POST)
                    .invoke();
            Message message;
            message = handler.obtainMessage();
            message.what = 0x11;
            handler.sendMessage(message);
        }
    }

}
