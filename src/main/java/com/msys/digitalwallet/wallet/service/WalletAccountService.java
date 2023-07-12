package com.msys.digitalwallet.wallet.service;

import com.msys.digitalwallet.common.exception.BusinessException;
import com.msys.digitalwallet.common.exception.ResourceNotFoundException;
import com.msys.digitalwallet.common.exception.ValidationException;
import com.msys.digitalwallet.integration.WalletIntegrationConfig;
import com.msys.digitalwallet.integration.notification.NotificationIntegration;
import com.msys.digitalwallet.integration.notification.request.ApiNotificationRequest;
import com.msys.digitalwallet.integration.notification.request.Channel;
import com.msys.digitalwallet.integration.plaidservice.PlaidVirtualAccount;
import com.msys.digitalwallet.integration.plaidservice.response.VirtualAccountResponse;
import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.service.UserService;
import com.msys.digitalwallet.wallet.apirequest.ApiUserWalletAccountUpdateRequest;
import com.msys.digitalwallet.wallet.apirequest.ApiWalletTransferRequest;
import com.msys.digitalwallet.wallet.apiresponse.ApiTransfer;
import com.msys.digitalwallet.wallet.apiresponse.ApiUserWalletAccount;
import com.msys.digitalwallet.wallet.apiresponse.ApiWalletTransferResponse;
import com.msys.digitalwallet.wallet.enums.*;
import com.msys.digitalwallet.wallet.mapper.TransferMapper;
import com.msys.digitalwallet.wallet.mapper.WalletAccountMapper;
import com.msys.digitalwallet.wallet.model.*;
import com.msys.digitalwallet.wallet.repository.*;
import com.plaid.client.model.WalletTransactionExecuteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
//My changes

@Service
@Slf4j
public class WalletAccountService {

    @Autowired
    private UserWalletAccountRepository userWalletAccountRepository;

    @Autowired
    private WalletAccountMapper walletAccountMapper;

    @Autowired
    private TransferMapper transferMapper;

    @Autowired
    private PlaidVirtualAccount plaidVirtualAccount;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private NotificationIntegration notificationIntegration;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private LinkAccountRepository linkAccountRepository;

    @Autowired
    private WalletIntegrationConfig walletIntegrationConfig;


    /**
     * Create the wallet account for the given user
     *
     * @param userId
     * @return ApiUserWalletAccount created wallet account details
     */
    public Mono<ApiUserWalletAccount> createWalletAccount(String userId) throws IOException {

        Mono<UserWalletAccount> userWalletAccount = userWalletAccountRepository.findByUserId(userId);

        if (userWalletAccount != null && userWalletAccount.block() != null && userWalletAccount.block().getId() != null) {
            throw new BusinessException(
                    ErrorType.WALLET_ALREADY_EXISTS, "Wallet already created for this user");
        }
        Mono<ApiUserWalletAccount> apiUserWalletAccount =
                createWallet(userId)
                        .map(wallet -> {
                            ApiUserWalletAccount apiUserWallet = walletAccountMapper.toApiUserWalletAccount(wallet);
                            redisService.setValue(apiUserWallet.getUserId(), apiUserWallet);
                            return apiUserWallet;
                        });


        Optional<User> userOptional = userService.findUser(userId);
        userOptional.ifPresent(
                user -> {
                    ApiNotificationRequest request =
                            ApiNotificationRequest.builder()
                                    .identifier(user.getEmailAddress())
                                    .subject("Welcome to MSys Wallet")
                                    .message("Wallet created for you!!!")
                                    .channel(Channel.email)
                                    .build();
                    notificationIntegration.sendNotification(request);
                });

        return apiUserWalletAccount;
    }

    private Mono<UserWalletAccount> createWallet(String userId) throws IOException {
        VirtualAccountResponse virtualAccountResponse = plaidVirtualAccount.createWallet();

        log.info("virtualAccountResponse: " + virtualAccountResponse);

        if (virtualAccountResponse == null)
            return Mono.error(
                    new BusinessException(
                            ErrorType.WALLET_CREATE_FAILED, "Error while creating wallet account on plain"));

        UserWalletAccount newUserWalletAccount =
                walletAccountMapper.toUserWalletAccount(userId, virtualAccountResponse);

        return userWalletAccountRepository.save(newUserWalletAccount);
    }

