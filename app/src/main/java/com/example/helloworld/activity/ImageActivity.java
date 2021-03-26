package com.example.helloworld.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ImageUtils;
import com.example.helloworld.R;
import com.example.helloworld.adapter.PhotoFragmentAdapter;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.view.PhotoFragment;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends MyAppCompatActivity {
    private List<Fragment> fragmentList = new ArrayList<>();
    List<String> base64s = new ArrayList<>();
    Intent intent;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ViewPager viewpager = findViewById(R.id.viewpager);
        intent = getIntent();
        base64s = intent.getStringArrayListExtra("base64s");
        position = intent.getIntExtra("position",0);
        for (int i = 0;i < base64s.size();i++)
            fragmentList.add(PhotoFragment.newInstance(base64s.get(i)));

        PhotoFragmentAdapter adapter = new PhotoFragmentAdapter(getSupportFragmentManager(),fragmentList);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpager.setCurrentItem(position+1);
    }

}
