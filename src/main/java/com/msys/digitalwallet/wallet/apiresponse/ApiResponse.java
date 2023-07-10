package com.msys.digitalwallet.wallet.apiresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashMap;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private Map<String, Object> result;

    public ApiResponse(boolean success, String message, String beanName, Object bean) {
        this.success = success;
        this.message = message;
        this.result = new HashMap<>();
        this.result.put(beanName, bean);
    }

}
