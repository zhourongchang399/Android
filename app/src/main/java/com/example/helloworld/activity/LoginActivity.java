package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.helloworld.R;
import com.example.helloworld.adapter.StraggerAdapter;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.ConnectServer;
import com.example.helloworld.socket.SocketManager;
import com.example.helloworld.socket.chatSocket;
import com.example.helloworld.ui.autoScrollViewManager;
import com.othershe.combinebitmap.CombineBitmap;

import java.io.IOException;
import java.net.Socket;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;


public class LoginActivity extends MyAppCompatActivity {
    private RecyclerView recyclerView;
    TextView bt_login,bt_pass,bt_login_other;
    Toolbar toolbar;
    Button register,forgive;
    StraggerAdapter adapter;
    com.example.helloworld.ui.autoScrollViewManager autoScrollViewManager;
    RecyclerView.State state;
    ImageView login_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bt_login = findViewById(R.id.bt_login_wx);
        register = findViewById(R.id.bt_register);
        forgive = findViewById(R.id.bt_forgive);
        toolbar = findViewById(R.id.toolbar);
        bt_pass = findViewById(R.id.bt_pass);
        bt_login_other = findViewById(R.id.bt_login_other);
        login_image = findViewById(R.id.login_image);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.rc);
        autoScrollViewManager = new autoScrollViewManager(2,StaggeredGridLayoutManager.VERTICAL);
        autoScrollViewManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(autoScrollViewManager);
        recyclerView.addItemDecoration(new MyDecoration());
//        recyclerView.smoothScrollToPosition(100);
        autoScrollViewManager.smoothScrollToPosition(recyclerView,state,23);
        adapter = new StraggerAdapter(LoginActivity.this);
        recyclerView.setAdapter(adapter);
//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                boolean value = false;
//                switch (event.getAction()){
//                    case ACTION_MOVE:
//                        value = true;
//                        break;
//                    case ACTION_DOWN:
//                        value = true;
//                        break;
//                }
//                return value;
//            }
//        });
//
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 如果自动滑动到最后一个位置，则此处状态为SCROLL_STATE_IDLE
                    autoScrollViewManager = (autoScrollViewManager) recyclerView
                            .getLayoutManager();
                    int position = 23;
                    int count = autoScrollViewManager.getItemCount();
                    if(position == count){
                        autoScrollViewManager.scrollToPosition(0);
                        autoScrollViewManager.smoothScrollToPosition(recyclerView,state,adapter.getItemCount());
                    }
                }
            }
        });
//        recyclerView.addOnScrollListener(new OnScrollListener() {
//            boolean isSlidingToLast = false;
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
//                int[] lastVisiblePositions = manager.findLastVisibleItemPositions(new int[manager.getSpanCount()]);
//                int lastVisiblePos = getMaxElem(lastVisiblePositions);
//                int totalItemCount = manager.getItemCount();
//
//                // 判断是否滚动到底部
//                if (lastVisiblePos == (totalItemCount -1) && isSlidingToLast) {
//                    autoScrollViewManager.scrollToPosition(0);
//                        autoScrollViewManager.smoothScrollToPosition(recyclerView,state,adapter.getItemCount());
//                }
//
//            }
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
//                if(dy > 0){
//                    //大于0表示，正在向下滚动
//                    isSlidingToLast = true;
//                }else{
//                    //小于等于0 表示停止或向上滚动
//                    isSlidingToLast = false;
//                }
//
//            }
//        });
        setOnClickListeren();
//        Thread myThread=new Thread() {//创建子线程
//            @Override
//            public void run() {
//                try {
//                    Socket socket = new Socket("192.168.123.1", 12345);
//
//                    chatSocket chatSocket = new chatSocket(socket);
//                    chatSocket.start();
//                    SocketManager.getSocketManager().addSocket(chatSocket);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        myThread.start();//启动线程
    }

    public void setOnClickListeren(){
        OnClick onClick = new OnClick();
        bt_login.setOnClickListener(onClick);
        register.setOnClickListener(onClick);
        forgive.setOnClickListener(onClick);
        bt_pass.setOnClickListener(onClick);
        bt_login_other.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_login_wx:
                    Intent intent10 = new Intent(LoginActivity.this,PhoneLoginActivity.class);
                    intent10.putExtra("context",getApplicationContext().toString());
                    startActivity(intent10);
                    break;
                case R.id.bt_register:
                    Intent intent1 = new Intent(LoginActivity.this,registerActivity.class);
                    intent1.putExtra("action","register");
                    startActivity(intent1);
                    break;
                case R.id.bt_forgive:
                    Intent intent2 = new Intent(LoginActivity.this,registerActivity.class);
                    intent2.putExtra("action","forgive");
                    startActivity(intent2);
                    break;
                case R.id.bt_pass:
                        bt_pass.setTypeface(Typeface.DEFAULT_BOLD);
                        Toast.makeText(LoginActivity.this,"功能暂未开发完成",Toast.LENGTH_LONG);
                        break;
                case R.id.bt_login_other:
                    Intent intent0 = new Intent(LoginActivity.this,OtherLoginActivity.class);
                    intent0.putExtra("context",getApplicationContext().toString());
                    startActivity(intent0);
            }
        }
    }
    public class MyDecoration extends  RecyclerView.ItemDecoration{
        public void getItemOffsets(@NonNull Rect outRect, int itemPosition,
                                   @NonNull RecyclerView parent) {
            outRect.set(getResources().getDimensionPixelOffset(R.dimen.height), getResources().getDimensionPixelOffset(R.dimen.height), getResources().getDimensionPixelOffset(R.dimen.height), getResources().getDimensionPixelOffset(R.dimen.height));
        }
    }
    private int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i]>maxVal)
                maxVal = arr[i];
        }
        return maxVal;
    }

}
