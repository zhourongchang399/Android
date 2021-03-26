package com.example.helloworld.data;

import androidx.annotation.NonNull;

public class MyDialog {

        private String id;
        private String dialogName;
        private Message lastMessage;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDialogName() {
            return dialogName;
        }

        public void setDialogName(String dialogName) {
            this.dialogName = dialogName;
        }

        public Message getLastMessage() {
            return lastMessage;
        }

        public void setLastMessage(Message lastMessage) {
            this.lastMessage = lastMessage;
        }

    @NonNull
    @Override
    public String toString() {
            return id+dialogName+lastMessage.toString();
    }
}
