package com.msys.digitalwallet.usermanagement.utils;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    private BasicTextEncryptor textEncryptor = null;

    public EncryptionUtil() {

        textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword("MSYS-TECHNOLOGY");
    }

    public String encrypt(String textToEncrypt) {
        return this.textEncryptor.encrypt(textToEncrypt);
    }

    public String decrypt(String encryptedText) {
        return this.textEncryptor.decrypt(encryptedText);
    }
}
