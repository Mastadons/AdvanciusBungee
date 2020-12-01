package net.advancius.encryption;

import lombok.Data;

@Data
public class EncryptionException extends RuntimeException {

    public EncryptionException(Exception internalException) {
        super(internalException);
    }
}
