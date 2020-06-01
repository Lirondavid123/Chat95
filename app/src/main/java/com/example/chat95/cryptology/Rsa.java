package com.example.chat95.cryptology;

import com.example.chat95.data.PrivateKey;
import com.example.chat95.data.PublicKey;

import java.math.BigInteger;
import java.util.Random;

public class Rsa {

    private BigInteger p;
    private BigInteger q;
    private BigInteger N;
    private BigInteger phi;
    private BigInteger e;
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

    // Encrypt message
    public byte[] encrypt(byte[] message) {
        return (new BigInteger(message)).modPow(e, N).toByteArray();
    }

    // Decrypt message
    public byte[] decrypt(byte[] message) {
        return (new BigInteger(message)).modPow(d, N).toByteArray();
    }

    public static PublicKey getPublicKey() {
        String n;
        String e;
        // TODO: 01/06/2020 delete those values after RSA
        n = "7409359639836890586905908";
        e = "6043584064080965934894";
        //
        PublicKey publicKey = new PublicKey(e, n);
        return publicKey;
    }
}
