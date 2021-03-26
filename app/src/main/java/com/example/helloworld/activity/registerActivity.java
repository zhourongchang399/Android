package com.example.helloworld.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.example.helloworld.R;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.view.fragment_register;
import com.example.helloworld.view.fragment_forgive;
import com.example.helloworld.view.fragment_forgive_change;
import com.example.helloworld.view.fragment_forgive_win;

public class registerActivity extends MyAppCompatActivity implements fragment_forgive.testDataCallback,fragment_forgive_change.testDataCallback2,fragment_forgive_win.testDataCallback3 {
    Toolbar toolbar2;
    String action,username;

    fragment_register fragment_register;
    fragment_forgive fragment_forgive;
    fragment_forgive_change fragment_forgive_change;
    fragment_forgive_win fragment_forgive_win;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_view);
        username = Cookies.getCookies().getUsername();
        action = getIntent().getStringExtra("action");
        if (action.equals("register")) {
            fragment_register = new fragment_register();
            getSupportFragmentManager().beginTransaction().add(R.id.register_view, fragment_register,"register").commit();
        }
        else
            if (action.equals("forgive"))
        {
            fragment_forgive = new fragment_forgive();
            getSupportFragmentManager().beginTransaction().replace(R.id.register_view, fragment_forgive,"forgive").commit();
        }
        toolbar2 = findViewById(R.id.toolbar2);
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerActivity.this.finish();
            }
        });
        View view = getLayoutInflater().inflate(R.layout.activity_register_view,null);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (event.getAction()){
                    case KeyEvent.KEYCODE_BACK:
                            getSupportFragmentManager().popBackStack();
                            break;
                }
                return false;
            }
        });
    }

    @Override
    public void testData(String phone) {
        fragment_forgive_change = new fragment_forgive_change(phone);
        getSupportFragmentManager().beginTransaction().replace(R.id.register_view, fragment_forgive_change).addToBackStack(null).commit();
    }

    @Override
    public void testData2() {
        fragment_forgive_win = new fragment_forgive_win();
        getSupportFragmentManager().beginTransaction().replace(R.id.register_view, fragment_forgive_win).addToBackStack(null).commit();
    }

    @Override
    public void testData3() {
        this.finish();
    }
}
