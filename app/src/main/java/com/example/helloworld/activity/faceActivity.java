package com.example.helloworld.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.helloworld.R;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.ui.MyDialogManager;
import com.example.helloworld.utils.BitmapUtils;
import com.example.helloworld.utils.FileUtils;
import com.example.helloworld.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.helloworld.http.HttpUtil.initJson;

public class faceActivity extends MyAppCompatActivity {

    ImageView face;
    TextView change_face;
    Uri uri;
    JSONObject jsonObject;
    Handler handler;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        init();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x11){
                    new MyDialogManager(faceActivity.this,"修改成功",1).show();
                    face.setImageBitmap(ScaleImg(bitmap));
                    Intent intent = new Intent("updateFace");
                    intent.putExtra("img",ImageUtils.bitmapToBase64(bitmap));
                    LocalBroadcastManager.getInstance(faceActivity.this).sendBroadcast(intent);
                }
            }
        };

    }

    public void init(){
        face = findViewById(R.id.user_face);
        Glide.with(faceActivity.this).load(new httpPathList().getUrl()+"/"+Cookies.getCookies().getImg()).into(face);
//        Bitmap bitmap = ScaleImg(ImageUtils.base64ToBitmap(Cookies.getCookies().getImg()));
////        face.setImageBitmap(bitmap);
        change_face = findViewById(R.id.change_face);
        change_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            uri = data.getData();// 获取图片的路径
            String selectedImagePath = FileUtils.getFilePathByUri(getApplicationContext(),uri,getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            bitmap = BitmapUtils.scaleBitmap(BitmapFactory.decodeFile(selectedImagePath),(float) 1);//filePath
            String base64 = ImageUtils.bitmapToBase64(bitmap);
            jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("userId", Cookies.getCookies().getUserId());
                jsonObject.putOpt("img", base64);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new myImageThread().start();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class myImageThread extends Thread{
        @Override
        public void run() {
            String path = new httpPathList().getPath()[19];
            String result = initJson().url(path).method(HttpUtil.RequestMethod.POST).body(jsonObject).invoke();
            if (result.equals("succeed")) {
                Message message = handler.obtainMessage();
                message.what = 0x11;
                handler.sendMessage(message);
            }
        }
    }

    public Bitmap ScaleImg(Bitmap bitmap){
        WindowManager windowManager = getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        bitmap = com.blankj.utilcode.util.ImageUtils.scale(bitmap,width,width);
        return bitmap;
    }
}
