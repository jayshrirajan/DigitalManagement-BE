package com.msys.digitalwallet.wallet.service;


import com.google.gson.reflect.TypeToken;
import com.mongodb.client.result.UpdateResult;
import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.repository.UserRepository;
import com.msys.digitalwallet.wallet.apirequest.LinkBankAccountRequest;
import com.msys.digitalwallet.wallet.constants.USER_BANK_ACCONUT_LINK_STATUS;
import com.msys.digitalwallet.wallet.filter.LoggingFilter;
import com.msys.digitalwallet.wallet.model.UserBankAccount;
import com.msys.digitalwallet.wallet.repository.LinkAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LinkAccountService {

    @Autowired
    private LinkAccountRepository linkAccountRepository;

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    private String USER_BANK_ACCOUNTS_REDIS_KEY;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MongoTemplate mongoTemplate;

    public Mono<UserBankAccount> saveUserBankAccount(LinkBankAccountRequest request) {
        LOGGER.info("saveUserBankAccount service methods starts");
        Double accountBalance = 0.0D;
        if(request.getAccountBalance() != null){
            accountBalance = request.getAccountBalance();
        }
        UserBankAccount userBankAccount =
                UserBankAccount
                        .builder()
                        .userId(request.getUserId())
                        .bankName(request.getBankName())
                        .bankAccountId(request.getBankAccountId())
                        .accountCreatedDate(LocalDateTime.now())
                        .accountBalance(accountBalance)
                        .status(USER_BANK_ACCONUT_LINK_STATUS.ACTIVE.ordinal())
                        .build();
        Mono<UserBankAccount> userBankAccountMono = linkAccountRepository
                .save(userBankAccount);
        redisService.setValue(userBankAccount.getUserAccountId(), userBankAccount);
        return userBankAccountMono;
    }

    public Mono<Map<String, Object>> checkUserIdExists(String userId) {
        Optional<User> user = userRepository.findById(userId);
        Mono<Map<String, Object>> resultMap;
        Map<String, Object> errorMap = new HashMap<>();
        if (!user.isPresent()) {
            errorMap.put("error", "User Id not found");
        }
        resultMap = Mono.just(errorMap);
        return resultMap;
    }

    public  Flux<UserBankAccount> getUserBankAccountsByUserId(String userId) {
        LOGGER.info("getUserBankAccountsByUserId service methods starts");
        Flux<UserBankAccount> userBankAccount = linkAccountRepository.findByUserId(userId);
        LOGGER.info("getUserBankAccountsByUserId service methods ends");
        return userBankAccount;
    }
    public  Flux<UserBankAccount> getUserActiveBankAccountsByUserId(String userId) {
        LOGGER.info("getUserActiveBankAccountsByUserId service methods starts");
        Flux<UserBankAccount> userBankAccount = linkAccountRepository.
                findByUserIdAndStatus(userId,USER_BANK_ACCONUT_LINK_STATUS.ACTIVE.ordinal());
        LOGGER.info("getUserActiveBankAccountsByUserId service methods ends");
        return userBankAccount;
    }


    public void deleteRedisKey(String key){
        redisService.delete(key);
    }

    public void deleteByBankAccountId(String accountId) {
        Query query=new Query(Criteria.where("bankAccountId").is(accountId));
        Update update = new Update();
        update.set("STATUS",USER_BANK_ACCONUT_LINK_STATUS.CLOSED.ordinal());
        UpdateResult result = mongoTemplate.updateMulti(query,update,UserBankAccount.class);
        if(result!=null){
            LOGGER.info("deleted-closed the bank account for bankAccountId {}",accountId);
        }else{
            LOGGER.info("No bank accounts updated to closed state for  bankAccountId{}",accountId);
        }

    }
}
