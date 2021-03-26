package com.example.helloworld.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;

public class ContentStraggerAdapter extends RecyclerView.Adapter<ContentStraggerAdapter.viewHolder> {
    private Context context;
    RecyclerView recyclerView;
    private int width,height;
    int image_width ;
    public ContentStraggerAdapter(Context context,int width){
        this.context = context;
        image_width = (int)((width / 3));
    }

    @NonNull
    @Override
    public ContentStraggerAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.appstraggerview_image,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContentStraggerAdapter.viewHolder holder, int position) {
        holder.action(position);
//        holder.setSize();
    }

    @Override
    public int getItemCount() {
        return 9;
    }


    public abstract class viewHolder extends RecyclerView.ViewHolder{
        public viewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public abstract void action(int position);
//        public abstract void  setSize();
    }

    public class ImageViewHolder extends viewHolder{
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.content_image);
            setSize();
        }
        @Override
        public void action(int position) {
            imageView.setBackgroundResource(R.drawable.scroll_1);
            imageView.setOnClickListener(new onClick(R.drawable.scroll_1));
        }
        public void setSize(){
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = image_width - 12;
            layoutParams.height = image_width - 12;
            imageView.setLayoutParams(layoutParams);
        }
    }
//        {
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//                    alertDialog.setView(itemView);
//                    alertDialog.setCancelable(true);
//                    alertDialog.show();
//                }
//            });
//        }
//    }

    public class VideoHolder extends viewHolder{
        VideoView videoView;
        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.content_video);
        }
        @Override
        public void action(int position) {

        }

//        @Override
//        public void setSize() {
//
//        }
    }

    public class onClick implements View.OnClickListener {
        ImageView imageView;
        int id;
        public onClick(int id){
            this.id = id;
        }
        @Override
        public void onClick(View v) {
            View view = LayoutInflater.from(context).inflate(R.layout.click_image_big,null);
            imageView = view.findViewById(R.id.image_click);
            imageView.setBackgroundResource(id);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setCancelable(true);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public class myThread extends Thread{
        int i;
        ImageView imageView;

        public myThread(int i,ImageView imageView) {
            this.i = i;
            this.imageView = imageView;
        }
        @Override
        public void run() {
            imageView.setBackgroundResource(R.drawable.scroll_1);
            imageView.setOnClickListener(new onClick(R.drawable.scroll_1));
        }
    }
}
