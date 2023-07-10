package com.msys.digitalwallet.wallet.controller;


import com.msys.digitalwallet.wallet.apirequest.LinkBankAccountRequest;
import com.msys.digitalwallet.wallet.apirequest.LinkBankAccountUserIdRequest;
import com.msys.digitalwallet.wallet.apiresponse.ApiResponse;
import com.msys.digitalwallet.wallet.filter.LoggingFilter;
import com.msys.digitalwallet.wallet.model.UserBankAccount;
import com.msys.digitalwallet.wallet.service.LinkAccountService;
import com.msys.digitalwallet.wallet.service.RedisService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/digital-wallet")
@CrossOrigin(maxAge = 3600)
public class LinkAccountController {

    @Autowired
    private LinkAccountService linkAccountService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @PostMapping("/link-bank-account")
    public ApiResponse saveUserBankAccount(@RequestBody @Valid LinkBankAccountRequest request) {
        LOGGER.info("saveUserBankAccount controller methods starts");
        Map<String, Object> errorMap = new HashMap<>();
        try {
            Mono<Map<String, Object>> errorMapMono = linkAccountService.checkUserIdExists(request.getUserId());
            if (errorMapMono.block().get("error") == null) {
                Mono<UserBankAccount> userBankAccountMono = linkAccountService.saveUserBankAccount(request);
                LOGGER.info("saveUserBankAccount controller methods ends");
                return new ApiResponse(true, "User bank account created successfully", "data", userBankAccountMono.block());
            } else {
                return new ApiResponse(false, "Error occurred ", "data", errorMapMono.block());
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Error has occurred", "data", errorMap);
        }
    }

    @GetMapping("/userId/{userId}")
    public ApiResponse getUserBankAccountsByUserId(@PathVariable String userId) {
        LOGGER.info("getUserBankAccountsByUserId controller methods starts");
        Map<String, Object> errorMap = new HashMap<>();
        try {
            Mono<Map<String, Object>> errorMapMono = linkAccountService.checkUserIdExists(userId);
            if (errorMapMono.block().get("error") == null) {
                Flux<UserBankAccount> userBankAccountMono = linkAccountService.getUserActiveBankAccountsByUserId(userId);
                return userBankAccountMono.collectList().flatMap(userBankData -> {
                    LOGGER.info("getUserBankAccountsByUserId controller methods ends");
                    LOGGER.debug("get all user bank account details {}", userBankData);
                    return Mono.just(new ApiResponse(true, "Users bank account details has been fetched successfully", "data", userBankData));
                }).block();
            } else {
                return new ApiResponse(false, "Error occurred ", "data", errorMapMono.block());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, "Error has occurred", "data", errorMap);
        }
    }
    @DeleteMapping("/accountId/{accountId}")
    public ApiResponse deleteBankAccountsByUserId(@PathVariable String accountId) {
                linkAccountService.deleteByBankAccountId(accountId);
            return new ApiResponse(true, "Account deleted successfully ",
                    "data",null);
            }

    @DeleteMapping("/redis/delete/{userId}")
    public void deleteUserId(@PathVariable("userId") String userId) {
        linkAccountService.deleteRedisKey(userId);
    }
}
