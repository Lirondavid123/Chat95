package com.example.chat95.cryptology;


import com.example.chat95.data.Keys;
import com.example.chat95.data.PrivateKey;
import com.example.chat95.data.PublicKey;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        N = p.multiply(q);  // N=p*q
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // phi = (p-1)(q-1)
        e = BigInteger.probablePrime(bitlength / 2, r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);    // calculate d so: d*e=1 mod(n)
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
        int bitLength = 1024;
        Random r = new Random();
        BigInteger p = BigInteger.probablePrime(bitLength, r);
        BigInteger q = BigInteger.probablePrime(bitLength, r);
        BigInteger N = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.probablePrime(bitLength / 2, r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0) {
            e.add(BigInteger.ONE);
        }
        BigInteger d = e.modInverse(phi);
        return new Keys(new PublicKey(e.toString(),N.toString()),new PrivateKey(p.toString(),q.toString(),d.toString()));
    }

    // Encrypt message using public key
    public static String encrypt(String text, PublicKey foreignPublicKey) {
        // TODO: 02/06/2020
        BigInteger e = new BigInteger(foreignPublicKey.getE());
        BigInteger n = new BigInteger(foreignPublicKey.getN());

        String str = null;
        try {
            str = new String((new BigInteger(text.getBytes("ISO-8859-1"))).modPow(e, n).toByteArray(),"ISO-8859-1");
        } catch (UnsupportedEncodingException er) {
            er.printStackTrace();
        }
        return str;
    }

    // Encrypt message using private key
    public static String encrypt(String text, PrivateKey privateKey) {
        // TODO: 02/06/2020
        BigInteger d = new BigInteger(privateKey.getD());
        BigInteger n = new BigInteger(privateKey.getN());
        String str = null;
        try {
            str = new String((new BigInteger(text.getBytes("ISO-8859-1"))).modPow(d.modInverse(n), n).toByteArray(),"ISO-8859-1");
        } catch (UnsupportedEncodingException er) {
            er.printStackTrace();
        }
        return str;

        // return text + "$$";
    }

    // Decrypt message using private key
    public static String decrypt(String text, PrivateKey privateKey) {
        // TODO: 02/06/2020
        BigInteger d = new BigInteger(privateKey.getD());
        BigInteger n = new BigInteger(privateKey.getN());

        String str = null;
        try {
            str = new String((new BigInteger(text.getBytes("ISO-8859-1"))).modPow(d, n).toByteArray(),"ISO-8859-1");
        } catch (UnsupportedEncodingException er) {
            er.printStackTrace();
        }
        return str;
    }


    // Decrypt message using public key
    public static String decrypt(String text, PublicKey foreignPublicKey) {
        // TODO: 02/06/2020
        BigInteger e = new BigInteger(foreignPublicKey.getE());
        BigInteger n = new BigInteger(foreignPublicKey.getN());
        String str = null;
        try {
            str = new String((new BigInteger(text.getBytes("ISO-8859-1"))).modPow(e.modInverse(n), n).toByteArray(),"ISO-8859-1");
        } catch (UnsupportedEncodingException er) {
            er.printStackTrace();
        }
        return str;
        // return text.substring(0, text.length() - 2);
    }
    /*// Encrypt message
    public byte[] encrypt(byte[] message) {
        return (new BigInteger(message)).modPow(e, N).toByteArray();
    }

    // Decrypt message
    public byte[] decrypt(byte[] message) {
        return (new BigInteger(message)).modPow(d, N).toByteArray();
    }*/

    public static String getCryptoHash(String input, String algorithm) {
        try {
            //MessageDigest classes Static getInstance method is called with MD5 hashing
            MessageDigest msgDigest = MessageDigest.getInstance(algorithm);

            //digest() method is called to calculate message digest of the input
            //digest() return array of byte.
            byte[] inputDigest = msgDigest.digest(input.getBytes());

            // Convert byte array into signum representation
            // BigInteger class is used, to convert the resultant byte array into its signum representation
            BigInteger inputDigestBigInt = new BigInteger(1, inputDigest);

            // Convert the input digest into hex value
            String hashtext = inputDigestBigInt.toString();

            //Add preceding 0's to pad the hashtext to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        // Catch block to handle the scenarios when an unsupported message digest algorithm is provided.
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String signature(String textMessage, PrivateKey privateKey) {
        // TODO: 02/06/2020
        String hashedMessage = getCryptoHash(textMessage, "MD5");
        return encrypt(hashedMessage, privateKey);
       // return KeyGenerator.generateKey(6);
    }

    public static boolean verify(String textMessage, String signature, PublicKey foreignPublicKey) {
        // TODO: 02/06/2020
        String hashedMessage = getCryptoHash(textMessage, "MD5");
        String expectedHashMessage = decrypt(signature, foreignPublicKey);
        return hashedMessage.equals(expectedHashMessage);
    }
}