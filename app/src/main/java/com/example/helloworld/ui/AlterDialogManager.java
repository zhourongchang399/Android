package com.example.helloworld.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.helloworld.R;

public class AlterDialogManager extends AlertDialog {
    Button button,button1;
    TextView textView;
    int receiveId;
    Context context;
    String msg;
    onClockListner onClockListner;
    public AlterDialogManager(Context context, int receiveId, String msg, onClockListner onClockListner) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.receiveId = receiveId;
        this.msg = msg;
        this.onClockListner = onClockListner;
    }

    public AlterDialogManager(Context context, String msg, onClockListner onClockListner) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.msg = msg;
        this.onClockListner = onClockListner;
    }

    protected AlterDialogManager(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected AlterDialogManager(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alter_dialog);
        button = findViewById(R.id.on);
        button1 = findViewById(R.id.off);
        textView = findViewById(R.id.message);
        textView.setText(msg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClockListner.onClockListner(0);
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
        public void onClockListner(int i);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }


}
