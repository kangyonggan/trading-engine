package com.kangyonggan.tradingEngine.util;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @author kyg
 */
public final class GoogleAuthenticator {

    private static final int SECRET_SIZE = 10;

    private static final String SEED = "g8GjEvTbW5oVSV7avLBdwIHqGlUYNzKFI7izOF8GwLDVKs2m0QN7vxRs2im5MDaNCWGmcD2rvcZx";

    private static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

    private static final int WINDOW_SIZE = 3;

    public static String generateSecretKey() throws Exception {
        SecureRandom sr;
        sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
        sr.setSeed(Base64.decodeBase64(SEED));
        byte[] buffer = sr.generateSeed(SECRET_SIZE);
        Base32 codec = new Base32();
        byte[] bEncodedKey = codec.encode(buffer);
        return new String(bEncodedKey);
    }

    public static String getQRBarcodeURL(String user, String host, String secret) {
        String format = "otpauth://totp/%s?secret=%s&issuer=%s";
        return String.format(format, user, secret, host);
    }

    public static boolean checkCode(String secret, long code, long timeMsec) {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
        long t = (timeMsec / 1000L) / 30L;
        for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {
            long hash;
            try {
                hash = verifyCode(decodedKey, t + i);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (hash == code) {
                return true;
            }
        }
        return false;
    }

    private static int verifyCode(byte[] key, long t) throws Exception {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
        return (int) truncatedHash;
    }

}
