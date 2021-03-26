package com.example.helloworld.socket;

import com.example.helloworld.data.Cookies;

import java.io.Serializable;
import java.lang.reflect.Type;

public class MessageInfo implements Serializable {
    private static final long serialVersionUID =  -703658844871140678L;
    int userId,receiveId;
    int imgId;
    String text;
    String createTime;
    String type;

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(int receiveId) {
        this.receiveId = receiveId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "userId=" + userId +
                ", receiveId=" + receiveId +
                ", text='" + text + '\'' +
                ", createTime=" + createTime +
                '}';
    }


}