    /**
     * Update the wallet account details
     *
     * @param walletUserId    userId
     * @param request         wallet details for update
     * @return ApiUserWalletAccount updated wallet account details
     */
    public Mono<ApiUserWalletAccount> editWalletAccount(
            String walletUserId, ApiUserWalletAccountUpdateRequest request) {

        String walletStatus = request.getStatus().name();
        Mono<UserWalletAccount> userWalletAccount =
                userWalletAccountRepository.findByUserId(walletUserId);

        Mono<ApiUserWalletAccount> apiUserWalletAccount =
         userWalletAccount
                .flatMap(
                  wallet -> {
                      // is update needed
                     if (walletStatus.equals(wallet.getStatus()))
                       return Mono.error(
                          new BusinessException(
                             ErrorType.WALLET_UPDATE_FAILED,
                             String.format("Wallet is in already %s status", walletStatus)));
                      // wallet update
                      wallet.setStatus(walletStatus);
                     return userWalletAccountRepository.save(wallet);
                    })
             .map(
                 wallet -> {
                   ApiUserWalletAccount apiUserWallet =
                        walletAccountMapper.toApiUserWalletAccount(wallet);
                    redisService.setValue(apiUserWallet.getUserId(), apiUserWallet);
                      return apiUserWallet;
                    })
                .switchIfEmpty(ifWalletNotExist(walletUserId));

        return apiUserWalletAccount;
    }

    /**
     * Get the wallet details for the given user
     *
     * @param walletUserId    userId
     * @return ApiUserWalletAccount fetched wallet account details from db
     */
    public Mono<ApiUserWalletAccount> fetchWalletAccount(
            String walletUserId) {
        ApiUserWalletAccount walletAccount = redisService.getValue(walletUserId, ApiUserWalletAccount.class);
        if (walletAccount != null) {
            return Mono.just(walletAccount);
        }
        Mono<UserWalletAccount> userWalletAccount =
                userWalletAccountRepository.findByUserId(walletUserId);

        Mono<ApiUserWalletAccount> apiUserWalletAccount =
                userWalletAccount
                        .flatMap(
                                wallet -> {

//                                    TODO Need to uncomment below code after updating wallet status directly on plaid side in Edit Wallet API
//                                    VirtualAccountResponse virtualAccountResponse =
//                                            plaidVirtualAccount.getWallet(wallet.getPlaidWalletId());
//                                    if (virtualAccountResponse == null)
//                                        return Mono.error(
//                                                new BusinessException(
//                                                        ErrorType.PLAID_API_FAILED, "Plaid API not responding."));
//                                    wallet.setStatus(virtualAccountResponse.getStatus().name());

                                    return Mono.just(wallet);
                                })
                        .map(walletAccountMapper::toApiUserWalletAccount)
                        .switchIfEmpty(ifWalletNotExist(walletUserId));

        return apiUserWalletAccount;
    }

