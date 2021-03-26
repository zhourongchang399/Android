package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
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

import com.example.helloworld.R;
import com.example.helloworld.adapter.MsgAdapter;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.data.historyMessage;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.MessageInfo;
import com.example.helloworld.ui.AlterDialogManager;
import com.example.helloworld.ui.MyDialogManager;
import com.example.helloworld.ui.emoji.Emoji;
import com.example.helloworld.ui.emoji.EmojiUtil;
import com.example.helloworld.ui.emoji.FaceFragment;
import com.example.helloworld.utils.BitmapUtils;
import com.example.helloworld.utils.DateUtils;
import com.example.helloworld.utils.FileUtils;
import com.example.helloworld.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.helloworld.http.HttpUtil.initJson;

public class ChatMessageActivity extends MyAppCompatActivity {

    MsgAdapter msgAdapter;
    static Context context;
    EditText inputText;
    ImageButton otherButton,sendButton,emojiButton;
    RecyclerView msgRecyclerView;
    FrameLayout emoji;
    TextView title;
    Toolbar toolbar;
    ImageButton point;
    PopupWindow popupWindow;

    static String img = "img";
    static String text = "text";
    static String defect = "defect";
    static int senderId;
    static int receiveId;
    static String senderUsername;
    int emojiStatus = 0;
    String receiveUsername;
    JSONObject jsonObject;
    List<MessageInfo> messageInfos;
    Handler handler;
    private Handler mHandler;
    historyMessageThread historyMessageThread;
    MyBroadcastReceive myBroadcastReceive;
    MyBroadcastForHisMsgReceive myBroadcastForHisMsgReceive;
    Uri uri;
    UserInfo userInfo;
    String base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        myBroadcastReceive = new MyBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter("updateNowMessage");
        LocalBroadcastManager.getInstance(ChatMessageActivity.this).registerReceiver(myBroadcastReceive,intentFilter);
        myBroadcastForHisMsgReceive = new MyBroadcastForHisMsgReceive();
        IntentFilter intentFilter2 = new IntentFilter("hisMsgPos");
        LocalBroadcastManager.getInstance(ChatMessageActivity.this).registerReceiver(myBroadcastForHisMsgReceive,intentFilter2);
        init();
    }

    public void initThread(){
        new resetUnreadMessage().start();
        handler = new MyHandler();
        new onLineThread().start();
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
        setOnClickListeren();
        senderId = Cookies.getCookies().getUserId();
        senderUsername = Cookies.getCookies().getUsername();
        context = getApplicationContext();
        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        receiveId = userInfo.getUserId();
        receiveUsername = userInfo.getName();
        title.setText(receiveUsername);
        View view = getLayoutInflater().inflate(R.layout.popup_window,null);
        TextView search,delete;
        search = view.findViewById(R.id.search);
        delete = view.findViewById(R.id.delete);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatMessageActivity.this,HistoryMsgActivity.class);
                intent.putExtra("userInfo",userInfo);
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlterDialogManager(ChatMessageActivity.this, "是否要清空聊天记录？", new AlterDialogManager.onClockListner() {
                    @Override
                    public void onClockListner(int i) {
                        if (i == 0){
                            new MyDeleteHisMsgThread().start();
                        }
                    }
                }).show();
            }
        });
        popupWindow = new PopupWindow(view,320, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
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
        msgAdapter = new MsgAdapter(messageInfos,context,userInfo);
        msgRecyclerView.setAdapter(msgAdapter);
        android.os.Message message = handler.obtainMessage();
        message.what = 0x18;
        handler.sendMessage(message);
    }

    static public void open(Context context, UserInfo userInfo) {
        Intent intent = new Intent(context, ChatMessageActivity.class);
        intent.putExtra("userInfo", userInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x15) {
                initAdapter();
            }
            if (msg.what == 0x16) {
                Intent intent = new Intent("updateMessage");
                LocalBroadcastManager.getInstance(ChatMessageActivity.this).sendBroadcast(intent);
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
        }
    }

    public class historyMessageThread extends Thread {
        @Override
        public void run() {
            String path = new httpPathList().getPath()[9];
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("userId", senderId);
                jsonObject.putOpt("age", receiveId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Type type = new TypeToken<List<MessageInfo>>() {
            }.getType();
            String historyString = HttpUtil.initJson().url(path).method(HttpUtil.RequestMethod.POST).body(jsonObject).invoke();
            if (historyString != null) {
                List<MessageInfo> messageInfos = null;
                Gson gson = new Gson();
                messageInfos = gson.fromJson(historyString, type);
                historyMessage.getHistoryMessage().setMessageInfos(messageInfos);
            } else {
                historyMessage.getHistoryMessage().setMessageInfos(null);
            }
            android.os.Message message = handler.obtainMessage();
            message.what = 0x15;
            handler.sendMessage(message);
        }
    }

    public class myThread extends Thread {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            String path = new httpPathList().getPath()[8];
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
            HttpUtil.initJson().url(new httpPathList().getPath()[14]).addParam("userId", Integer.toString(Cookies.getCookies().getUserId())).addParam("userId2", Integer.toString(receiveId)).method(HttpUtil.RequestMethod.GET).invoke();
        }
    }

    public class offLineThread extends Thread {
        @Override
        public void run() {
            HttpUtil.initJson().url(new httpPathList().getPath()[13]).addParam("userId", Integer.toString(Cookies.getCookies().getUserId())).addParam("userId2", Integer.toString(receiveId)).method(HttpUtil.RequestMethod.GET).invoke();
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
                case R.id.point:
                    popupWindow.showAsDropDown(point);
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
        messageInfo.setReceiveId(receiveId);
        messageInfo.setText(content);
        messageInfo.setCreateTime(DateUtils.getNowDateString());
        messageInfo.setType(type);
        return messageInfo;
    }

    public class resetUnreadMessage extends Thread {
        @Override
        public void run() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("userId",Integer.toString(senderId));
                jsonObject.putOpt("age",Integer.toString(receiveId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpUtil.initJson().url(new httpPathList().getPath()[16]).body(jsonObject).method(HttpUtil.RequestMethod.POST).invoke();
            android.os.Message message = handler.obtainMessage();
            message.what = 0x16;
            handler.sendMessage(message);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO 自动生成的方法存根
        if (data != null) {
            uri = data.getData();// 获取图片的路径
            String selectedImagePath = FileUtils.getFilePathByUri(context,uri,getExternalFilesDir(Environment.DIRECTORY_PICTURES));
//            Bitmap bitmap = ImageUtils.lessenUriImage(selectedImagePath);
            Bitmap bitmap = BitmapUtils.scaleBitmap(BitmapFactory.decodeFile(selectedImagePath),(float) 0.3);//filePath
            String type = com.blankj.utilcode.util.ImageUtils.getImageType(selectedImagePath);
            base64 = ImageUtils.bitmapToBase64(bitmap);
            jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("userId", senderId);
                jsonObject.putOpt("text", base64);
                jsonObject.putOpt("receiveId", receiveId);
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
            String path = new httpPathList().getPath()[18];
            String result = initJson().url(path).method(HttpUtil.RequestMethod.POST).body(jsonObject).invoke();
            if (result != null) {
                if (result.equals("defect"))
                    message.what = 0x24;
                else {
                    message.what = 0x23;
                }
                handler.sendMessage(message);
            }
        }
    }

    public boolean onSubmit(String input) {
        jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("userId", senderId);
            jsonObject.putOpt("text", input);
            jsonObject.putOpt("receiveId", receiveId);
            jsonObject.putOpt("createTime", DateUtils.getNowDateString());
            jsonObject.putOpt("type", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new myThread().start();
        return true;
    }

    public class MyDeleteHisMsgThread extends Thread {

        @Override
        public void run() {
            String result = HttpUtil.initJson()
                    .url(new httpPathList().getPath()[24])
                    .addParam("send", Integer.toString(senderId))
                    .addParam("receive", Integer.toString(receiveId))
                    .method(HttpUtil.RequestMethod.POST)
                    .invoke();
            if (result.equals("succeed")) {
                android.os.Message message = handler.obtainMessage();
                message.what = 0x20;
                handler.sendMessage(message);
            }
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
        new offLineThread().start();
        mHandler.removeCallbacks(historyMessageThread);
        if (myBroadcastForHisMsgReceive != null)
            LocalBroadcastManager.getInstance(ChatMessageActivity.this).unregisterReceiver(myBroadcastForHisMsgReceive);
        if (myBroadcastReceive != null)
            LocalBroadcastManager.getInstance(ChatMessageActivity.this).unregisterReceiver(myBroadcastReceive);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("updateMessage");
        LocalBroadcastManager.getInstance(ChatMessageActivity.this).sendBroadcast(intent);
    }

    public class MyBroadcastReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageInfo messageInfo = (MessageInfo) intent.getSerializableExtra("messageInfo");
            updateMessage(messageInfo);
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
