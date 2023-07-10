package com.msys.digitalwallet.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {

    List<String> messages;
    public ValidationException(List<String> messages) {
        super(String.join(",", messages));
        this.messages = messages;
    }

    public ValidationException() {

    }
}