package com.example.helloworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.example.helloworld.activity.ChatMessageActivity;
import com.example.helloworld.activity.ChatMessageGroupActivity;
import com.example.helloworld.activity.FriendCardActivity;
import com.example.helloworld.activity.SearchOneActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.Dialog;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.AlterDialogManager;
import com.example.helloworld.ui.emoji.EmojiUtil;
import com.example.helloworld.utils.ImageUtils;
import com.othershe.combinebitmap.CombineBitmap;
import com.othershe.combinebitmap.layout.WechatLayoutManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class chatDialogAdapter extends RecyclerView.Adapter<chatDialogAdapter.ViewHolder> {

    Context context;
    String username;
    List<Dialog> Dialogs;
    String menber;
    UserInfo userInfo = null;

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

    public chatDialogAdapter(Context context,List<Dialog> dialogList) {
        this.context = context;
        username = Cookies.getCookies().getUsername();
        Dialogs = dialogList;
    }

    @Override
    public chatDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(chatDialogAdapter.ViewHolder holder, int position) {
        if (Dialogs.size() != 0) {
            final Dialog dialog = Dialogs.get(position);
            if (dialog.getType().equals("people")) {
                Glide.with(context).load(new httpPathList().getUrl()+"/"+dialog.getUserInfo().getImg()).centerCrop().into(holder.owner_image);
//                holder.owner_image.setImageBitmap(ImageUtils.base64ToBitmap(dialog.getUserInfo().getImg()));
                holder.linearLayout.setOnClickListener(new onClick(dialog.getUserInfo()));
                holder.owner_image.setOnClickListener(new onClick(dialog.getUserInfo()));
                holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlterDialogManager(context, "删除后,清空聊天记录！", new AlterDialogManager.onClockListner() {
                            @Override
                            public void onClockListner(int i) {
                                if (i == 0)
                                    deleteDialog(Cookies.getCookies().getUserId(), dialog.getUserInfo().getUserId());
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("updateMessage"));
                            }
                        }).show();
                        return true;
                    }
                });
                holder.title.setText(dialog.getUserInfo().getName());
            } else if (dialog.getType().equals("group")){
                String[] urls = new String[dialog.getUserInfos().size()];
                for (int i = 0; i < dialog.getUserInfos().size(); i++) {
                    if (dialog.getLastMsg() != null)
                    if (dialog.getUserInfos().get(i).getUserId() == dialog.getLastMsg().getUserId())
                        userInfo = dialog.getUserInfos().get(i);
                    else
                        userInfo = null;
                    urls[i] = new httpPathList().getUrl() + "/" + dialog.getUserInfos().get(i).getImg();
                }
                CombineBitmap.init(context)
                        .setLayoutManager(new WechatLayoutManager()) // 必选， 设置图片的组合形式，支持WechatLayoutManager、DingLayoutManager
                        .setSize(16) // 必选，组合后Bitmap的尺寸，单位dp
                        .setGap(1)
                        .setUrls(urls)
                        .setGapColor(Color.parseColor("#000000"))
                        .setImageView(holder.owner_image) // 直接设置要显示图片的ImageView
                        .build();
                holder.title.setText(dialog.getName());
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatMessageGroupActivity.open(context,dialog.getUserInfos(),dialog.getName(),dialog.getId());
                    }
                });
                holder.owner_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatMessageGroupActivity.open(context,dialog.getUserInfos(),dialog.getName(),dialog.getId());
                    }
                });
                holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlterDialogManager(context, "删除后,清空聊天记录！", new AlterDialogManager.onClockListner() {
                            @Override
                            public void onClockListner(int i) {
                                if (i == 0)
                                    deleteDialogGroup(Cookies.getCookies().getUserId(), Integer.parseInt(dialog.getId()));
                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("updateMessage"));
                            }
                        }).show();
                        return true;
                    }
                });
            }
            if (dialog.getLastMsg() != null) {
                if (dialog.getType().equals("group"))
                    if (userInfo != null)
                        menber = userInfo.getName() +": ";
                    else
                        menber = "";
                else
                    menber = "";
                holder.time.setText(dialog.getLastMsg().getCreateTime().substring(5));
                if (!dialog.getLastMsg().getType().equals("text"))
                    holder.msg.setText(menber + "[图片]");
                else {
                    try {
                        String content = dialog.getLastMsg().getText().replace("\n", " ");
                        if (content.length() > 20) {
                            content = content.substring(0, 20);
                            content = content.concat("...");
                        }
                        EmojiUtil.handlerEmojiText(holder.msg, menber + content, context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                holder.time.setText(null);
                holder.msg.setText(null);
            }
            if (dialog.getUnreadCount() != 0) {
                holder.count.setVisibility(View.VISIBLE);
                holder.count.setText(Integer.toString(dialog.getUnreadCount()));
            } else if (dialog.getUnreadCount() == 0) {
                holder.count.setVisibility(View.GONE);
                holder.count.setText(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return Dialogs.size();
    }

    public interface clickOtherButton{
        public void OnClick(Bundle bundle);
    }

    public void addToStar(MessageInfo messageInfo){

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
                    ChatMessageActivity.open(context,userInfo);
                    break;
                case R.id.owen_image:
                    Intent intent = new Intent(context, FriendCardActivity.class);
                    intent.putExtra("UserInfo",userInfo);
                    intent.putExtra("ifSelf","false");
                    context.startActivity(intent);
            }
        }
    }

    public void deleteDialog(final int senderId, final int receiveId){
        final int host = senderId;
        final int other = receiveId;
        new Thread(){
            @Override
            public void run() {
                super.run();
                HttpUtil.initJson()
                        .url(new httpPathList().getPath()[12])
                        .addParam("host",Integer.toString(host))
                        .addParam("other",Integer.toString(other))
                        .method(HttpUtil.RequestMethod.POST)
                        .invoke();
            }
        }.start();
    }

    public void deleteDialogGroup(final int userId, final int group){
        new Thread(){
            @Override
            public void run() {
                super.run();
                HttpUtil.initJson()
                        .url(new httpPathList().getPath()[44])
                        .addParam("userId",Integer.toString(userId))
                        .addParam("group",Integer.toString(group))
                        .method(HttpUtil.RequestMethod.POST)
                        .invoke();
            }
        }.start();
    }

}
