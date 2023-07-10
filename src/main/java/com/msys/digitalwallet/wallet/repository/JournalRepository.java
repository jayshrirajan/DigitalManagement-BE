package com.msys.digitalwallet.wallet.repository;

import com.msys.digitalwallet.wallet.model.JournalEntry;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalRepository extends ReactiveMongoRepository<JournalEntry, String> {
}