    public Mono<ApiWalletTransferResponse> initiateWalletTransfer(String userId, ApiWalletTransferRequest apiWalletTransferRequest) {
        Mono<UserWalletAccount> userWalletAccountMono = userWalletAccountRepository.findByUserId(userId);
        if (userWalletAccountMono == null)
            throw new BusinessException(ErrorType.WALLET_NOT_EXISTS, "User not registered any wallet");

        UserWalletAccount fromUserWalletAccount = userWalletAccountMono.block();
        validateUserWallet(fromUserWalletAccount, apiWalletTransferRequest);
        Mono<UserWalletAccount> toUserWalletAccountMono = searchWalletByIdentifier(apiWalletTransferRequest);

        if (toUserWalletAccountMono == null)
            throw new BusinessException(ErrorType.WALLET_NOT_EXISTS, "Recipient wallet id is invalid");

        UserWalletAccount toUserWalletAccount = toUserWalletAccountMono.block();
        transactAmount(fromUserWalletAccount, apiWalletTransferRequest.getAmount(), TransferType.DEBIT);
        transactAmount(toUserWalletAccount, apiWalletTransferRequest.getAmount(), TransferType.CREDIT);
        String transactionId = UUID.randomUUID().toString();
        Transfer transfer = transferMapper.toTransfer(userId, transactionId, fromUserWalletAccount.getId().toString(), toUserWalletAccount.getId().toString(), apiWalletTransferRequest.getAmount(), apiWalletTransferRequest.getCurrency(), apiWalletTransferRequest.getReason(), "Success");
        transferRepository.save(transfer).subscribe(result -> log.info("wallet to wallet transaction submitted successfully"));
        return Mono.just(walletAccountMapper.toApiWalletTransferResponse(transfer));
    }

    private void transactAmount(UserWalletAccount userWalletAccount, BigDecimal amount, TransferType transferType) {
        BigDecimal accountBalance = userWalletAccount.getAccountBalance();
        if (TransferType.CREDIT.equals(transferType)) {
            userWalletAccount.setAccountBalance(accountBalance.add(amount));
        } else if (TransferType.DEBIT.equals(transferType)) {
            userWalletAccount.setAccountBalance(accountBalance.subtract(amount));
        }
        userWalletAccountRepository.save(userWalletAccount).subscribe(result -> log.info("wallet updated success"));
        redisService.setValue(userWalletAccount.getUserId(), walletAccountMapper.toApiUserWalletAccount(userWalletAccount));
    }


    private void validateUserWallet(UserWalletAccount userWalletAccount, ApiWalletTransferRequest apiWalletTransferRequest) {
        if (apiWalletTransferRequest.getAmount().doubleValue() <= 0)
            throw new BusinessException(ErrorType.INVALID_AMOUNT, "Transaction amount should minimum 1");

        if (!apiWalletTransferRequest.getCurrency().name().equals(userWalletAccount.getCurrency()))
            throw new BusinessException(ErrorType.INVALID_CURRENCY, String.format("User wallet support only %s currency", userWalletAccount.getCurrency()));

        if (!userWalletAccount.getStatus().equals(AccountStatus.ACTIVE.name()))
            throw new BusinessException(ErrorType.WALLET_NOT_ACTIVE, "This user wallet not in active status");

        if (userWalletAccount.getAccountBalance().doubleValue() <= 0 || userWalletAccount.getAccountBalance().doubleValue() < apiWalletTransferRequest.getAmount().doubleValue())
            throw new BusinessException(ErrorType.INSUFFICIENT_BALANCE, "don't have enough balance in your account to initiate this transaction");

    }

    private Mono<UserWalletAccount> userNotRegisteredWallet() {
        throw new BusinessException(ErrorType.WALLET_NOT_EXISTS, "User not registered any wallet");
    }

    private Mono<UserWalletAccount> searchWalletByIdentifier(ApiWalletTransferRequest request) {
        User user = searchUser(request.getIdentifier(), request.getIdentifierType());
        return userWalletAccountRepository.findByUserId(user.getUserId());
    }

    private User searchUser(String identifier, IdentifierType identifierType) {
     Function<String, Optional<User>> searchUserFunction =
            identifierType.equals(IdentifierType.MOBILE)
                ? (userService::findUserByMobile)
                : (userService::findUserByEmail);
     Optional<User> userOptional = searchUserFunction.apply(identifier);

     if (userOptional.isEmpty())
          throw new BusinessException(
             ErrorType.INVALID_IDENTIFIER,
             String.format("No User found for the given identifier(%s)", identifier));

        return userOptional.get();
     }

    private Mono<ApiUserWalletAccount> ifWalletNotExist(String walletId) {
        return Mono.error(
                new BusinessException(
                        ErrorType.WALLET_NOT_EXISTS, String.format("Wallet is not exist for this user (%s)", walletId)));
    }


