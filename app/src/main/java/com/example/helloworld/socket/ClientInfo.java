package com.example.helloworld.socket;

import java.io.Serializable;

public class ClientInfo implements Serializable {

    private static final long serialVersionUID =  -703658844871140677L;
    private int id;
    private int userId;
    private MessageInfo messageInfo;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public MessageInfo getMessageInfo() {
        return messageInfo;
    }

    public void setMessageInfo(MessageInfo messageInfo) {
        this.messageInfo = messageInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
