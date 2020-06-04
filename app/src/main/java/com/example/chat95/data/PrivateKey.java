package com.example.chat95.data;

import java.math.BigInteger;

public class PrivateKey {
    String p;
    String q;
    String d;

    public PrivateKey() {
    }

    public PrivateKey(String p, String q, String d) {
        this.p = p;
        this.q = q;
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

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getN() {
        BigInteger q = new BigInteger(this.q);
        BigInteger p = new BigInteger(this.p);
        return q.multiply(p).toString();
    }
}
