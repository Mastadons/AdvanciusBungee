package net.advancius.encryption;

import lombok.Data;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AsymmetricEncryption {

    public static PublicKey decodePublicKey(String encoded) {
        try {
            byte[] encoded0 = encoded.getBytes(StandardCharsets.UTF_8);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encoded0));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception exception) { throw new EncryptionException(exception); }
    }

    public static PrivateKey decodePrivateKey(String encoded) {
        try {
            byte[] encoded0 = encoded.getBytes(StandardCharsets.UTF_8);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(encoded0));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception exception) { throw new EncryptionException(exception); }
    }

    public static String encodePublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String encodePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static String decrypt(String data, String encodedPrivateKey) {
        return decrypt(Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8)), decodePrivateKey(encodedPrivateKey));
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(data));
        } catch (Exception exception) { throw new EncryptionException(exception); }
    }

    public static byte[] encrypt(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) { throw new EncryptionException(exception); }
    }

    @Data
    public static class AsymmetricEncryptionKeypair {

        private final PrivateKey privateKey;
        private final PublicKey publicKey;

        public static AsymmetricEncryptionKeypair generateKeypair() throws NoSuchAlgorithmException {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            return new AsymmetricEncryptionKeypair(keyPair.getPrivate(), keyPair.getPublic());
        }

        public static AsymmetricEncryptionKeypair generateKeypair(String encodedPrivate, String encodedPublic) {
            return new AsymmetricEncryptionKeypair(
                    AsymmetricEncryption.decodePrivateKey(encodedPrivate),
                    AsymmetricEncryption.decodePublicKey(encodedPublic));
        }

        public String getPrivateBase64() {
            return Base64.getEncoder().encodeToString(privateKey.getEncoded());
        }

        public String getPublicBase64() {
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        }
    }
}
