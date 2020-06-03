package com.example.chat95.cryptology;

import com.example.chat95.data.Keys;
import com.example.chat95.data.PrivateKey;
import com.example.chat95.data.PublicKey;

import java.math.BigInteger;
import java.util.Random;

public class Rsa {

    private BigInteger p;
    private BigInteger q;
    private static BigInteger N;
    private BigInteger phi;
    private static BigInteger e;
    private BigInteger d;
    private int bitlength = 1024;
    private Random r;

    public Rsa() {
        r = new Random();
        p = BigInteger.probablePrime(bitlength, r);
        q = BigInteger.probablePrime(bitlength, r);
        N = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(bitlength / 2, r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
    }

    public Rsa(BigInteger e, BigInteger d, BigInteger N) {
        this.e = e;
        this.d = d;
        this.N = N;
    }


    private static String bytesToString(byte[] encrypted) {
        String test = "";
        for (byte b : encrypted) {
            test += Byte.toString(b);
        }
        return test;
    }

    public static PrivateKey createPrivateKey() {
        String p;
        String q;
        String d;
        p = "34927358737";
        q = "9574347839";
        d = "389534786456895897";
        PrivateKey privateKey = new PrivateKey(p, q, d);
        return privateKey;
    }

    public static Keys createKeys() {
        return new Keys(new PublicKey("543","23423"),new PrivateKey("45654","345423","23454325"));
    }

    // Encrypt message
    public static String encrypt(String text, PublicKey foreignPublicKey) {
        // TODO: 02/06/2020

        return text + "$$";
    }

    // Decrypt message
    public static String decrypt(String text, PrivateKey privateKey) {
        // TODO: 02/06/2020

        return text.substring(0, text.length() - 2);
    }
    /*// Encrypt message
    public byte[] encrypt(byte[] message) {
        return (new BigInteger(message)).modPow(e, N).toByteArray();
    }

    // Decrypt message
    public byte[] decrypt(byte[] message) {
        return (new BigInteger(message)).modPow(d, N).toByteArray();
    }*/


    public static String signature(String textMessage, PrivateKey privateKey) {
        // TODO: 02/06/2020

        return KeyGenerator.generateKey(6);
    }

    public static boolean verify(String textMessage, String signature, PublicKey foreignPublicKey) {
        // TODO: 02/06/2020

        return true;
    }
}
