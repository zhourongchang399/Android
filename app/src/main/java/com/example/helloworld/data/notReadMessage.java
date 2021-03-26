package com.example.helloworld.data;

import android.content.Intent;

import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;
import com.example.helloworld.presenter.MyAppCompatActivity;
import com.example.helloworld.socket.MessageInfo;

import java.util.ArrayList;
import java.util.List;

public class notReadMessage {
    private static notReadMessage notReadMessage = new notReadMessage();
    private void notReadMessage(){}
    public static notReadMessage getNotReadMessage(){
        return notReadMessage;
    }

    onLineThread onLineThread;
    List<MessageInfo> messageInfos = new ArrayList<>();

    public List<MessageInfo> getMessages() {
        return messageInfos;
    }

    public void setMessages(List<MessageInfo> messages) {
        this.messageInfos = messageInfos;
    }

    public void addMessage(MessageInfo messageInfo){
//        new onLineThread().start();
        messageInfos.add(messageInfo);
    }

    public void resetMessage(int i){
        messageInfos.clear();
    }

    public class onLineThread extends Thread {
        @Override
        public void run() {
            HttpUtil.initJson().url(new httpPathList().getPath()[14]).addParam("userId", Integer.toString(Cookies.getCookies().getUserId())).method(HttpUtil.RequestMethod.POST).invoke();
        }
    }

}
