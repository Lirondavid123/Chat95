package com.example.chat95.cryptology;


import android.util.Log;

import com.example.chat95.data.Keys;
import com.example.chat95.data.PrivateKey;
import com.example.chat95.data.PublicKey;
import com.google.common.hash.Hashing;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import static android.content.ContentValues.TAG;


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
            e = e.add(BigInteger.ONE);
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
            str = new String((new BigInteger(text.getBytes("ISO-8859-1"))).modPow(d, n).toByteArray(),"ISO-8859-1");
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
            str = new String((new BigInteger(text.getBytes("ISO-8859-1"))).modPow(e, n).toByteArray(),"ISO-8859-1");
        } catch (UnsupportedEncodingException er) {
            er.printStackTrace();
        }
        Log.d(TAG, "onStart: decrypt(public key) str "+str);
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

    public static byte[] getSHA(String input)
    {
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // Static getInstance method is called with hashing SHA


        // digest() method called
        // to calculate message digest of an input
        // and return array of byte

    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public static String getCryptoHash(String input, String algorithm) {
        try {
            Log.d(TAG, "onStart: getCryptoHash input "+input);
            //MessageDigest classes Static getInstance method is called with MD5 hashing
            MessageDigest msgDigest = MessageDigest.getInstance(algorithm);
//            byte[] inputDigest = null;
//            msgDigest.reset();
//            msgDigest.update(input.getBytes(Charset.forName("ISO-8859-1")), 0, input.length());
//            inputDigest = msgDigest.digest();
//            try {
//                //msgDigest.update(input.getBytes("UTF-8"));
//                msgDigest.reset();
//                msgDigest.update(input.getBytes(Charset.forName("utf-8")), 0, input.length());
//                inputDigest = msgDigest.digest();
//                //inputDigest = msgDigest.digest(input.getBytes("UTF-8"));
//                Log.d(TAG, "onStart: getCryptoHash inputDigest "+ Arrays.toString(inputDigest));
//            } catch (UnsupportedEncodingException er) {
//                er.printStackTrace();
//            }

           // Log.d(TAG, "onStart: getCryptoHash msgDigest "+msgDigest.toString());
            //digest() method is called to calculate message digest of the input
            //digest() return array of byte.
            byte[] inputDigest = msgDigest.digest(input.getBytes());
//            byte[] inputDigest = msgDigest.digest();
            Log.d(TAG, "onStart: getCryptoHash inputDigest "+ Arrays.toString(inputDigest));
            String hashed = Base64.getEncoder().encodeToString(inputDigest);
            Log.d(TAG, "onStart: getCryptoHash hashed "+hashed);
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
        Log.d(TAG, "onStart: signature textMessage "+textMessage);
        //String hashedMessage = toHexString(getSHA(textMessage));
//        String hashedMessage = Hashing.sha256()
//                .hashString(textMessage, StandardCharsets.UTF_8)
//                .toString();
        String hashedMessage = getCryptoHash(textMessage, "SHA-512");
        Log.d(TAG, "onStart: signature hashedMessage "+hashedMessage);
        Log.d(TAG, "onStart: signature privateKey D "+privateKey.getD());
        Log.d(TAG, "onStart: signature privateKey Q "+privateKey.getQ());
        Log.d(TAG, "onStart: signature privateKey P "+privateKey.getP());
        Log.d(TAG, "onStart: signature privateKey N "+privateKey.getN());
        return encrypt(hashedMessage, privateKey);
       // return KeyGenerator.generateKey(6);
    }

    public static boolean verify(String textMessage, String signature, PublicKey foreignPublicKey) {
        // TODO: 02/06/2020
        Log.d(TAG, "onStart: verify textMessage "+textMessage);
       // String hashedMessage = toHexString(getSHA(textMessage));
//        String hashedMessage = Hashing.sha256()
//                .hashString(textMessage, StandardCharsets.UTF_8)
//                .toString();
        String hashedMessage = getCryptoHash(textMessage, "SHA-512");
        Log.d(TAG, "onStart: verify hashedMessage "+hashedMessage);
        Log.d(TAG, "onStart: verify signature "+signature);
        String expectedHashMessage = decrypt(signature, foreignPublicKey);
        Log.d(TAG, "onStart: verify foreignPublicKey e "+foreignPublicKey.getE());
        Log.d(TAG, "onStart: verify foreignPublicKey n "+foreignPublicKey.getN());
        Log.d(TAG, "onStart: verify expectedHashMessage "+expectedHashMessage);
        return hashedMessage.equals(expectedHashMessage);
    }
}