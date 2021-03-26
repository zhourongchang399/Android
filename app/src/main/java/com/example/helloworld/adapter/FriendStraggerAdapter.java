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
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.utils.ImageUtils;

import java.util.List;


public class FriendStraggerAdapter extends RecyclerView.Adapter<FriendStraggerAdapter.ViewHolder> {
    private Context context;
    List<UserInfo> userInfoList;
    ImageView userImage;

    public FriendStraggerAdapter(Context context,List<UserInfo> userInfoList){
        this.context = context;
        this.userInfoList = userInfoList;
    }
    @NonNull
    @Override
    public FriendStraggerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendStraggerAdapter.StraggerViewHolder(LayoutInflater.from(context).inflate(R.layout.friend_item,parent,false));

    }

    @Override
    public int getItemViewType(int position) {
        if(position != 0)
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendStraggerAdapter.ViewHolder holder, int position) {
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
        UserInfo userInfo;
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
            Glide.with(context).load(new httpPathList().getUrl()+"/"+userInfo.getImg()).centerCrop().into(userImage);
//            userImage.setImageBitmap(ImageUtils.base64ToBitmap(userInfo.getImg()));
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,FriendCardActivity.class);
                    intent.putExtra("UserInfo",userInfo);
                    intent.putExtra("ifSelf","false");
                    context.startActivity(intent);
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
        public void OnClick(Bundle bundle);
    }

}