    /**
     * Get transaction for given transactionId
     *
     * @param transactionId
     * @return ApiTransfer transaction with id transactionId
     */
    public ApiTransfer getTransactionById(int transactionId) {

        Mono<Transfer> transaction = transferRepository.findById(String.valueOf(transactionId));
        return transaction.blockOptional().map(transfer -> transferMapper.fromTransfer(transfer)).orElseThrow(() -> new ResourceNotFoundException(String.format("transaction does not exist", transactionId)));

    }


    /**
     * Get all transactions for given user
     *
     * @param userId
     * @return List<ApiTransfer> list of transactions
     */
    public List<ApiTransfer> getAllTransactionsFoUser(String userId) {
        //check if User with given user id exist and get transactions else throw exception
        return userService.findUser(userId).map(w -> transferRepository.findByUserId(userId).collectList().block().stream().map(transferMapper::fromTransfer).collect(Collectors.toList())).orElseThrow(() -> new ResourceNotFoundException(String.format("User does not exist", userId)));
    }

    public void deleteRedisKey(String key) {
        redisService.delete(key);
    }

    public Mono<ApiUserWalletAccount> depositAmount(String walletAccountId, BigDecimal amount) {
        if (amount.doubleValue() <= 0)
            throw new BusinessException(ErrorType.INVALID_AMOUNT, "amount should be more than zero");

        Mono<UserWalletAccount> userWalletAccountMono =
                userWalletAccountRepository.findById(UUID.fromString(walletAccountId));
        if (userWalletAccountMono == null)
            userNotRegisteredWallet();
        UserWalletAccount userWalletAccount = userWalletAccountMono.block();
        if (!userWalletAccount.getStatus().equals(AccountStatus.ACTIVE.name()))
            throw new BusinessException(ErrorType.WALLET_NOT_ACTIVE, "Wallet is not active status");
        userWalletAccount.setAccountBalance(userWalletAccount.getAccountBalance().add(amount));
        ApiUserWalletAccount apiUserWalletAccount =
            walletAccountMapper.toApiUserWalletAccount(
               userWalletAccountRepository.save(userWalletAccount).block());
        redisService.setValue(apiUserWalletAccount.getUserId(), apiUserWalletAccount);
        return Mono.just(apiUserWalletAccount);
    }


    public Mono<HashMap<String,Object>> executeWalletToBankTransfer(String userId,
                                                                    WalletTransactionExecuteRequest walletTransactionExeRequest) throws IOException{
        final WalletTransactionExecuteRequest walletTransactionExecuteRequest = walletTransactionExeRequest
                .clientId(walletIntegrationConfig.getPlaidClientId())
                .secret(walletIntegrationConfig.getPlaidSecret());
        final String walletId = walletTransactionExecuteRequest.getWalletId();
        final Mono<UserWalletAccount> walletAccount = userWalletAccountRepository.findByUserId(userId);
        Double requestedAmount = walletTransactionExecuteRequest.getAmount().getValue();
        String bankAccountNo = walletTransactionExecuteRequest.getCounterparty().getNumbers().getBacs().getAccount();
        LocalDateTime transactionDateTime = LocalDateTime.now();

        Mono<HashMap<String,Object>> dbEntryMapMono = walletAccount
                .doOnNext(apiUserWalletAccount ->
                        validateRequestedAmount(apiUserWalletAccount,requestedAmount)
                ).flatMap((UserWalletAccount userWalletAccount) ->{
                    HashMap<String,Object> dbEntryMap = new HashMap<String,Object>();

                    //Create JournalEntry data
                    JournalEntry journalEntry = createJournalEntry(userWalletAccount, walletTransactionExecuteRequest, userId);
                    dbEntryMap.put("JOURNAL", journalEntry);
                    //Create Ledger data
                    UserBankAccount userBankAccount = linkAccountRepository.findById(userWalletAccount.getPlaidAccountNumber()).block();
                    Ledger ledger = createLedger(walletTransactionExecuteRequest,userWalletAccount,userBankAccount,journalEntry,userId);
                    dbEntryMap.put("LEDGER", ledger);
                    String fromAccountTYpe = AccountType.wallet.toString();
                    String toAccountTYpe = AccountType.bank.toString();
                    //Create Transfer data
                    Transfer transfer = createTransfer(walletTransactionExecuteRequest,userWalletAccount,userId,fromAccountTYpe,toAccountTYpe,journalEntry, walletId);
                    dbEntryMap.put("TRANSFER",transfer);
                    // Updating wallet balance after transaction
                    BigDecimal walletBalance = userWalletAccount.getAccountBalance();
                    userWalletAccount.setAccountBalance(walletBalance.subtract(BigDecimal.valueOf(requestedAmount)));
                    userWalletAccountRepository.save(userWalletAccount).subscribe(wallet -> {
                        redisService.setValue(wallet.getUserId(), walletAccountMapper.toApiUserWalletAccount(wallet));
                        log.debug(String.format("Wallet balance is updated in wallet for user %s", userId));
                    });

                    return Mono.just(dbEntryMap);
                }).onErrorMap( t->{
                            if(t instanceof ValidationException){
                                throw (ValidationException)t;
                            }
                            throw new IllegalArgumentException(t.getMessage());
                        }
                );
        return dbEntryMapMono;
    }


