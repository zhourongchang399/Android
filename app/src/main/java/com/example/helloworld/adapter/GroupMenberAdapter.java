package com.example.helloworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.ImageActivity;
import com.example.helloworld.activity.SearchOneActivity;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class GroupMenberAdapter extends RecyclerView.Adapter<GroupMenberAdapter.ViewHolder> {

    Context context;
    List<UserInfo> userInfoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    public GroupMenberAdapter(List<UserInfo> userInfoList, Context context) {
        this.userInfoList = userInfoList;
        this.context = context;
    }

    @Override
    public GroupMenberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menber_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupMenberAdapter.ViewHolder holder, int position) {
        UserInfo userInfo = userInfoList.get(position);
//        Bitmap bitmap = ImageUtils.base64ToBitmap(userInfo.getImg());
//        bitmap = com.blankj.utilcode.util.ImageUtils.scale(bitmap,50,50);
        Glide.with(context).load(new httpPathList().getUrl()+"/"+userInfo.getImg()).centerCrop().into(holder.imageView);
//        holder.imageView.setImageBitmap(bitmap);
        holder.textView.setText(userInfo.getName());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }

}
