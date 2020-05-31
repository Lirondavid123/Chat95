package com.example.chat95.data;

public class ChatConversation {
    private String conversationId;
    private String userName;
    private String chosenUid;
    private String sender,receiver;
    private String receiverProfilePicture;
    private boolean isApproved;

    public ChatConversation(){

    }

    public ChatConversation(String conversationId, String sender, String receiver, String sentToProfilePicture, boolean isApproved,String userName, String chosenUid) {
        this.conversationId = conversationId;
        this.sender = sender;
        this.receiver = receiver;
        this.receiverProfilePicture = sentToProfilePicture;
        this.isApproved = isApproved;
        this.userName=userName;
        this.chosenUid=chosenUid;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverProfilePicture() {
        return receiverProfilePicture;
    }

    public void setReceiverProfilePicture(String receiverProfilePicture) {
        this.receiverProfilePicture = receiverProfilePicture;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getChosenUid() {
        return chosenUid;
    }

    public void setChosenUid(String chosenUid) {
        this.chosenUid = chosenUid;
    }
    //
}


