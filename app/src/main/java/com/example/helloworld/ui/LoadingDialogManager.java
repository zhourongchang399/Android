package com.example.helloworld.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworld.R;

public class LoadingDialogManager extends Dialog {
    ImageView loading_Image;
    public LoadingDialogManager(@NonNull Context context) {
        super(context,R.style.MyDialog);
    }

    public LoadingDialogManager(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialogManager(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        loading_Image = findViewById(R.id.loading_imageView);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anim);
        anim.setFillAfter(true);//设置旋转后停止
        loading_Image.startAnimation(anim);
    }
}