    private void validateRequestedAmount(final UserWalletAccount apiUserWalletAccount,
                                         final Double requestedAmount){
            Integer result = apiUserWalletAccount.getAccountBalance().compareTo(BigDecimal.valueOf(requestedAmount));
            if (result == -1)
                throw new ValidationException(new ArrayList<String>() {{
                    add("User Wallet amount should be greater than Requested amount");
                }});
    }

    public Mono<HashMap<String,Object>> executeBankToWalletTransfer(String userId,
                                                                    WalletTransactionExecuteRequest walletTransactionExeRequest) throws IOException{
        final WalletTransactionExecuteRequest walletTransactionExecuteRequest = walletTransactionExeRequest
                .clientId(walletIntegrationConfig.getPlaidClientId())
                .secret(walletIntegrationConfig.getPlaidSecret());
        final String walletId = walletTransactionExecuteRequest.getWalletId();
        final Mono<UserWalletAccount> walletAccount = userWalletAccountRepository.findByUserId(userId);

        Mono<HashMap<String,Object>> dbEntryMapMono = walletAccount
                .flatMap((UserWalletAccount userWalletAccount) ->{
                    HashMap<String,Object> dbEntryMap = new HashMap<String,Object>();

                    //Create JournalEntry data
                    JournalEntry journalEntry = createJournalEntry(userWalletAccount, walletTransactionExecuteRequest, userId);
                    dbEntryMap.put("JOURNAL", journalEntry);
                    //Create Ledger data
                    UserBankAccount userBankAccount = linkAccountRepository.findById(userWalletAccount.getPlaidAccountNumber()).block();
                    Ledger ledger = createLedger(walletTransactionExecuteRequest,userWalletAccount,userBankAccount,journalEntry,userId);
                    dbEntryMap.put("LEDGER", ledger);
                    String fromAccountTYpe = AccountType.bank.toString();
                    String toAccountTYpe = AccountType.wallet.toString();
                    //Create Transfer data
                    Transfer transfer = createTransfer(walletTransactionExecuteRequest,userWalletAccount,userId,fromAccountTYpe,toAccountTYpe,journalEntry, walletId);
                    dbEntryMap.put("TRANSFER",transfer);
                    // Updating wallet balance after transaction
                    BigDecimal walletBalance = userWalletAccount.getAccountBalance();
                    userWalletAccount.setAccountBalance(walletBalance.add(BigDecimal.valueOf( walletTransactionExecuteRequest.getAmount().getValue())));
                    userWalletAccountRepository.save(userWalletAccount).subscribe(wallet -> {
                        redisService.setValue(wallet.getUserId(), walletAccountMapper.toApiUserWalletAccount(wallet));
                        log.debug(String.format("Wallet balance is updated in wallet for user %s", userId));
                    });
                    return Mono.just(dbEntryMap);
                }).onErrorMap( t->{
                            if(t instanceof ValidationException){
                                throw (ValidationException)t;
                            }
                            throw new IllegalArgumentException(t.getMessage());
                        }
                );
        return dbEntryMapMono;
    }

