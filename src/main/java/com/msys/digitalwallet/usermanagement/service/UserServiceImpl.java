package com.msys.digitalwallet.usermanagement.service;


import com.google.gson.reflect.TypeToken;
import com.msys.digitalwallet.auth.dto.Credentials;
import com.msys.digitalwallet.auth.dto.UserDto;
import com.msys.digitalwallet.auth.service.AuthService;
import com.msys.digitalwallet.auth.service.KeyCloakService;
import com.msys.digitalwallet.common.exception.ResourceAlreadyAvailableException;
import com.msys.digitalwallet.common.exception.ResourceNotFoundException;
import com.msys.digitalwallet.usermanagement.dto.RestResponse;
import com.msys.digitalwallet.usermanagement.eventListeners.MongoDBAfterLoadEventListener;
import com.msys.digitalwallet.usermanagement.eventListeners.MongoDBBeforeSaveEventListener;
import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.repository.UserRepository;
import com.msys.digitalwallet.wallet.apiresponse.ApiUserWalletAccount;
import com.msys.digitalwallet.wallet.filter.LoggingFilter;
import com.msys.digitalwallet.wallet.service.RedisService;
import com.msys.digitalwallet.wallet.service.WalletAccountService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class UserServiceImpl implements UserService {

    private static final String AUTH_SERVICE_BASE_URL = "/api/user";

    private static final String AUTH_SERVICE_BASE_URL_CREATE = "/api/user";

    private static final String AUTH_SERVICE_BASE_URL_UPDATE = "/api/user/update";

    private String USERS_REDIS_KEY;

    @Autowired
    private WebClient webClient;

    @Value("${services.authService.baseUrl}")
    private String baseUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeyCloakService keyCloakService;

    @Autowired
    MessageSource messageSource;
    @Autowired
    private AuthService authService;

    @Autowired
    RedisService redisService;

    List<User> cacheUserList ;
    @Autowired
    WalletAccountService walletAccountService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public RestResponse createUser(User request) {

        Map<String, Object> responseMap = new HashMap<>();
        validateUser(request);
        try {
            User user = userRepository.save(request);
            redisService.setValue(user.getUsername(), user);
            redisService.delete(USERS_REDIS_KEY);
            Mono<ApiUserWalletAccount> walletAccount = walletAccountService.createWalletAccount(user.getUserId());
            ApiUserWalletAccount apiUserWalletAccount = walletAccount.block();

            if(apiUserWalletAccount!=null){
                responseMap.put("walletStatus","wallet created successfully");
                responseMap.put("walletDetails",apiUserWalletAccount);
            }else {
                responseMap.put("walletStatus","wallet creation failed");

            }
            getAuthMsResponse(user, "create");
            if(user.getUserId()!=null) {
                responseMap.put("username", user.getUsername());
                return new RestResponse(true, "User created successfully", "data", responseMap);
            }
            else {
                return new RestResponse(false, "Error has occurred in creating user ", "data", responseMap);
            }
        } catch (Exception e) {
            return new RestResponse(false, "Error has occurred in creating user ", "data", e);
        }
    }

    @Override
    public boolean updateUser(User request) throws ResourceNotFoundException {

        String userErrorMessage = messageSource.getMessage("resource.not.found.error.message", new Object[0], Locale.getDefault());
        Map<String, Object> responseMap = new HashMap<>();
        AtomicBoolean flag = new AtomicBoolean(false);

        userRepository.findByUsername(request.getUsername()).ifPresentOrElse((user) -> {
            try {
                BeanUtils.copyProperties(request, user, getNullPropertyNames(request));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            boolean checkStatus = getAuthMsResponse(user, "update");
            if (checkStatus) {
                user = userRepository.save(user);
                responseMap.put("username", user.getUsername());
                redisService.setValue(user.getUsername(), user);
                redisService.delete(USERS_REDIS_KEY);
                flag.set(true);
            } else {
                flag.set(false);
            }
        }, () -> {
            throw new ResourceNotFoundException(String.format(userErrorMessage, request.getUsername()));
        });

        return flag.get();
    }

    private void validateUser(User request) {
        String userNameErrorMessage = messageSource.getMessage("resource.username.already.available.message", new Object[0], Locale.getDefault());
        List<String> errorMessages = new ArrayList<>();
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isPresent()) {
            throw new ResourceAlreadyAvailableException(String.format(userNameErrorMessage, request.getUsername()));
        }
    }

    @Override
    public List<User> fetchAllUser() {
        Type sampleListType = new TypeToken<List<User>>() {}.getType();
        cacheUserList = redisService.getValue(USERS_REDIS_KEY, sampleListType);
        if(cacheUserList != null){
            LOGGER.info("Getting Users from redis cache");
            return cacheUserList;
        }
        List<User> userList = userRepository.findAll();
        redisService.setValue(USERS_REDIS_KEY,userList);
        return userList;
    }

    @Override
    public Map<String, Object> delete(String username) {
        String userErrorMessage = messageSource.getMessage("resource.not.found.error.message", new Object[0], Locale.getDefault());
        Map<String, Object> responseMap = new HashMap<>();
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            userRepository.delete(user);
            redisService.delete(user.getUsername());
            responseMap.put("user_id", username);
            responseMap.put("status", 200);
            responseMap.put("message", "User Delete Successful");

        }, () -> {
            throw new ResourceNotFoundException(String.format(userErrorMessage, username));
        });

        return responseMap;
    }

    @Bean
    public MongoDBBeforeSaveEventListener mongoDBBeforeSaveEventListener() {
        return new MongoDBBeforeSaveEventListener();
    }

    @Bean
    public MongoDBAfterLoadEventListener mongoDBAfterLoadEventListener() {
        return new MongoDBAfterLoadEventListener();
    }

    public static String[] getNullPropertyNames(final Object source) throws NoSuchFieldException {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (!pd.getName().equalsIgnoreCase("class")) {
                Field field = source.getClass().getDeclaredField(pd.getName());
                if ((field.isAnnotationPresent(NotBlank.class) || field.isAnnotationPresent(NotNull.class)) && srcValue == null)
                    emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public Optional<User> findByUsername(String username) {
        User cacheUser = redisService.getValue(username, User.class);
        if(cacheUser != null && cacheUser.getUsername().equals(username)){
            LOGGER.info("Getting User " + username + " From Redis Cache" );
            return Optional.ofNullable(cacheUser);
        }
        User user = userRepository.findByUsername(username).get();
        redisService.setValue(user.getUsername(),user);
        return Optional.ofNullable(user);
    }

    public Optional<User> findUser(@NonNull String userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findUserByMobile(String mobileNumber) {
        return userRepository.findByMobileNumber(mobileNumber).stream().findFirst();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmailAddress(email).stream().findFirst();
    }

  private boolean getAuthMsResponse(User request, String apiName) {
        List<Credentials> credentials = new ArrayList<>();
        Credentials credential = new Credentials();
        credential.setValue(request.getPassword());
        credentials.add(credential);
        UserDto createUserDto = new UserDto(request.getName().getFirstName(), request.getName().getLastName(), request.getUsername(), request.getEmailAddress(), true, credentials);
        try {
            RestResponse restResponse;
            if (apiName.equals("create")) {
                //
                keyCloakService.addUser(createUserDto);
               //authService.createUser(createUserDto);
                // restResponse = webClient.post().uri(baseUrl + AUTH_SERVICE_BASE_URL_CREATE).body(Mono.just(createUserDto), UserDto.class).retrieve().bodyToMono(RestResponse.class).block();
            } else {
                //restResponse = webClient.put().uri(baseUrl + AUTH_SERVICE_BASE_URL_UPDATE).body(Mono.just(createUserDto), UserDto.class).retrieve().bodyToMono(RestResponse.class).block();
            }
            return true;
        } catch (WebClientRequestException | WebClientResponseException e) {
            throw new RuntimeException(e);
        }
    }

}
