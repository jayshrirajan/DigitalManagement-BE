package com.msys.digitalwallet.wallet.controller;

import com.msys.digitalwallet.wallet.apirequest.*;
import com.msys.digitalwallet.wallet.apiresponse.ApiResponse;
import com.msys.digitalwallet.wallet.filter.LoggingFilter;
import com.msys.digitalwallet.wallet.model.UserBankAccount;
import com.msys.digitalwallet.wallet.service.LinkAccountService;
import com.msys.digitalwallet.wallet.service.PlaidService;
import com.plaid.client.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/plaid-service")
@CrossOrigin(maxAge = 3600)
public class PlaidController {
    @Autowired
    private PlaidService plaidService;

    @Autowired
    private LinkAccountService linkAccountService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @PostMapping("/link-token")
    public ApiResponse getPlaidLinkToken(@RequestBody @Valid PlaidLinkTokenRequest request) throws IOException {
        LOGGER.info("getPlaidLinkToken controller method started");
        Mono<Map<String,Object>> linkToken = plaidService.getPlaidLinkToken(request.getUserId());
        LOGGER.info("getPlaidLinkToken controller method ends");
        if (linkToken.block().get("error") == null){
            return new ApiResponse(true, "Link token generated successfully", "data", linkToken.block());
        }else{
            return new ApiResponse(false, "Error occurred", "data", linkToken.block());
        }
    }

    @PostMapping("/public-token")
    public ApiResponse getPublicToken() throws IOException {
        LOGGER.info("getPublicToken controller method started");
        Mono<Map<String,Object>> publicToken = plaidService.getPublicToken();
        LOGGER.info("getPublicToken controller method ends");
        if (publicToken.block().get("error") == null){
            return new ApiResponse(true, "Public token generated successfully", "data", publicToken.block());
        }else {
            return new ApiResponse(false, "Error occurred", "data", publicToken.block());
        }
    }

    @PostMapping("/access-token")
    public ApiResponse getAccessToken(@RequestBody @Valid PlaidAccessTokenRequest request) throws IOException {
        LOGGER.info("getAccessToken controller method started");
        Mono<Map<String,Object>> accessToken = plaidService.getAccessToken(request.getPublicToken());
        LOGGER.info("getAccessToken controller method ends");
        if (accessToken.block().get("error") == null){
            return new ApiResponse(true, "Access token generated successfully", "data", accessToken.block());
        }else {
            return new ApiResponse(false, "Error occurred", "data", accessToken.block());
        }
    }

    @PostMapping("/account-details")
    public ApiResponse getPlaidAccountDetails(@RequestBody @Valid PlaidBankAccountRequest request) throws IOException {
        LOGGER.info("getPlaidAccountDetails controller method started");
        Mono<AccountsGetResponse> plaidServiceAccountDetails = plaidService.getAccountDetails(request.getAccessToken());
        LOGGER.info("getPlaidAccountDetails controller method ends");
        return new ApiResponse(true, "Plaid bank account data", "data", plaidServiceAccountDetails.block());
    }


    @PostMapping("/link-bank-account")
    public ApiResponse saveUserBankAccount(@RequestBody @Valid LinkBankAccountRequest request) {
        LOGGER.info("saveUserBankAccount controller methods starts");
        Map<String, Object> errorMap = new HashMap<>();
        try {
            Mono<Map<String, Object>>errorMapMono = linkAccountService.checkUserIdExists(request.getUserId());
            if (errorMapMono.block().get("error") == null){
                Mono<UserBankAccount> userBankAccountMono = linkAccountService.saveUserBankAccount(request);
                LOGGER.info("saveUserBankAccount controller methods ends");
                return new ApiResponse(true, "User bank account created successfully", "data",  userBankAccountMono.block());
            }else {
                return new ApiResponse(false, "Error occurred ", "data",  errorMapMono.block());
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Error has occurred", "data", errorMap);
        }
    }

    @GetMapping("/userId")
    public ApiResponse getUserBankAccountsByUserId(@RequestBody @Valid LinkBankAccountUserIdRequest request) {
        LOGGER.info("getUserBankAccountsByUserId controller methods starts");
        Map<String, Object> errorMap = new HashMap<>();
        try {
            Mono<Map<String, Object>>errorMapMono = linkAccountService.checkUserIdExists(request.getUserId());
            if (errorMapMono.block().get("error") == null){
                Flux<UserBankAccount> userBankAccountMono = linkAccountService.getUserBankAccountsByUserId(request.getUserId());
                return userBankAccountMono.collectList().flatMap(userBankData -> {
                    LOGGER.info("getUserBankAccountsByUserId controller methods ends");
                    LOGGER.debug("get all user bank account details {}",userBankData);
                    return Mono.just(new ApiResponse(true, "Users bank account details has been fetched successfully", "data", userBankData));
                }).block();
            }else {
                return new ApiResponse(false, "Error occurred ", "data",  errorMapMono.block());
            }
        }catch (Exception e) {
            return new ApiResponse(false, "Error has occurred", "data", errorMap);
        }
    }

    @GetMapping("/institution/{institutionId}")
    public ApiResponse getInstitutionById(@PathVariable @NotNull @NotEmpty String institutionId) {
        LOGGER.info("getInstitutionById controller methods starts");
        Map<String, Object> errorMap = new HashMap<>();
        try {
            Mono<Institution> institution = plaidService.getInstitutionById(institutionId);
            return new ApiResponse(true, "Institution Details", "data", institution.block());
        } catch (Exception e) {
            return new ApiResponse(false, "Error has occurred", "data", errorMap);
        }
    }
}
