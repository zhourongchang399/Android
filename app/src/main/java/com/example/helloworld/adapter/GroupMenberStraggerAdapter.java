package com.example.helloworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.FriendCardActivity;
import com.example.helloworld.activity.SearchOneActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.utils.ImageUtils;

import java.text.CollationKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GroupMenberStraggerAdapter extends RecyclerView.Adapter<GroupMenberStraggerAdapter.ViewHolder> {
    private Context context;
    List<UserInfo> userInfoList;
    ImageView userImage;
    static boolean isOption;
    List<UserInfo> friendList;
    List<Integer> optioned;
    Boolean aBoolean;
    OnClickListener onClickListener;

    public GroupMenberStraggerAdapter(Context context, List<UserInfo> userInfoList,Boolean aBoolean){
        this.context = context;
        this.userInfoList = userInfoList;
        isOption = false;
        friendList = Cookies.getCookies().getUserInfos();
        optioned = new ArrayList<>();
        this.aBoolean = aBoolean;
    }

    public GroupMenberStraggerAdapter(Context context, List<UserInfo> userInfoList,Boolean aBoolean,OnClickListener onClickListener){
        this.context = context;
        this.userInfoList = userInfoList;
        isOption = false;
        friendList = Cookies.getCookies().getUserInfos();
        optioned = new ArrayList<>();
        this.aBoolean = aBoolean;
        this.onClickListener = onClickListener;
    }
    @NonNull
    @Override
    public GroupMenberStraggerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupMenberStraggerAdapter.StraggerViewHolder(LayoutInflater.from(context).inflate(R.layout.new_group_menber_item,parent,false));

    }

    @Override
    public int getItemViewType(int position) {
        if(position != 0)
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMenberStraggerAdapter.ViewHolder holder, int position) {
        holder.action(position);
    }


    @Override
    public int getItemCount() {
        // 返回足够多的item
        if (aBoolean)
            return friendList.size();
        else
            return userInfoList.size();
    }

    public class StraggerViewHolder extends ViewHolder {
        TextView username;
        LinearLayout linearLayout;
        UserInfo userInfo;
        ImageView status;
        public StraggerViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_name);
            linearLayout =itemView.findViewById(R.id.item_one);
            userImage = itemView.findViewById(R.id.userImage);
            status = itemView.findViewById(R.id.status);
        }

        @Override
        public void action(int position) {
            if (aBoolean){
                status.setVisibility(View.VISIBLE);
            userInfo = friendList.get(position);
            username.setText(userInfo.getName());
                Glide.with(context).load(new httpPathList().getUrl()+"/"+userInfo.getImg()).into(userImage);
//            userImage.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getImg()));
                if (userInfoList.size() != 0) {
                    for (int i = 0; i < userInfoList.size(); i++) {
                        if (userInfoList.get(i).getUserId() == userInfo.getUserId()) {
                            linearLayout.setEnabled(false);
                            status.setImageResource(R.drawable.recycle);
                        } else {
                            linearLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!isOption) {
                                        status.setImageResource(R.drawable.recycle);
                                        isOption = true;
                                        optioned.add(userInfo.getUserId());
                                    } else {
                                        status.setImageResource(R.drawable.recycle2);
                                        isOption = false;
                                        Iterator<Integer> iterator = optioned.iterator();
                                        while (iterator.hasNext()) {
                                            if (iterator.next() == userInfo.getUserId()) {
                                                iterator.remove();
                                            }
                                        }
                                    }
                                    Cookies.getCookies().setInts(optioned);
                                }
                            });
                        }
                    }
                } else {
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isOption) {
                                status.setImageResource(R.drawable.recycle);
                                isOption = true;
                                optioned.add(userInfo.getUserId());
                            } else {
                                status.setImageResource(R.drawable.recycle2);
                                isOption = false;
                                Iterator<Integer> iterator = optioned.iterator();
                                while (iterator.hasNext()) {
                                    if (iterator.next() == userInfo.getUserId()) {
                                        iterator.remove();
                                    }
                                }
                            }
                            Cookies.getCookies().setInts(optioned);
                        }
                    });
                }
            }
            else if (!aBoolean) {
                userInfo = userInfoList.get(position);
                username.setText(userInfo.getName());
                Glide.with(context).load(new httpPathList().getUrl()+"/"+userInfo.getImg()).into(userImage);
//                userImage.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getImg()));
                status.setVisibility(View.GONE);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.OnClick(Integer.toString(userInfo.getUserId()));
                    }
                });
            }
        }
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public abstract void action(int position);
    }

    public interface OnClickListener{
        public void OnClick(String i);
    }

}
