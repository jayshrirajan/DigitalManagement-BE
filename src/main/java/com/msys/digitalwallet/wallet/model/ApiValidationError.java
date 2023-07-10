package com.msys.digitalwallet.wallet.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiValidationError {
    private String field;
    private Object rejectedValue;
    private String message;

    public ApiValidationError(Object rejectedValue, String message) {
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public ApiValidationError(String message) {
        this.message = message;
    }
}
