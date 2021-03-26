package com.example.helloworld.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloworld.R;
import com.example.helloworld.adapter.MsgAdapter;
import com.example.helloworld.adapter.MsgGroupAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.data.historyMessage;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.AlterDialogManager;
import com.example.helloworld.ui.emoji.Emoji;
import com.example.helloworld.ui.emoji.EmojiUtil;
import com.example.helloworld.ui.emoji.FaceFragment;
import com.example.helloworld.utils.BitmapUtils;
import com.example.helloworld.utils.DateUtils;
import com.example.helloworld.utils.FileProviderUtils;
import com.example.helloworld.utils.FileUtils;
import com.example.helloworld.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.helloworld.http.HttpUtil.initJson;
import static com.mob.MobSDK.getContext;

public class ChatMessageGroupActivity extends MyAppCompatActivity {

    MsgGroupAdapter msgAdapter;
    static Context context;
    EditText inputText;
    ImageButton otherButton,sendButton,emojiButton;
    RecyclerView msgRecyclerView;
    FrameLayout emoji;
    TextView title;
    Toolbar toolbar;
    ImageButton point;

    static String img = "img";
    static String text = "text";
    static String defect = "defect";
    static int senderId;
    static String group;
    static String senderUsername;
    int emojiStatus = 0;
    JSONObject jsonObject;
    List<MessageInfo> messageInfos;
    Handler handler;
    private Handler mHandler;
    historyMessageThread historyMessageThread;
    MyBroadcastReceive myBroadcastReceive;
    MyBroadcastDeleteReceive myBroadcastDeleteReceive;
    MyBroadcastForHisMsgReceive myBroadcastForHisMsgReceive;
    MyBroadcastChatNameReceive myBroadcastChatNameReceive;
    Uri uri;
    List<UserInfo> userInfos;
    List<UserInfo> msgUserInfos;
    String base64;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        myBroadcastChatNameReceive = new MyBroadcastChatNameReceive();
        IntentFilter intentFilter1 = new IntentFilter("updateGroupChatName");
        LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).registerReceiver(myBroadcastChatNameReceive,intentFilter1);
        myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter("updateMessageGroup");
        LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).registerReceiver(myBroadcastReceive,intentFilter);
        myBroadcastForHisMsgReceive = new MyBroadcastForHisMsgReceive();
        IntentFilter intentFilter2 = new IntentFilter("hisMsgPos");
        LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).registerReceiver(myBroadcastForHisMsgReceive,intentFilter2);
        myBroadcastDeleteReceive = new MyBroadcastDeleteReceive();
        IntentFilter intentFilter3 = new IntentFilter("deleteMessageGroup");
        LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).registerReceiver(myBroadcastDeleteReceive,intentFilter3);
        init();
    }

    public void initThread(){
        new resetUnreadMessage().start();
        handler = new MyHandler();
//        new onLineThread().start();
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        historyMessageThread = new historyMessageThread();
        mHandler.post(historyMessageThread);//将线程post到Handler中
    }

    public void init(){
        msgRecyclerView = findViewById(R.id.msg);
        sendButton = findViewById(R.id.sendButton);
        otherButton = findViewById(R.id.otherButton);
        emojiButton = findViewById(R.id.emojiButton);
        inputText = findViewById(R.id.input);
        emoji = findViewById(R.id.emoji);
        title = findViewById(R.id.other_title);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        point = findViewById(R.id.point);
        toolbar = findViewById(R.id.msgBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        senderId = Cookies.getCookies().getUserId();
        senderUsername = Cookies.getCookies().getUsername();
        context = getApplicationContext();
        Bundle bundle = getIntent().getExtras();
        userInfos = (List<UserInfo>)bundle.getSerializable("userInfos");
        group = getIntent().getStringExtra("group");
        name = getIntent().getStringExtra("name");
        title.setText(name);
        initThread();
    }

    public void setOnClickListeren(){
       MyOnClickListeren myOnClickListeren = new MyOnClickListeren();
        sendButton.setOnClickListener(myOnClickListeren);
        emojiButton.setOnClickListener(myOnClickListeren);
        otherButton.setOnClickListener(myOnClickListeren);
        point.setOnClickListener(myOnClickListeren);
    }

    public void initAdapter(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        msgRecyclerView.setLayoutManager(layoutManager);
        messageInfos = historyMessage.getHistoryMessage().getMessageInfos();
        if (messageInfos == null)
            messageInfos = new ArrayList<>();
        msgAdapter = new MsgGroupAdapter(messageInfos,context,userInfos,msgUserInfos);
        msgRecyclerView.setAdapter(msgAdapter);
        Message message = handler.obtainMessage();
        message.what = 0x18;
        handler.sendMessage(message);
    }

    static public void open(Context context, List<UserInfo> userInfos,String name,String group) {
        Intent intent = new Intent(context, ChatMessageGroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfos", (Serializable) userInfos);
        intent.putExtras(bundle);
        intent.putExtra("name",name);
        intent.putExtra("group",group);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x15) {
                new MyHisMsgThread().start();
            }
            if (msg.what == 0x16) {
                Intent intent = new Intent("updateMessage");
                LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).sendBroadcast(intent);
            }
            if(msg.what == 0x18){
                if (messageInfos.size() != 0)
                    msgRecyclerView.smoothScrollToPosition(messageInfos.size()-1);
            }
            if(msg.what == 0x20){
                new historyMessageThread().start();
            }
            if(msg.what == 0x21){
                updateMessage(getMessageInfo(inputText.getText().toString(), text));
                inputText.setText(null);
            }
            if(msg.what == 0x22){
                updateMessage(getMessageInfo(inputText.getText().toString(), defect));
                inputText.setText(null);
            }
            if(msg.what == 0x23){
                updateMessage(getMessageInfo(base64,"myImage"));
            }
            if(msg.what == 0x24){
                updateMessage(getMessageInfo(base64,"defect_img"));
            }
            if (msg.what == 0x32){
                setOnClickListeren();
                initAdapter();
            }
        }
    }

    public class historyMessageThread extends Thread {
        @Override
        public void run() {
            String path = new httpPathList().getPath()[36];
            Type type = new TypeToken<List<MessageInfo>>() {
            }.getType();
            String historyString = HttpUtil.initJson().url(path).method(HttpUtil.RequestMethod.POST).addParam("group",group).addParam("userId", Integer.toString(senderId)).invoke();
            if (historyString != null) {
                List<MessageInfo> messageInfos = null;
                Gson gson = new Gson();
                messageInfos = gson.fromJson(historyString, type);
                historyMessage.getHistoryMessage().setMessageInfos(messageInfos);
            } else {
                historyMessage.getHistoryMessage().setMessageInfos(null);
            }
            Message message = handler.obtainMessage();
            message.what = 0x15;
            handler.sendMessage(message);
        }
    }

    public class myThread extends Thread {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            String path = new httpPathList().getPath()[38];
            String result = initJson().url(path).method(HttpUtil.RequestMethod.POST).body(jsonObject).invoke();
            if (result.equals("succeed"))
                message.what = 0x21;
            else
                if (result.equals("defect"))
                    message.what = 0x22;
            handler.sendMessage(message);
        }
    }

    public class onLineThread extends Thread {
        @Override
        public void run() {
            HttpUtil.initJson().url(new httpPathList().getPath()[14]).addParam("userId", Integer.toString(Cookies.getCookies().getUserId())).addParam("userId2", group).method(HttpUtil.RequestMethod.GET).invoke();
        }
    }

    public class offLineThread extends Thread {
        @Override
        public void run() {
            HttpUtil.initJson().url(new httpPathList().getPath()[13]).addParam("userId", Integer.toString(Cookies.getCookies().getUserId())).addParam("userId2", group).method(HttpUtil.RequestMethod.GET).invoke();
        }
    }

    public class MyOnClickListeren implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sendButton:
                    if (!inputText.getText().toString().equals("")) {
                        onSubmit(inputText.getText().toString());
                    }
                    break;
                case R.id.otherButton:
                    clickOtherButton();
                    break;
                case R.id.emojiButton:
                    clickEmojiButton();
                    break;
                case R.id.point: {
                    GroupChatSettingActivity.open(ChatMessageGroupActivity.this, userInfos, name, group, msgUserInfos);
                }
            }
        }
    }

    public void clickEmojiButton(){
            if (emojiStatus == 0)
                emojiStatus =1;
            else
                emojiStatus = 0;
            final FaceFragment faceFragment = FaceFragment.Instance();
            faceFragment.setListener(new FaceFragment.OnEmojiClickListener(){
            @Override
            public void onEmojiDelete() {
                getSupportFragmentManager().beginTransaction().hide(faceFragment).commit();
            }

                @Override
                public void onEmojiClick(Emoji emoji) {
                String content = inputText.getText().toString()+emoji.getContent();
                    try {
                        EmojiUtil.handlerEmojiText(inputText,content,context);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        });
            if (emojiStatus == 0)
                getSupportFragmentManager().beginTransaction().add(R.id.emoji,faceFragment).commit();
            if (emojiStatus == 1)
                getSupportFragmentManager().beginTransaction().hide(faceFragment).commit();

    }

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.requestFocus();
            imm.showSoftInput(view, 0);
        }
    }

    public MessageInfo getMessageInfo(String content,String type){
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUserId(senderId);
        messageInfo.setReceiveId(Integer.parseInt(group));
        messageInfo.setText(content);
        messageInfo.setCreateTime(DateUtils.getNowDateString());
        messageInfo.setType(type);
        return messageInfo;
    }

    public class resetUnreadMessage extends Thread {
        @Override
        public void run() {
            HttpUtil.initJson().url(new httpPathList().getPath()[37]).addParam("group",group).addParam("userId",Integer.toString(senderId)).method(HttpUtil.RequestMethod.POST).invoke();
            Message message = handler.obtainMessage();
            message.what = 0x16;
            handler.sendMessage(message);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO 自动生成的方法存根
        if (data != null) {
            uri = data.getData();// 获取图片的路径

//            File imagePath = new File(context.getFilesDir(), "images");
//            File photoFile= new File(imagePath, "default_image.jpg");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Uri uri = FileProvider.getUriForFile(getContext(), "com.example.helloworld.fileprovider", photoFile);
//            } else {
//                Uri uri = Uri.fromFile(photoFile);
//            }
            String selectedImagePath = FileUtils.getFilePathByUri(context,uri,getExternalFilesDir(Environment.DIRECTORY_PICTURES));
//            uri = FileProviderUtils.getUriForFile(ChatMessageGroupActivity.this,picture);
//            String selectedImagePath = FileUtils.getFilePathByUri(context,uri);
//            Bitmap bitmap = ImageUtils.lessenUriImage(selectedImagePath);
            Bitmap bitmap = BitmapUtils.scaleBitmap(BitmapFactory.decodeFile(selectedImagePath),(float) 0.3);//filePath
            base64 = ImageUtils.bitmapToBase64(bitmap);
            jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("userId", senderId);
                jsonObject.putOpt("text", base64);
                jsonObject.putOpt("receiveId", group);
                jsonObject.putOpt("createTime", DateUtils.getNowDateString());
                jsonObject.putOpt("type", "jpg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new myImageThread().start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class myImageThread extends Thread {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            String path = new httpPathList().getPath()[39];
            String result = initJson().url(path).method(HttpUtil.RequestMethod.POST).body(jsonObject).invoke();
            if (result.equals("defect"))
                message.what = 0x24;
            else {
                message.what = 0x23;
            }
            handler.sendMessage(message);
        }
    }

    public boolean onSubmit(String input) {
        jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("userId", senderId);
            jsonObject.putOpt("text", input);
            jsonObject.putOpt("receiveId", group);
            jsonObject.putOpt("createTime", DateUtils.getNowDateString());
            jsonObject.putOpt("type", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new myThread().start();
        return true;
    }

    public class MyHisMsgThread extends Thread {

        @Override
        public void run() {
            String result = HttpUtil.initJson()
                    .url(new httpPathList().getPath()[50])
                    .addParam("group", group)
                    .method(HttpUtil.RequestMethod.POST)
                    .invoke();
            if (result != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<UserInfo>>(){}.getType();
                msgUserInfos = gson.fromJson(result,type);
            }
            Message message = handler.obtainMessage();
            message.what = 0x32;
            handler.sendMessage(message);
        }
    }

    public void clickOtherButton(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 0);
    }

    @Override
    public void finish() {
        super.finish();
//        new offLineThread().start();
        mHandler.removeCallbacks(historyMessageThread);
        if (myBroadcastForHisMsgReceive != null)
            LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).unregisterReceiver(myBroadcastForHisMsgReceive);
        if (myBroadcastReceive != null)
            LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).unregisterReceiver(myBroadcastReceive);
        if (myBroadcastDeleteReceive != null)
            LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).unregisterReceiver(myBroadcastDeleteReceive);
        if (myBroadcastChatNameReceive != null)
            LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).unregisterReceiver(myBroadcastChatNameReceive);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new resetUnreadMessage().start();
        Intent intent = new Intent("updateMessage");
        LocalBroadcastManager.getInstance(ChatMessageGroupActivity.this).sendBroadcast(intent);
    }

    public class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("messageInfo");
            if (intent.getStringExtra("group").equals(group))
                updateMessage(messageInfo);
        }
    }

    public class MyBroadcastDeleteReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("message").equals("delMsg")){
                messageInfos = null;
                initAdapter();
            }else
                if (intent.getStringExtra("message").equals("delGroup"))
                    finish();
        }
    }

    public class MyBroadcastChatNameReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            title.setText(intent.getStringExtra("name"));
        }
    }

    public class MyBroadcastForHisMsgReceive extends BroadcastReceiver {
        int pos;
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("hisMsg");
            for (int i = 0; i < messageInfos.size(); i++) {
                if (messageInfo.getCreateTime().equals(messageInfos.get(i).getCreateTime())){
                    pos = i;
                    break;
                }
            }
            msgRecyclerView.smoothScrollToPosition(pos);
        }
    }

    public void updateMessage(MessageInfo messageInfo){
        if (messageInfos == null) {
            messageInfos = new ArrayList<>();
        }
            messageInfos.add(messageInfo);
            msgAdapter.notifyDataSetChanged();
            msgRecyclerView.smoothScrollToPosition(messageInfos.size() - 1);
    }

}
