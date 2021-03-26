package com.example.helloworld.socket;

import android.content.Context;
import android.util.Log;

import com.example.helloworld.http.HttpUtil;
import com.example.helloworld.http.httpPathList;

import java.io.IOException;
import java.net.Socket;

public class ConnectServer extends Thread {
    chatSocket chatSocket;
    Socket socket;
    Context context;

    public ConnectServer(Context context){
        this.context = context;
    }
    @Override
    public void run() {
        try {
            socket = new Socket(new httpPathList().toString(), 12346);
            chatSocket = new chatSocket(socket,context);
            chatSocket.start();
            if (chatSocket != null)
                System.out.println("socket连接成功！");
            SocketManager.getSocketManager().addSocket(chatSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopChatSocket(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
