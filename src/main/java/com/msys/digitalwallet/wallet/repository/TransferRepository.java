package com.msys.digitalwallet.wallet.repository;

import com.msys.digitalwallet.wallet.model.Transfer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransferRepository extends ReactiveMongoRepository<Transfer, String> {
    Flux<Transfer> findByUserId(String userId);
}
