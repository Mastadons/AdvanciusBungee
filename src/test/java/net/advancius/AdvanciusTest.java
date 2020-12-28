package net.advancius;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AdvanciusTest {

    public static void main(String[] arguments) throws IOException, NoSuchAlgorithmException {
        for (int i = 0; i < 100; i++) {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128, SecureRandom.getInstanceStrong());
            SecretKey key = keyGen.generateKey();
            System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
        }
    }
}
