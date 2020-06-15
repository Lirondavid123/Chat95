package com.example.chat95.chatactivity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ConversationEntity {

    @PrimaryKey
    @NonNull
    private String conversationId;

    @ColumnInfo
    private String myE;
    @ColumnInfo
    private String myN;

    @ColumnInfo
    private String d;
    @ColumnInfo
    private String p;

    @ColumnInfo
    private String q;
    @ColumnInfo
    private String SymmetricKey;
    @ColumnInfo
    private String foreignE;
    @ColumnInfo
    private String foreignN;
    @ColumnInfo
    private boolean isApproved;

    public ConversationEntity() {
    }

    public ConversationEntity(@NonNull String conversationId, String myE, String myN, String d, String p, String q, String symmetricKey, String foreignE, String foreignN, boolean isApproved) {
        this.conversationId = conversationId;
        this.myE = myE;
        this.myN = myN;
        this.d = d;
        this.p = p;
        this.q = q;
        SymmetricKey = symmetricKey;
        this.foreignE = foreignE;
        this.foreignN = foreignN;
        this.isApproved = isApproved;
    }

    @NonNull
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMyE() {
        return myE;
    }

    public void setMyE(String myE) {
        this.myE = myE;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getMyN() {
        return myN;
    }

    public void setMyN(String myN) {
        this.myN = myN;
    }

    public String getSymmetricKey() {
        return SymmetricKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        SymmetricKey = symmetricKey;
    }

    public String getForeignE() {
        return foreignE;
    }

    public void setForeignE(String foreignE) {
        this.foreignE = foreignE;
    }

    public String getForeignN() {
        return foreignN;
    }

    public void setForeignN(String foreignN) {
        this.foreignN = foreignN;
    }

    public boolean isApproved() {
        return isApproved;
    }

    @Override
    public String toString() {
        return "ConversationEntity{" +
                "conversationId='" + conversationId + '\'' +
                ", myE='" + myE + '\'' +
                ", myN='" + myN + '\'' +
                ", d='" + d + '\'' +
                ", p='" + p + '\'' +
                ", q='" + q + '\'' +
                ", SymmetricKey='" + SymmetricKey + '\'' +
                ", foreignE='" + foreignE + '\'' +
                ", foreignN='" + foreignN + '\'' +
                ", isApproved=" + isApproved +
                '}';
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
