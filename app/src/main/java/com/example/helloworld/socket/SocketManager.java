package com.example.helloworld.socket;

import android.content.Context;

import com.example.helloworld.data.Cookies;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SocketManager {
    private static final SocketManager socketManager = new SocketManager();
    private SocketManager(){}
    public static SocketManager getSocketManager(){
        return socketManager;
    }

    chatSocket chatSocket;

    public void addSocket(chatSocket chatSocket){
        this.chatSocket = chatSocket;
    }

    public boolean sendMessage(){return true;}

    public void sendUserId(){
        if (chatSocket == null)
            System.out.println("socket未连接！");
        chatSocket.outputObject();
    }

    public void heart(){
        ClientInfo clientInfo = new ClientInfo();
        chatSocket.heartOutput(clientInfo);
    }
    public void Close(){
        chatSocket.closeSocket();
    }
}
