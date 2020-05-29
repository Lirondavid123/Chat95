package com.example.chat95.chatactivity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ConversationEntity {

    @PrimaryKey@NonNull
    private String uid;

    @ColumnInfo
    private String myprivateKey;

    @ColumnInfo
    private String mypublicKey;

    @ColumnInfo
    private String partnerKey;

    @ColumnInfo
    private String partnerMod;

    @ColumnInfo
    private String SymmetricKey;


    public ConversationEntity(){}


    public ConversationEntity(@NonNull String uid, String myprivateKey, String mypublicKey, String partnerKey, String partnerMod, String symmetricKey) {
        this.uid = uid;
        this.myprivateKey = myprivateKey;
        this.mypublicKey = mypublicKey;
        this.partnerKey = partnerKey;
        this.partnerMod = partnerMod;
        SymmetricKey = symmetricKey;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public String getMyprivateKey() {
        return myprivateKey;
    }

    public String getMypublicKey() {
        return mypublicKey;
    }

    public String getPartnerKey() {
        return partnerKey;
    }

    public String getPartnerMod() {
        return partnerMod;
    }

    public String getSymmetricKey() {
        return SymmetricKey;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMyprivateKey(String myprivateKey) {
        this.myprivateKey = myprivateKey;
    }

    public void setMypublicKey(String mypublicKey) {
        this.mypublicKey = mypublicKey;
    }

    public void setPartnerKey(String partnerKey) {
        this.partnerKey = partnerKey;
    }

    public void setPartnerMod(String partnerMod) {
        this.partnerMod = partnerMod;
    }

    public void setSymmetricKey(String symmetricKey) {
        SymmetricKey = symmetricKey;
    }
}
