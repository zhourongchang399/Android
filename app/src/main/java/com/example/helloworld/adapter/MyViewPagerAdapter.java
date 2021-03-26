package com.example.helloworld.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentslist;
    String[] title = new String[]{"聊天","朋友","消息","我的"};

    public MyViewPagerAdapter(FragmentManager fragmentManager, List<Fragment> fragmentslist){
        super(fragmentManager);
        this.fragmentslist = fragmentslist;
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentslist.get(position);

    }

    @Override
    public int getCount() {
        return fragmentslist.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

}
