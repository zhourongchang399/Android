package com.example.helloworld.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.helloworld.R;

public class MyDialogManager extends Dialog {
    TextView textView;
    ImageView imageView;
    String string;
    int i;
    public MyDialogManager(@NonNull Context context,String s,int i) {
        super(context,R.style.MyDialog);
        this.string = s;
        this.i = i;
    }

    public MyDialogManager(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyDialogManager(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mystyle_dialog);
        textView = this.findViewById(R.id.mydialog_content);
        textView.setText(string);
        imageView = this.findViewById(R.id.image_dialog);
        setIcon();
    }

    public void setIcon(){
        if (i == 1)
            imageView.setBackgroundResource(R.drawable.checkbox_pre);
        else
            imageView.setBackgroundResource(R.drawable.worry);
    }
}
