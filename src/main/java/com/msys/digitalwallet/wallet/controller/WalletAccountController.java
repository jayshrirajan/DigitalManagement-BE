package com.msys.digitalwallet.wallet.controller;

import com.msys.digitalwallet.wallet.apirequest.ApiUserWalletAccountUpdateRequest;
import com.msys.digitalwallet.wallet.apirequest.ApiWalletTransferRequest;
import com.msys.digitalwallet.wallet.apiresponse.ApiTransfer;
import com.msys.digitalwallet.wallet.apiresponse.ApiUserWalletAccount;
import com.msys.digitalwallet.wallet.apiresponse.ApiWalletTransferResponse;
import com.msys.digitalwallet.wallet.model.PaymentRequest;
import com.msys.digitalwallet.wallet.model.Transfer;
import com.msys.digitalwallet.wallet.service.PlaidService;
import com.msys.digitalwallet.wallet.service.WalletAccountService;
import com.plaid.client.model.PaymentInitiationPaymentCreateResponse;
import com.plaid.client.model.WalletTransactionExecuteRequest;
import com.plaid.client.model.WalletTransactionExecuteResponse;
import com.plaid.client.model.WalletTransactionStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/wallet")
@CrossOrigin(maxAge = 3600)
public class WalletAccountController {

    @Autowired
    private WalletAccountService walletAccountService;
    @Autowired
    private PlaidService plaidService;

  @PostMapping("/account")
  @ResponseStatus(value = HttpStatus.CREATED)
  public Mono<ApiUserWalletAccount> createWallet(@RequestHeader("user-id") String userId)
      throws IOException {
    return walletAccountService.createWalletAccount(userId);
    }

  @PutMapping("/account")
  public Mono<ApiUserWalletAccount> editWallet(
      @RequestHeader("user-id") String userId,
      @RequestBody @Valid ApiUserWalletAccountUpdateRequest request) {
        return walletAccountService.editWalletAccount(userId, request);
    }

  @GetMapping("/account")
  public Mono<ApiUserWalletAccount> getWallet(
      @RequestHeader("user-id") String userId) {
        return walletAccountService.fetchWalletAccount(userId);
    }

    @PutMapping("/account/deposit/{walletId}")
    public Mono<ApiUserWalletAccount> depositAmount(
            @PathVariable("walletId") String walletAccountId,
            @RequestBody @Valid ApiUserWalletAccountUpdateRequest request) {
        return walletAccountService.depositAmount(walletAccountId, request.getAmount());
    }

    @PostMapping("/payment/PayBank-toWallet")
    public Mono<PaymentInitiationPaymentCreateResponse> createPaymentRequest(@RequestBody PaymentRequest paymentrequest) throws IOException {
        return plaidService.createPayment(paymentrequest);

    }

    @PostMapping("/payment/wallet-to-wallet")
    public Mono<ApiWalletTransferResponse> initiateWalletTransfer(@RequestHeader("user-id") String userId,@RequestBody ApiWalletTransferRequest apiWalletTransferRequest) throws IOException {
        return walletAccountService.initiateWalletTransfer(userId,apiWalletTransferRequest);

    }

  @DeleteMapping("/redis/delete/{redis-key}")
  public void deleteRedisCache(@PathVariable("redis-key") String key) throws IOException {
         walletAccountService.deleteRedisKey(key);

    }


    @GetMapping("/transactions/{transactionId}")
    public ApiTransfer getTransactionDetails(@PathVariable int transactionId) {
        ApiTransfer transaction = walletAccountService.getTransactionById(transactionId);
        return transaction;
    }

  @GetMapping("/transactions")
  public List<ApiTransfer> getAllTransactionsForUser(@RequestParam String userId) {
        List<ApiTransfer> transactions = walletAccountService.getAllTransactionsFoUser(userId);
        return transactions;
    }



    @PostMapping("/payment/Wallet-toBank")
    public Mono<WalletTransactionExecuteResponse> createWalletToBankPaymentRequest(
            @RequestHeader("user-id") String userId,
            @RequestBody WalletTransactionExecuteRequest walletTransactionExecuteRequest) throws IOException {
        final String requestID = walletTransactionExecuteRequest.getIdempotencyKey();
        return  walletAccountService.executeWalletToBankTransfer(userId, walletTransactionExecuteRequest).flatMap(
                (HashMap<String,Object> dbEntryMap) ->{
                    return Mono.just(new WalletTransactionExecuteResponse(){{
                        setRequestId(requestID);
                        setStatus(WalletTransactionStatus.EXECUTED);
                        setTransactionId(String.valueOf(((Transfer)dbEntryMap.get("TRANSFER")).getTransactionID()));
                    }});
                }
        );


    }

    @PostMapping("/payment/bank-to-wallet")
    public Mono<WalletTransactionExecuteResponse> createBankToWalletPaymentRequest(
            @RequestHeader("user-id") String userId,
            @RequestBody WalletTransactionExecuteRequest walletTransactionExecuteRequest) throws IOException {
        final String requestID = walletTransactionExecuteRequest.getIdempotencyKey();
        return walletAccountService.executeBankToWalletTransfer(userId, walletTransactionExecuteRequest).flatMap(
                (HashMap<String, Object> dbEntryMap) -> {
                    return Mono.just(new WalletTransactionExecuteResponse() {{
                        setRequestId(requestID);
                        setStatus(WalletTransactionStatus.EXECUTED);
                        setTransactionId(String.valueOf(((Transfer) dbEntryMap.get("TRANSFER")).getTransactionID()));
                    }});
                }
        );
    }


    }
