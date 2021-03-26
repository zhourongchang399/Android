package com.example.helloworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.ImageActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    Context context;
    private List<MessageInfo> msgList;
    int width;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo);
        }
    }

    public PhotoAdapter(List<MessageInfo> msgList, Context context,int width) {
        this.msgList = msgList;
        this.context = context;
        this.width = width;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder holder, int position) {
        MessageInfo messageInfo = msgList.get(position);
//        Bitmap bitmap = ImageUtils.base64ToBitmap(messageInfo.getText());
//        bitmap = com.blankj.utilcode.util.ImageUtils.scale(bitmap,width,width);
//        holder.imageView.setImageBitmap(bitmap);
        Glide.with(context).load(new httpPathList().getUrl()+"/"+ messageInfo.getText()).centerCrop().thumbnail(0.1f).override(width,width).into(holder.imageView);
        holder.imageView.setOnClickListener(new onClick(messageInfo.getText(),position));
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class onClick implements View.OnClickListener {
        String base64;
        int pos;
        public onClick(String base64,int pos){
            this.base64 = base64;
            this.pos = pos;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ImageActivity.class);
            ArrayList<String> base64s = new ArrayList<>();
            for (int i = 0;i < msgList.size();i++) {
                if (!msgList.get(i).getType().equals("text")) {
                    base64s.add(msgList.get(i).getText());
                }
            }
            intent.putExtra("position", pos - 1);
            intent.putStringArrayListExtra("base64s",base64s);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity(intent);
        }
    }

}
