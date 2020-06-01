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
    private String e;

    @ColumnInfo
    private String d;

    @ColumnInfo
    private String p;

    @ColumnInfo
    private String q;

    @ColumnInfo
    private String n;

    @ColumnInfo
    private String SymmetricKey;


    public ConversationEntity(){}

    public ConversationEntity(@NonNull String uid, String e, String d, String p, String q, String n, String symmetricKey) {
        this.uid = uid;
        this.e = e;
        this.d = d;
        this.p = p;
        this.q = q;
        this.n = n;
        SymmetricKey = symmetricKey;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
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

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getSymmetricKey() {
        return SymmetricKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        SymmetricKey = symmetricKey;
    }
}
