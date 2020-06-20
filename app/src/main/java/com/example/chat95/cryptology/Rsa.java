package com.example.chat95.cryptology;

import com.example.chat95.data.Keys;
import com.example.chat95.data.PrivateKey;
import com.example.chat95.data.PublicKey;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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

    // Decrypt message using private key
    public static String decrypt(String text, PrivateKey privateKey) {
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

    public static String getCryptoHash(String input, String algorithm) {
        try {
            //MessageDigest classes Static getInstance method is called with MD5 hashing
            MessageDigest msgDigest = MessageDigest.getInstance(algorithm);
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
        String hashedMessage = getCryptoHash(textMessage, "SHA-512");

        // Encryption of the hashMessage using a private key
        BigInteger d = new BigInteger(privateKey.getD());
        BigInteger n = new BigInteger(privateKey.getN());
        String str = null;
        try {
            str = new String((new BigInteger(hashedMessage.getBytes("ISO-8859-1"))).modPow(d, n).toByteArray(),"ISO-8859-1");
        } catch (UnsupportedEncodingException er) {
            er.printStackTrace();
        }
        return str;
        //return encrypt(hashedMessage, privateKey);
    }

    public static boolean verify(String textMessage, String signature, PublicKey foreignPublicKey) {
        String hashedMessage = getCryptoHash(textMessage, "SHA-512");

        // Decryption of the signature using a public key
        BigInteger e = new BigInteger(foreignPublicKey.getE());
        BigInteger n = new BigInteger(foreignPublicKey.getN());
        String expectedHashMessage = null;
        try {
            expectedHashMessage = new String((new BigInteger(signature.getBytes("ISO-8859-1"))).modPow(e, n).toByteArray(),"ISO-8859-1");
        } catch (UnsupportedEncodingException er) {
            er.printStackTrace();
        }
       // String expectedHashMessage = decrypt(signature, foreignPublicKey);
        return hashedMessage.equals(expectedHashMessage);
    }
}