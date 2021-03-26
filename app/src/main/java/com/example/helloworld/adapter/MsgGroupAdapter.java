package com.example.helloworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.ImageActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.emoji.EmojiUtil;
import com.example.helloworld.utils.DateUtils;
import com.example.helloworld.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MsgGroupAdapter extends RecyclerView.Adapter<MsgGroupAdapter.ViewHolder> {

    Context context;
    private List<MessageInfo> msgList;
    String base64;
    List<UserInfo> userInfoList;
    List<UserInfo> msgUserInfos;
    static String date;
    UserInfo other_userInfo = null;

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout,otherLayout,defect_msg_left_layout;
        LinearLayout rightLayout,hostLayout,defect_msg_right_layout;

        TextView leftMsg;
        TextView rightMsg;
        TextView time;

        ImageView leftImage;
        ImageView rightImage;
        ImageView otherImage;
        ImageView hostImage;
        ImageView defect_msg_left;
        ImageView defect_msg_right;
        GifImageView other_gif,host_gif;

        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout= itemView.findViewById(R.id.left_layout);
            rightLayout= itemView.findViewById(R.id.right_layout);
            leftMsg= itemView.findViewById(R.id.left_msg);
            rightMsg= itemView.findViewById(R.id.right_msg);
            leftImage = itemView.findViewById(R.id.other_face);
            rightImage = itemView.findViewById(R.id.host_face);
            time = itemView.findViewById(R.id.time);
            otherImage = itemView.findViewById(R.id.other_image);
            hostImage = itemView.findViewById(R.id.host_image);
            otherLayout = itemView.findViewById(R.id.other_layout);
            hostLayout = itemView.findViewById(R.id.host_layout);
            other_gif = itemView.findViewById(R.id.other_gif);
            host_gif = itemView.findViewById(R.id.host_gif);
            defect_msg_left = itemView.findViewById(R.id.defect_msg_left);
            defect_msg_right = itemView.findViewById(R.id.defect_msg_right);
            defect_msg_left_layout = itemView.findViewById(R.id.defect_msg_left_layout);
            defect_msg_right_layout = itemView.findViewById(R.id.defect_msg_right_layout);
        }
    }

    public MsgGroupAdapter(List<MessageInfo> msgList, Context context, List<UserInfo> userInfo, List<UserInfo> msgUserInfos) {
        this.msgList = msgList;
        this.context = context;
        this.userInfoList = userInfo;
        this.msgUserInfos = msgUserInfos;
    }

    @Override
    public MsgGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MsgGroupAdapter.ViewHolder holder, int position) {
        if (msgList.size() != 0) {
            holder.defect_msg_left.setVisibility(View.GONE);
            holder.defect_msg_right.setVisibility(View.GONE);
            holder.defect_msg_left_layout.setVisibility(View.GONE);
            holder.defect_msg_right_layout.setVisibility(View.GONE);
            MessageInfo msg = msgList.get(position);
            for (int i = 0; i < msgUserInfos.size(); i++) {
                if(msg.getUserId() == msgUserInfos.get(i).getUserId()) {
                    other_userInfo = msgUserInfos.get(i);
                    break;
                }
            }
            if (position == 0)
                date = msg.getCreateTime();
            String time = msg.getCreateTime().substring(0, 16);
            if (DateUtils.DateBetween(date, msg.getCreateTime())) {
                if (position != 0)
                    holder.time.setVisibility(View.GONE);
                else {
                    holder.time.setVisibility(View.VISIBLE);
                    holder.time.setText(time);
                }
            } else {
                holder.time.setVisibility(View.VISIBLE);
                holder.time.setText(time);
            }
            date = msg.getCreateTime();
            System.out.println(msg.getType());
            if (msg.getType().equals("defect")) {
                int id = Cookies.getCookies().getUserId();
                    if (msg.getUserId() == id) {
                        holder.defect_msg_left_layout.setVisibility(View.GONE);
                        holder.defect_msg_right_layout.setVisibility(View.VISIBLE);
                        holder.otherLayout.setVisibility(View.GONE);
                        holder.hostLayout.setVisibility(View.VISIBLE);
                        holder.rightMsg.setVisibility(View.VISIBLE);
                        holder.leftLayout.setVisibility(View.GONE);
                        holder.rightLayout.setVisibility(View.VISIBLE);
                        holder.rightImage.setVisibility(View.VISIBLE);
                        holder.hostImage.setVisibility(View.GONE);
                        holder.host_gif.setVisibility(View.GONE);
                        holder.defect_msg_left.setVisibility(View.GONE);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + Cookies.getCookies().getImg()).centerCrop().into(holder.rightImage);
                        try {
                            EmojiUtil.handlerEmojiText(holder.rightMsg, msg.getText(), context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        holder.defect_msg_right.setVisibility(View.VISIBLE);
                    } else {
                        holder.defect_msg_left_layout.setVisibility(View.VISIBLE);
                        holder.defect_msg_right_layout.setVisibility(View.GONE);
                        holder.otherLayout.setVisibility(View.VISIBLE);
                        holder.hostLayout.setVisibility(View.GONE);
                        holder.leftMsg.setVisibility(View.VISIBLE);
                        holder.leftLayout.setVisibility(View.VISIBLE);
                        holder.rightLayout.setVisibility(View.GONE);
                        holder.leftImage.setVisibility(View.VISIBLE);
                        holder.otherImage.setVisibility(View.GONE);
                        holder.other_gif.setVisibility(View.GONE);
                        holder.defect_msg_right.setVisibility(View.GONE);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + other_userInfo.getImg()).centerCrop().into(holder.leftImage);
                        try {
                            EmojiUtil.handlerEmojiText(holder.leftMsg, msg.getText(), context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        holder.defect_msg_left.setVisibility(View.VISIBLE);
                    }
                }
                else if (msg.getType().equals("defect_img")) {
                    base64 = msg.getText();
                    int id = Cookies.getCookies().getUserId();
                    Bitmap bitmap = ImageUtils.base64ToBitmap(msg.getText());
                    if (msg.getUserId() == id) {
                        holder.defect_msg_left_layout.setVisibility(View.GONE);
                        holder.defect_msg_right_layout.setVisibility(View.VISIBLE);
                        holder.otherLayout.setVisibility(View.GONE);
                        holder.hostLayout.setVisibility(View.VISIBLE);
                        holder.rightMsg.setVisibility(View.GONE);
                        holder.leftLayout.setVisibility(View.GONE);
                        holder.rightLayout.setVisibility(View.VISIBLE);
                        holder.rightImage.setVisibility(View.VISIBLE);
                        holder.hostImage.setVisibility(View.VISIBLE);
                        holder.host_gif.setVisibility(View.GONE);
                        holder.defect_msg_left.setVisibility(View.GONE);
                        holder.defect_msg_right.setVisibility(View.VISIBLE);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + Cookies.getCookies().getImg()).centerCrop().into(holder.rightImage);
                        Glide.with(context).load(bitmap).centerCrop().into(holder.hostImage);
                        holder.hostImage.setOnClickListener(new onClick(base64, position));
                    }
                }
                else {
                if (msg.getType().equals("text")) {
                    int id = Cookies.getCookies().getUserId();
                    if (msg.getUserId() == id) {
                        holder.otherLayout.setVisibility(View.GONE);
                        holder.hostLayout.setVisibility(View.VISIBLE);
                        holder.rightMsg.setVisibility(View.VISIBLE);
                        holder.leftLayout.setVisibility(View.GONE);
                        holder.rightLayout.setVisibility(View.VISIBLE);
                        holder.rightImage.setVisibility(View.VISIBLE);
                        holder.hostImage.setVisibility(View.GONE);
                        holder.host_gif.setVisibility(View.GONE);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + Cookies.getCookies().getImg()).centerCrop().into(holder.rightImage);
                        try {
                            EmojiUtil.handlerEmojiText(holder.rightMsg, msg.getText(), context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        holder.otherLayout.setVisibility(View.VISIBLE);
                        holder.hostLayout.setVisibility(View.GONE);
                        holder.leftMsg.setVisibility(View.VISIBLE);
                        holder.leftLayout.setVisibility(View.VISIBLE);
                        holder.rightLayout.setVisibility(View.GONE);
                        holder.leftImage.setVisibility(View.VISIBLE);
                        holder.otherImage.setVisibility(View.GONE);
                        holder.other_gif.setVisibility(View.GONE);
                        if (other_userInfo != null)
                            Glide.with(context).load(new httpPathList().getUrl() + "/" + other_userInfo.getImg()).centerCrop().into(holder.leftImage);
                        else
                            holder.leftImage.setBackgroundResource(R.drawable.outgroup);
                        try {
                            EmojiUtil.handlerEmojiText(holder.leftMsg, msg.getText(), context);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (msg.getType().equals("myImage")) {
                    base64 = msg.getText();
                    int id = Cookies.getCookies().getUserId();
                    Bitmap bitmap = ImageUtils.base64ToBitmap(msg.getText());
                    if (msg.getUserId() == id) {
                        holder.otherLayout.setVisibility(View.GONE);
                        holder.hostLayout.setVisibility(View.VISIBLE);
                        holder.rightMsg.setVisibility(View.GONE);
                        holder.leftLayout.setVisibility(View.GONE);
                        holder.rightLayout.setVisibility(View.VISIBLE);
                        holder.rightImage.setVisibility(View.VISIBLE);
                        holder.hostImage.setVisibility(View.VISIBLE);
                        holder.host_gif.setVisibility(View.GONE);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + Cookies.getCookies().getImg()).centerCrop().into(holder.rightImage);
//                        holder.rightImage.setImageBitmap(ImageUtils.base64ToBitmap(Cookies.getCookies().getImg()));
//                        holder.hostImage.setImageBitmap(bitmap);
                        Glide.with(context).load(bitmap).centerCrop().into(holder.hostImage);
//                        Glide.with(context).load(new httpPathList().getUrl()+"/"+msg.getText()).override(450,600).into(holder.hostImage);
                        holder.hostImage.setOnClickListener(new onClick(base64, position));
                    }
                }else if (!msg.getType().equals("gif")) {
                    base64 = msg.getText();
                    int id = Cookies.getCookies().getUserId();
                    if (msg.getUserId() == id) {
                        holder.otherLayout.setVisibility(View.GONE);
                        holder.hostLayout.setVisibility(View.VISIBLE);
                        holder.rightMsg.setVisibility(View.GONE);
                        holder.leftLayout.setVisibility(View.GONE);
                        holder.rightLayout.setVisibility(View.VISIBLE);
                        holder.rightImage.setVisibility(View.VISIBLE);
                        holder.hostImage.setVisibility(View.VISIBLE);
                        holder.host_gif.setVisibility(View.GONE);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + Cookies.getCookies().getImg()).centerCrop().into(holder.rightImage);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + msg.getText()).centerCrop().into(holder.hostImage);
                        holder.hostImage.setOnClickListener(new onClick(base64, position));
                    } else {
                        holder.otherLayout.setVisibility(View.VISIBLE);
                        holder.hostLayout.setVisibility(View.GONE);
                        holder.leftMsg.setVisibility(View.GONE);
                        holder.leftLayout.setVisibility(View.VISIBLE);
                        holder.rightLayout.setVisibility(View.GONE);
                        holder.leftImage.setVisibility(View.VISIBLE);
                        holder.otherImage.setVisibility(View.VISIBLE);
                        holder.other_gif.setVisibility(View.GONE);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + msg.getText()).centerCrop().into(holder.otherImage);
                        Glide.with(context).load(new httpPathList().getUrl() + "/" + other_userInfo.getImg()).centerCrop().into(holder.leftImage);
                        holder.hostImage.setOnClickListener(new onClick(base64, position));
                    }
                }
            }
        }
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
            for (int i = 0;i < msgList.size();i++)
                if (!msgList.get(i).getType().equals("text")) {
                    base64s.add(msgList.get(i).getText());
                }
            intent.putExtra("position", pos - 1);
            intent.putStringArrayListExtra("base64s",base64s);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