    private JournalEntry createJournalEntry(UserWalletAccount userWalletAccount, WalletTransactionExecuteRequest walletTransactionExecuteRequest,String userId){
        Double requestedAmount = walletTransactionExecuteRequest.getAmount().getValue();
        String bankAccountNo = walletTransactionExecuteRequest.getCounterparty().getNumbers().getBacs().getAccount();
        LocalDateTime transactionDateTime = LocalDateTime.now();

        final JournalEntry journalEntry = JournalEntry.builder()
                .journalEntryId(UUID.randomUUID().toString())
                .accountIDOne(userWalletAccount.getId().toString())
                .accountIdTwo(bankAccountNo)
                .currency(userWalletAccount.getCurrency())
                .accountOneDebitAmount(requestedAmount)
                .accountTwoCreditAmount(requestedAmount)
                .transactionDescription("Transaction ID " + UUID.randomUUID())
                .transactionTime(transactionDateTime)
                .build();
        journalRepository.save(journalEntry)
                .subscribe(journal ->
                        log.info(String.format("JournalEntry(Id: %s) record is successfully added for user %s", journal.getJournalEntryId(), userId)));
        return  journalEntry;
    }

    private Ledger createLedger(WalletTransactionExecuteRequest walletTransactionExecuteRequest,UserWalletAccount userWalletAccount,UserBankAccount userBankAccount,JournalEntry journalEntry,String userId) {
        Double requestedAmount = walletTransactionExecuteRequest.getAmount().getValue();
        String bankAccountNo = walletTransactionExecuteRequest.getCounterparty().getNumbers().getBacs().getAccount();
        LocalDateTime transactionDateTime = LocalDateTime.now();
        final Ledger ledger = Ledger.builder()
                .ledgerId(UUID.randomUUID().toString())
                .journalId(journalEntry)
                .currency(userWalletAccount.getCurrency())
                .userId(userWalletAccount.getUserId().toString())
                .transactionAmount(requestedAmount)
                .transactionDate(transactionDateTime)
                .userBankAccount(userBankAccount)
                .build();
        ledgerRepository.save(ledger).subscribe(l -> {
            log.info(String.format("Ledger(Id: %s) record is successfully added in DB for user %s", l.getLedgerId(), userId));
        });
        return ledger;
    }
 private Transfer createTransfer(WalletTransactionExecuteRequest walletTransactionExecuteRequest,UserWalletAccount userWalletAccount,String userId,String fromAccountTYpe, String toAccountTYpe,JournalEntry journalEntry,String walletId) {
     Double requestedAmount = walletTransactionExecuteRequest.getAmount().getValue();
     String bankAccountNo = walletTransactionExecuteRequest.getCounterparty().getNumbers().getBacs().getAccount();
     LocalDateTime transactionDateTime = LocalDateTime.now();
     final Transfer transfer = Transfer.builder()
             .transactionID(UUID.randomUUID().toString())
             .currency(userWalletAccount.getCurrency())
             .accountIDFrom(walletId)
             .accountIDFromType(fromAccountTYpe)
             .amount(requestedAmount)
             .accountIDTo(bankAccountNo)
             .accountIdToType(toAccountTYpe)
             .journalEntry(journalEntry)
             .userId(userId)
             .transacDateTime(transactionDateTime)
             .transactionStatus(Status.success.toString())
             .build();
     transferRepository.save(transfer).subscribe(t -> {
         log.info(String.format("Transfer(Id: %s) record is successfully added in DB for user %s", t.getTransactionID(), userId));
     });
     return transfer;
 }
}
