package com.msys.digitalwallet.usermanagement.repository;


import com.msys.digitalwallet.usermanagement.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

    List<User> findByMobileNumber(String mobileNumber);
    List<User> findByEmailAddress(String emailAddress);
}

