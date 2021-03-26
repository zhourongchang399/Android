package com.example.helloworld.ui;

import android.os.CountDownTimer;
import android.widget.Button;

public class TimeCount extends CountDownTimer {
    private Button tvCode;

    public TimeCount(long millisInFuture, long countDownInterval, Button tv) {
        super(millisInFuture, countDownInterval);
        this.tvCode = tv;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        tvCode.setText(millisUntilFinished / 1000 + "秒");
        tvCode.setClickable(false);
    }

    @Override
    public void onFinish() {
        tvCode.setText("发送验证码");
        tvCode.setClickable(true);
    }
}
