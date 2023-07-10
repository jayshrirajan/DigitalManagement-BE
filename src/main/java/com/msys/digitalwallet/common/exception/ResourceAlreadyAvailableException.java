package com.msys.digitalwallet.common.exception;

public class ResourceAlreadyAvailableException extends RuntimeException {
    public ResourceAlreadyAvailableException(String message) {
        super(message);
    }
}