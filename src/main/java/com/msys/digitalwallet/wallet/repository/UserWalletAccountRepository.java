package com.msys.digitalwallet.wallet.repository;

import com.msys.digitalwallet.wallet.model.UserWalletAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserWalletAccountRepository
    extends ReactiveMongoRepository<UserWalletAccount, UUID> {

  Mono<UserWalletAccount> findByUserId(String userId);
  Mono<UserWalletAccount> findByPlaidWalletId(String plaidWalletId);


}
