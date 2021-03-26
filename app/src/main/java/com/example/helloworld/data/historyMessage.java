package com.example.helloworld.data;

import com.example.helloworld.socket.MessageInfo;

import java.util.ArrayList;
import java.util.List;

public class historyMessage {
    private static historyMessage historyMessage = new historyMessage();
    private void historyMessage(){}
    public static historyMessage getHistoryMessage(){
        return historyMessage;
    }

    List<MessageInfo> messageInfos = new ArrayList<>();

    public List<MessageInfo> getMessageInfos() {
        return messageInfos;
    }

    public void setMessageInfos(List<MessageInfo> messageInfos) {
        this.messageInfos = messageInfos;
    }

}
