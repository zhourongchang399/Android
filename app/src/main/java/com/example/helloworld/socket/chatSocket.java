package com.example.helloworld.socket;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.helloworld.activity.ApplicationActivity;
import com.example.helloworld.data.Cookies;
import com.example.helloworld.data.Message;
import com.example.helloworld.data.User;
import com.example.helloworld.data.notReadMessage;
import com.example.helloworld.ui.MyDialogManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class chatSocket extends Thread {
    public Socket chaSocket;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    Context context;

    public chatSocket(Socket socket,Context context) {
        this.context = context;
        this.chaSocket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(chaSocket.getOutputStream());
            objectInputStream = new ObjectInputStream(chaSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getChatSocket(){
        return chaSocket;
    }

    @Override
    public void run() {
        try {
            ClientInfo clientInfo;
            while ((clientInfo = (ClientInfo) objectInputStream.readObject()) != null) {
                if(clientInfo.getUserId() == 0) {
                    Intent intent = new Intent("updateMessage");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                if (clientInfo.getUserId() == 1){
                    Intent intent = new Intent("updateInfo");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                if (clientInfo.getUserId() == 10){
                    Intent intent1 = new Intent("updateMessage");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                    Intent intent = new Intent("updateMessageGroup");
                    MessageInfo messageInfo = clientInfo.getMessageInfo();
                    intent.putExtra("group",Integer.toString(clientInfo.getId()));
                    intent.putExtra("messageInfo",messageInfo);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                else {
                    Intent intent = new Intent("updateNowMessage");
                    MessageInfo messageInfo = clientInfo.getMessageInfo();
                    intent.putExtra("messageInfo",messageInfo);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
            objectInputStream.close();

        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void outputObject() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setId(0);
        clientInfo.setMessageInfo(null);
        clientInfo.setUserId(Cookies.getCookies().getUserId());
        try {
            objectOutputStream.writeObject(clientInfo);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void heartOutput(ClientInfo clientInfo) {
        try {
            objectOutputStream.writeObject(clientInfo);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket(){
        try {
            chaSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }
