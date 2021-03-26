package com.example.helloworld.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.helloworld.R;

public class uploadDialogManager extends AlertDialog {
    Button button,button1;
    TextView textView;
    int receiveId;
    Context context;
    String msg;
    onClockListner onClockListner;
    public uploadDialogManager(Context context, int receiveId, String msg, onClockListner onClockListner) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.receiveId = receiveId;
        this.msg = msg;
        this.onClockListner = onClockListner;
    }

    protected uploadDialogManager(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected uploadDialogManager(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_dialog);
    }

    public interface onClockListner {
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
