package com.example;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

/**
 * AESC加密解密
 * <p>
 * modify by mtf on 2017/8/17.
 */
public class AESCrypt {

    /**
     * AESC秘钥
     */
    public static final String AESKEY = "KEY68#^%@*)P*H)Q874$@}Z?]287[ers";

    /**
     * AESC秘钥(绵阳地区)
     */
    public static final String MAESCKEY = "MYKEY85^%#@*)S*U)N620$}@H?]18[9z";

    /**
     * AESC偏移量
     */
    public static final byte[] iv = {16, 1, 15, 2, 14, 3, 13, 4, 12, 5, 11, 6, 10, 7, 9, 8,};


    private static Cipher cipher = null;
    private static SecretKeySpec key;
    private static AlgorithmParameterSpec spec;
    public static final String SEED_16_CHARACTER = AESKEY;

    public AESCrypt() {
        try {
            // hash password with SHA-256 and crop the output to 128-bit for key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(SEED_16_CHARACTER.getBytes("UTF-8"));
            byte[] keyBytes = new byte[32];
            System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            key = new SecretKeySpec(keyBytes, "AES");
            spec = getIV();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AESCrypt(String area) {
        try {
            // hash password with SHA-256 and crop the output to 128-bit for key
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            if (area.equals("mianyang")) {
                digest.update(MAESCKEY.getBytes("UTF-8"));
            }
            byte[] keyBytes = new byte[32];
            System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            key = new SecretKeySpec(keyBytes, "AES");
            spec = getIV();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AlgorithmParameterSpec getIV() {
        IvParameterSpec ivParameterSpec;
        ivParameterSpec = new IvParameterSpec(iv);
        return ivParameterSpec;
    }

    /**
     * AESC加密算法
     *
     * @param plainText
     * @return
     * @throws Exception
     */
    public String encrypt(String plainText) {
        String encryptedText = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
            encryptedText = new BASE64Encoder().encode(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText.trim();
    }

    /**
     * AESC解密算法
     *
     * @param cryptedText
     * @return
     * @throws Exception
     */
    public String decrypt(String cryptedText) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            String encode = new BASE64Encoder().encode(cryptedText.getBytes());
            byte[] decrypted = cipher.doFinal(encode.getBytes());
            decryptedText = new String(decrypted, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
}