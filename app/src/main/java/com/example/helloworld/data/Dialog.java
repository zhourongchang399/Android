package com.example.helloworld.data;


import com.example.helloworld.socket.MessageInfo;
import java.util.List;

public class Dialog {


    private String id;
    private MessageInfo lastMsg;
    private UserInfo userInfo;
    private String type;
    private List<UserInfo> userInfos;
    private int unreadCount = 0;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<UserInfo> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<UserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Dialog() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageInfo getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(MessageInfo lastMsg) {
        this.lastMsg = lastMsg;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

}

