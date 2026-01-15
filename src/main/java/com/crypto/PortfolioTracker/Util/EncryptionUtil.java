package com.crypto.PortfolioTracker.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtil {

    @Value("${app.encryption.secret}")
    private String secretKey;

    private final String ALGORITHM = "AES";

    public String encrypt(String data) {

        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte [] encryptedBytes = cipher.doFinal(data.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting", e);
        }
    }

    public String decrypt(String encryptedData) {

        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte [] rawEncryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte [] decryptedBytes = cipher.doFinal(rawEncryptedBytes);

            return new String(decryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error while decryption", e);
        }
    }
}