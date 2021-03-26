package com.example.helloworld.data;

public class LastMessage {
    int userId,receiveId;
    String text;
    String createTime;

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
