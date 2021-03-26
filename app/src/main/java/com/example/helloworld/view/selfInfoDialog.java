package com.example.helloworld.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.helloworld.R;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.UserInfo;
import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.ui.MyDialogManager;

import org.json.JSONException;
import org.json.JSONObject;

public class selfInfoDialog extends Dialog {
    EditText name,city,age,content;
    RadioGroup radioGroup;
    String myname,mycity,mycontent,mysex;
    int myage;
    Button save,back;
    int i,n = 1,j;
    MyDialogManager myDialogManager;
    Context context;
    String user;
    RadioButton female,male;
    updateListener updateListener;
    String path;
    UserInfo userInfo1;
    Handler handler;

    public selfInfoDialog(@NonNull Context context, String user, String name, String sex, String city, String content, String age, updateListener updateListener) {
        super(context);
        this.context = context;
        this.user = user;
        this.updateListener = updateListener;
        myage = Integer.parseInt(age);
        mycity  = city;
        mycontent = content;
        myname = name;
        mysex = sex;
    }

    public selfInfoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected selfInfoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_info);
        name = findViewById(R.id.self_name_ed);
        city = findViewById(R.id.selt_city_ed);
        age = findViewById(R.id.self_age_ed);
        content = findViewById(R.id.self_content_ed);
        radioGroup = findViewById(R.id.radio_sex);
        save = findViewById(R.id.self_save);
        back = findViewById(R.id.self_cancel);
        female = findViewById(R.id.female);
        male = findViewById(R.id.male);
        city.setText(mycity);
        content.setText(mycontent);
        name.setText(myname);
        age.setText(Integer.toString(myage));

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x12) {
                    Bundle bundle = msg.getData();
                    String result = bundle.getString("result");
                    if (result.equals("succeed")) {
                        myDialogManager = new MyDialogManager(context, "保存成功", 1);
                        myDialogManager.show();
                    } else if (result.equals("defeat")) {
                        myDialogManager = new MyDialogManager(context, "保存失败", 1);
                        myDialogManager.show();
                    }
                }
            }
        };
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.female:
                        n = 0;
                        break;
                    case R.id.male:
                        n = 1;
                        break;
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userInfo = new UserInfo();
               if (!city.getText().toString().trim().equals("")){
                   mycity = city.getText().toString().trim();
                }
               if (!content.getText().toString().trim().equals("")){
                   mycontent = content.getText().toString().trim();
                }
                if (!name.getText().toString().trim().equals("")){
                    myname = name.getText().toString().trim();
                }
                if (!age.getText().toString().trim().equals("")){
                    myage = Integer.parseInt(age.getText().toString().trim());
                }
                    if (n == 0)
                        mysex = "女";
                    else if (n == 1)
                        mysex = "男";
                    final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.putOpt("userId", Cookies.getCookies().getUserId());
                    jsonObject.putOpt("name",myname );
                    jsonObject.putOpt("username",Cookies.getCookies().getUsername());
                    jsonObject.putOpt("age",myage );
                    jsonObject.putOpt("city",mycity );
                    jsonObject.putOpt("personalProfile",mycontent );
                    jsonObject.putOpt("sex",mysex );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                      path = new httpPathList().getPath()[3];
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            if ((userInfo1 = HttpUtil.initJson().url(path).method(HttpUtil.RequestMethod.POST).body(jsonObject).invokeClass(UserInfo.class)) != null) {
                                bundle.putString("result","succeed");
                                updateListener.getResult(1);
                                Cookies.getCookies().setName(userInfo1.getName());
                                Cookies.getCookies().setPersonalProfile(userInfo1.getPersonalProfile());
                                Cookies.getCookies().setCity(userInfo1.getCity());
                                Cookies.getCookies().setAge(userInfo1.getAge());
                                Cookies.getCookies().setSex(userInfo1.getSex());
                            }
                            else
                                bundle.putString("result","defeat");
                            message.setData(bundle);
                            message.what = 0x12;
                            handler.sendMessage(message);
                        }
                    }).start();
                }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public interface updateListener{
        public void getResult(int i);
    }
}
