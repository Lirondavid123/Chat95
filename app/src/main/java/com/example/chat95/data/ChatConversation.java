package com.example.chat95.data;

import com.google.firebase.database.Exclude;

public class ChatConversation {
    private PublicKey publicKey;
    private String conversationId;
    private String userName;
    private String chosenUid;
    private String sender, receiver;
    private String receiverProfilePicture;
    private Boolean isApproved;
    private String KIC;

    public ChatConversation() {

    }

    public ChatConversation(PublicKey publicKey, String conversationId,
                            String sender, String receiver, String sentToProfilePicture, Boolean isApproved,
                            String userName, String chosenUid, String KIC) {
        this.publicKey = publicKey;
        this.conversationId = conversationId;
        this.sender = sender;
        this.receiver = receiver;
        this.receiverProfilePicture = sentToProfilePicture;
        this.isApproved = isApproved;
        this.userName = userName;
        this.chosenUid = chosenUid;
        this.KIC = KIC;
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

    public Boolean isApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
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

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getKIC() {
        return KIC;
    }

    public void setKIC(String KIC) {
        this.KIC = KIC;
    }
}


