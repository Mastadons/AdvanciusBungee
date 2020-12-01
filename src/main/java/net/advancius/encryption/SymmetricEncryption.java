package net.advancius.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class SymmetricEncryption {

    public final static int SALT_LENGTH = 12;
    public final static String ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding";

    public static SecretKey decodeSecretKey(String encoded) {
        try {
            byte[] encoded0 = encoded.getBytes(StandardCharsets.UTF_8);
            return new SecretKeySpec(encoded0, 0, encoded0.length, "AES");
        } catch (Exception exception) { throw new EncryptionException(exception); }
    }

    public static byte[] generateRandomSalt() {
        byte[] nonce = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, SecureRandom.getInstanceStrong());
            return keyGen.generateKey();
        } catch (Exception exception) { throw new EncryptionException(exception); }
    }

    public static byte[] encrypt(String data, SecretKey secret, byte[] salt) {
        return encrypt(data.getBytes(StandardCharsets.UTF_8), secret, salt);
    }

    public static byte[] encrypt(byte[] data, SecretKey secret, byte[] salt) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(SALT_LENGTH * 8, salt));
            byte[] encryptedText = cipher.doFinal(data);
            return encryptedText;
        } catch (Exception exception) { throw new EncryptionException(exception); }
    }

    public static String decrypt(byte[] data, SecretKey secret, byte[] salt) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(SALT_LENGTH * 8, salt));
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception exception) { throw new EncryptionException(exception); }
    }
}
