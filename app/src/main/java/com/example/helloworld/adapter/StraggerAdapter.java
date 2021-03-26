package com.example.helloworld.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.helloworld.R;
import com.example.helloworld.data.ImageString;

import java.util.Random;


public class StraggerAdapter extends RecyclerView.Adapter<StraggerAdapter.StraggerViewHopler> {
    private Context context;
    int mData;
    ImageString imageString;
    String string;
    String s = "R.drawable.scroll_9";
    int anInt;
    public StraggerAdapter(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public StraggerAdapter.StraggerViewHopler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StraggerViewHopler(LayoutInflater.from(context).inflate(R.layout.straggerview_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StraggerAdapter.StraggerViewHopler holder, int position) {
        ImageView imageView = holder.imageView;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        float itemWidth = ScreenUtils.getScreenWidth() / 2;
        layoutParams.width = (int)itemWidth;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (isConnectIsNomarl()) {
            imageString = new ImageString();
            Random random = new Random();
            anInt = (int) Math.floor(Math.random() * Math.floor(29));
            int itemHeight = random.nextInt(1000) + 600;
            string = imageString.getString()[anInt];
            Glide.with(context).load(string).override((int) itemWidth,itemHeight).into(imageView);
        }
        else
            imageView.setImageResource(R.drawable.scroll_9);
    }


    @Override
    public int getItemCount() {
        // 返回足够多的item
        return 23;
    }

    public class StraggerViewHopler extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private LinearLayout linearLayout;
        public StraggerViewHopler(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.stragger_image);
            linearLayout = itemView.findViewById(R.id.image_item);
        }
    }
    public interface OnClickListener{
        public void OnClick(int pos);
    }

    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return true;
        } else {
            return false;
        }
    }
}
