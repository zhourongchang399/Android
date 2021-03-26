package com.example.helloworld.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.helloworld.R;
import com.example.helloworld.ui.ImageViewManager;
import com.example.helloworld.adapter.ContentStraggerAdapter;

import static android.content.ContentValues.TAG;


public class AppStraggerAdapter extends RecyclerView.Adapter<AppStraggerAdapter.ViewHolder> {
    private Context context;
    private int mData,width = 0;
    public AppStraggerAdapter(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public AppStraggerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0)
            return new StraggerViewHolder2(LayoutInflater.from(context).inflate(R.layout.bg_top,parent,false));
        else
        return new StraggerViewHolder(LayoutInflater.from(context).inflate(R.layout.appstraggerview_item,parent,false));

    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull AppStraggerAdapter.ViewHolder holder, int position) {
//         ImageViewManager imageView;
//         ImageView bg_top;
//         TextView textView_user_name,textView_user_content;
//        View view = holder.view;
//        bg_top = view.findViewById(R.id.bg_top);
//        textView_user_name = view.findViewById(R.id.user_name);
//        textView_user_content = view.findViewById(R.id.user_content);
//        imageView = view.findViewById(R.id.user_face);
//        if (position == 0) {
//            bg_top.setBackgroundResource(R.drawable.scroll_1);
//        }
//        if (position % 2 == 0){
//            imageView.setBackgroundResource(R.drawable.scroll_3);
//        }
//        else {
//            imageView.setBackgroundResource(R.drawable.scroll_1);
//        }
//        textView_user_content.setText(R.string.large_text);
//        textView_user_name.setText(R.string.app_name);

        holder.action(position);
    }


    @Override
    public int getItemCount() {
        // 返回足够多的item
        return 9;
    }

    public class StraggerViewHolder extends ViewHolder{
        ImageViewManager imageView;
        TextView textView_user_name,textView_user_content;
        RecyclerView recyclerView;
        public StraggerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_user_name = itemView.findViewById(R.id.user_name);
            textView_user_content = itemView.findViewById(R.id.user_content);
            imageView = itemView.findViewById(R.id.user_face);
            recyclerView = itemView.findViewById(R.id.rcv_user_content_main);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
            ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    width = recyclerView.getWidth();
                }
            });
    }

        @Override
        public void action(int position) {
            textView_user_content.setOnClickListener(new View.OnClickListener() {
                boolean state = false;
                @Override
                public void onClick(View v) {
                    if (!state) {
                        textView_user_content.setMaxLines(10);
                        state = true;
                    }
                    else{
                        textView_user_content.setMaxLines(6);
                        state = false;
                    }
                }
            });
                imageView.setBackgroundResource(R.drawable.scroll_2);
            textView_user_content.setText(R.string.large_text);
            textView_user_name.setText(R.string.app_name);
            if (width != 0)
            recyclerView.setAdapter(new ContentStraggerAdapter(context,width));
        }
    }

    public class StraggerViewHolder2 extends ViewHolder{
        ImageView bg_top;
        public StraggerViewHolder2(@NonNull View itemView) {
            super(itemView);
            bg_top = itemView.findViewById(R.id.bg_top);
        }


        @Override
        public void action(int position) {
            bg_top.setBackgroundResource(R.drawable.scroll_1);
        }
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public abstract void action(int position);
    }

    public interface OnClickListener{
        public void OnClick(int pos);
    }
}
