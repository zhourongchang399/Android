package com.example.helloworld.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.activity.LoginActivity;
import com.example.helloworld.activity.faceActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.socket.SocketManager;
import com.example.helloworld.ui.changeUserAccountDialogManager;
import com.example.helloworld.utils.ImageUtils;

import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.MODE_PRIVATE;

public class fragment_my extends Fragment {
    RecyclerView recyclerView;
    View thisView;
    GifImageView gifImageView;
    TextView username;
    selfInfoDialog selfInfoDialog;
    Button bt_userinfo,back,bt_option;
    TextView city,age,sex,myself,name;
    String my_username,my_city,my_myself,my_sex,my_name;
    Handler handler;
    int my_age,j,userid;
    Button home,friend,info,my;
    ImageView user_face;
    Context context;
    receiveListener receiveListener;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my,container,false);
        context = view.getContext();
        receiveListener = new receiveListener();
        IntentFilter intentFilter = new IntentFilter("updateFace");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiveListener,intentFilter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = context.getSharedPreferences("share", MODE_PRIVATE);
        username = view.findViewById(R.id.my_username);
        my_username = Cookies.getCookies().getUsername();
        username.setText(my_username);
        city = view.findViewById(R.id.my_user_city);
        name = view.findViewById(R.id.my_user_name);
        age = view.findViewById(R.id.my_user_age);
        sex = view.findViewById(R.id.my_user_sex);
        myself = view.findViewById(R.id.my_user_myself);
        back = view.findViewById(R.id.bt_back);
        bt_option = view.findViewById(R.id.bt_option);
        user_face = view.findViewById(R.id.my_user_face);
        Glide.with(context).load(new httpPathList().getUrl()+"/"+Cookies.getCookies().getImg()).centerCrop().into(user_face);
//        user_face.setImageBitmap(ImageUtils.base64ToBitmap(Cookies.getCookies().getImg()));
        setOnClickListeren();
        if (my_username != null){
            init();
        }

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    Bundle bundle = msg.getData();
                    if(bundle.getString("result").equals("update"))
                        init();
                }
            }
        };

        gifImageView = view.findViewById(R.id.gifimageview);
        gifImageView.setBackgroundResource(R.drawable.gif6);
        bt_userinfo = view.findViewById(R.id.bt_userinfo);
        bt_userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfInfoDialog = new selfInfoDialog(context, username.getText().toString().trim(),name.getText().toString().trim(),sex.getText().toString().trim(),city.getText().toString().trim(),myself.getText().toString().trim(),age.getText().toString().trim(),new selfInfoDialog.updateListener() {
                    @Override
                    public void getResult(int i) {
                        if (i == 1)
                            if (my_username != null) {
                                Message message = handler.obtainMessage();
                                Bundle bundle = new Bundle();
                                bundle.putString("result","update");
                                message.setData(bundle);
                                message.what = 0x11;
                                handler.sendMessage(message);
                            }
                    }
                });
                selfInfoDialog.getWindow().setBackgroundDrawableResource(R.drawable.ed_design2);
                selfInfoDialog.show();
            }
        });
//        recyclerView = view.findViewById(R.id.fellow_rcv);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();
////        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
////            @Override
////            public void onGlobalLayout() {
////                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
////                width = recyclerView.getWidth();
////                recyclerView.setAdapter(new AppStraggerAdapter(context));
////            }
////        });
//        recyclerView.setAdapter(new AppStraggerAdapter(context));
//        recyclerView.post(new Runnable() {
//            @Override
//            public void run() {
//                width = recyclerView.getWidth();
//                Log.w("topRecyc", String.valueOf(width));
//                recyclerView.setAdapter(new AppStraggerAdapter(context,width));
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void init() {
        userid = Cookies.getCookies().getUserId();
        my_age = Cookies.getCookies().getAge();
        my_city = Cookies.getCookies().getCity();
        my_name = Cookies.getCookies().getName();
        my_myself = Cookies.getCookies().getPersonalProfile();
        my_sex = Cookies.getCookies().getSex();
        name.setText(my_name);
        age.setText(Integer.toString(my_age));
        sex.setText(my_sex);
        city.setText(my_city);
        myself.setText(my_myself);
    }

    public void setOnClickListeren(){
        MyOnClickListeren myOnClickListeren = new MyOnClickListeren();
        back.setOnClickListener(myOnClickListeren);
        user_face.setOnClickListener(myOnClickListeren);
        bt_option.setOnClickListener(myOnClickListeren);
    }

    public class MyOnClickListeren implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case  R.id.bt_back:
                    editor = sharedPreferences.edit();
                    editor.putBoolean("isLogin",false);
                    editor.commit();
                    SocketManager.getSocketManager().Close();
                    Intent intent4 = new Intent(context, LoginActivity.class);
                    startActivity(intent4);
                    if (getActivity() instanceof testDataCallback) {
                        ((testDataCallback) getActivity()).testData();
                    }
                    Cookies.getCookies().reset();
                    break;
                case R.id.my_user_face:
                    startActivity(new Intent(context, faceActivity.class));
                    break;
                case R.id.bt_option:
                    new changeUserAccountDialogManager(context).show();
                    break;
            }
        }
    }

    public class receiveListener extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接收广播！");
            String base64 = intent.getStringExtra("img");
            Cookies.getCookies().setImg(base64);
            user_face.setImageBitmap(ImageUtils.base64ToBitmap(base64));
        }
    }

    public interface testDataCallback {
        void testData();
    }

}
