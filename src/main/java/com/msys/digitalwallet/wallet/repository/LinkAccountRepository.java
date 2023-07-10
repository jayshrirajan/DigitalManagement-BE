package com.msys.digitalwallet.wallet.repository;

import com.msys.digitalwallet.wallet.model.UserBankAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LinkAccountRepository extends ReactiveMongoRepository<UserBankAccount, String> {

    Flux<UserBankAccount> findByUserId(String userId);
    Flux<UserBankAccount> findByUserIdAndStatus(String userId,int status);
}
