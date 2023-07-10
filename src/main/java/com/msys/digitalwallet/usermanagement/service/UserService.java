package com.msys.digitalwallet.usermanagement.service;




import com.msys.digitalwallet.usermanagement.dto.RestResponse;
import com.msys.digitalwallet.usermanagement.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    RestResponse createUser(User request);

    boolean updateUser(User request);

    Optional<User> findByUsername(String username);

    List<User> fetchAllUser();

    Map<String, Object> delete(String id);

    Optional<User> findUser(String userId);

    Optional<User> findUserByMobile(String mobileNumber);

    Optional<User> findUserByEmail(String email);

    }
