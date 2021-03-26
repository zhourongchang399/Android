package com.example.helloworld.model.lisenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public interface actionListener{
        void action();
    }
}
