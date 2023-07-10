package com.msys.digitalwallet.wallet.repository;

import com.msys.digitalwallet.wallet.model.Ledger;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends ReactiveMongoRepository<Ledger,String> {
}
