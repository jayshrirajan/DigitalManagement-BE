package com.msys.digitalwallet.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    List<String> messages;

    public ResourceNotFoundException(String message) {
        super(message);
    }

}