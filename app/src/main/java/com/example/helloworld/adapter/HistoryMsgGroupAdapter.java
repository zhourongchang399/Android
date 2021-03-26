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

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.SearchOneActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.emoji.EmojiUtil;
import com.example.helloworld.utils.ImageUtils;

import java.io.IOException;
import java.util.List;

public class HistoryMsgGroupAdapter extends RecyclerView.Adapter<HistoryMsgGroupAdapter.ViewHolder> {

    private List<MessageInfo> msgList;
    Context context;
    List<UserInfo> userInfoList;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView owner_image;
        TextView title,msg,time,count;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            title= itemView.findViewById(R.id.chatTitle);
            msg= itemView.findViewById(R.id.chatLastMsg);
            time= itemView.findViewById(R.id.time);
            count= itemView.findViewById(R.id.count);
            owner_image = itemView.findViewById(R.id.owen_image);
            linearLayout = itemView.findViewById(R.id.alllayout);
        }
    }

    public HistoryMsgGroupAdapter(List<MessageInfo> msgList, Context context, List<UserInfo> userInfoList) {
        this.context = context;
        this.msgList = msgList;
        this.userInfoList = userInfoList;
    }

    @Override
    public HistoryMsgGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryMsgGroupAdapter.ViewHolder holder, int position) {
       final MessageInfo messageInfo = msgList.get(position);
       holder.count.setVisibility(View.GONE);
       UserInfo userInfo = null;
        for (int i = 0; i < userInfoList.size(); i++) {
            if (messageInfo.getUserId() == userInfoList.get(i).getUserId())
                userInfo = userInfoList.get(i);
        }
        Glide.with(context).load(new httpPathList().getUrl()+"/"+userInfo.getImg()).into(holder.owner_image);
//            holder.owner_image.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getImg()));
            holder.title.setText(userInfo.getName());
           holder.time.setText(messageInfo.getCreateTime());
           if (messageInfo.getType().equals("text")) {
               String content = messageInfo.getText().replace("\n"," ");
               if (content.length() > 20) {
                   content = content.substring(0,20);
                   content = content.concat("...");
               }
               try {
                   EmojiUtil.handlerEmojiText(holder.msg, content, context);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           else
               holder.msg.setText("[图片]");
       holder.linearLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent("hisMsgPos");
               intent.putExtra("hisMsg",messageInfo);
               LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public interface clickOtherButton{
        public void OnClick(Bundle bundle);
    }

    public void addToStar(MessageInfo messageInfo){

    }


}
