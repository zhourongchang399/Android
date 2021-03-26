package com.example.helloworld.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.helloworld.R;
import com.example.helloworld.activity.SearchOneActivity;
import com.example.helloworld.adapter.AppStraggerAdapter;
import com.example.helloworld.adapter.FriendStraggerAdapter;
import com.example.helloworld.adapter.SearchStraggerAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserAccount;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.socket.ClientInfo;
import com.example.helloworld.socket.SocketManager;

import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class fragment_addFriend_search extends Fragment {
    Context context;
    RecyclerView recyclerView;
    List<UserInfo> userInfoList;

    public fragment_addFriend_search( List<UserInfo> userInfoList){
        this.userInfoList = userInfoList;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_friend_main,container,false);
        context = view.getContext();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rcv_af_search);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new SearchStraggerAdapter(context,userInfoList));
}


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
