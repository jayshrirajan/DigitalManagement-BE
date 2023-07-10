package com.msys.digitalwallet.common.exception;


import com.msys.digitalwallet.wallet.enums.ErrorType;

public class BusinessException extends RuntimeException {

    private ErrorType errorType;

    public BusinessException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return this.errorType;
    }
}
