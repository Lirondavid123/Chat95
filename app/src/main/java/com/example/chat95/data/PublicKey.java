package com.example.chat95.data;

public class PublicKey {
    String e;
    String n;

    public PublicKey() {
    }

    public PublicKey(String e, String n) {
        this.e = e;
        this.n = n;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }
}
