package com.example.helloworld.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.AddFriendActivity;
import com.example.helloworld.activity.FriendCardActivity;
import com.example.helloworld.activity.SearchOneActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.ImageString;
import com.example.helloworld.data.UserAccount;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.ClientInfo;
import com.example.helloworld.ui.MyDialogManager;
import com.example.helloworld.utils.ImageUtils;

import java.util.List;


public class SearchStraggerAdapter extends RecyclerView.Adapter<SearchStraggerAdapter.ViewHolder> {
    private Context context;
    List<UserInfo> userInfoList;

    public SearchStraggerAdapter(Context context,List<UserInfo> userInfoList){
        this.context = context;
        this.userInfoList = userInfoList;
    }

    @NonNull
    @Override
    public SearchStraggerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StraggerViewHolder(LayoutInflater.from(context).inflate(R.layout.friend_content_item,parent,false));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchStraggerAdapter.ViewHolder holder, int position) {
        holder.action(position);
    }


    @Override
    public int getItemCount() {
        // 返回足够多的item
        return userInfoList.size();
    }

    public class StraggerViewHolder extends ViewHolder{
        ImageView userImage;
        TextView name;
        TextView userName;
        LinearLayout linearLayout;
        UserInfo userInfo;
        public StraggerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            userName = itemView.findViewById(R.id.userName);
            linearLayout = itemView.findViewById(R.id.item_one);
            userImage = itemView.findViewById(R.id.userImage);
        }

        @Override
        public void action(int position){
            userInfo = userInfoList.get(position);
                name.setText(userInfo.getName());
                userName.setText(userInfo.getUsername());
            Glide.with(context).load(new httpPathList().getUrl()+"/"+userInfo.getImg()).centerCrop().into(userImage);
//            userImage.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getImg()));
                List<UserInfo> userInfos = Cookies.getCookies().getUserInfos();
                if (userInfo.getUserId() == Cookies.getCookies().getUserId()){
                    linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, FriendCardActivity.class);
                            intent.putExtra("UserInfo", userInfo);
                            intent.putExtra("ifSelf","true");
                            context.startActivity(intent);
                        }
                    });
                }
                else {
                    if (userInfos.size() == 0) {
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, SearchOneActivity.class);
                                intent.putExtra("UserInfo", userInfo);
                                intent.putExtra("ifSelf", "false");
                                context.startActivity(intent);
                            }
                        });
                    } else {
                        if (userInfoList.size() != 0) {
                            for (int j = 0; j < userInfos.size(); j++) {
                                UserInfo userInfoFri = userInfos.get(j);
                                if (userInfo.getUserId() == userInfoFri.getUserId()) {
                                    linearLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, FriendCardActivity.class);
                                            intent.putExtra("UserInfo", userInfo);
                                            intent.putExtra("ifSelf", "false");
                                            context.startActivity(intent);
                                        }
                                    });
                                    break;
                                } else {
                                    linearLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, SearchOneActivity.class);
                                            intent.putExtra("UserInfo", userInfo);
                                            intent.putExtra("ifSelf", "false");
                                            context.startActivity(intent);
                                        }
                                    });
                                }
                            }
                        } else {
                            new MyDialogManager(context, "没有该用户！", 2).show();
                        }
                    }
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
        public void OnClick(Bundle bundle);
    }

}
