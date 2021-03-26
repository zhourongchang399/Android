package com.example.helloworld.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.helloworld.R;

public class AddFriendDialogManager extends AlertDialog {
    Button button,button1;
    EditText fa_add_friend_text;
    int receiveId;
    Context context;
    String msg;
    onClockListner onClockListner;
    public AddFriendDialogManager(Context context, int receiveId, String msg, onClockListner onClockListner) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.receiveId = receiveId;
        this.onClockListner = onClockListner;
    }

    public AddFriendDialogManager(Context context, String msg, onClockListner onClockListner) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.onClockListner = onClockListner;
    }

    protected AddFriendDialogManager(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected AddFriendDialogManager(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_dialog);
        button = findViewById(R.id.on);
        button1 = findViewById(R.id.off);
        fa_add_friend_text = findViewById(R.id.fa_add_friend_text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClockListner.onClockListner(0,fa_add_friend_text.getText().toString());
                dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface onClockListner {
        public void onClockListner(int i,String text);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


}
