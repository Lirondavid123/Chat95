package com.example.chat95.data;



public class ChatMessage {

    private String textMessage, senderId, receiverId, timeStamp;

    public ChatMessage() {

    }

    public ChatMessage(String textMessage, String senderId, String receiverId, String timeStamp) {
        this.textMessage = textMessage;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timeStamp = timeStamp;
    }
//team
    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public int getSenderId() {
       // return senderId;
        return 5;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
