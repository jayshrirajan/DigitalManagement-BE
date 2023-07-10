package com.msys.digitalwallet.auth.controller;


import com.msys.digitalwallet.auth.dto.ApiResponse;
import com.msys.digitalwallet.auth.dto.LoginDto;
import com.msys.digitalwallet.auth.dto.UserDto;
import com.msys.digitalwallet.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
@CrossOrigin(maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    AuthService authService;

    @Operation(summary = "Get All Users Information's")
    @GetMapping
    public ApiResponse getAllUsers() {

        logger.info("get all user");
        try {
            Flux<UserDto> result = authService.getAllUser();
            return result.collectList().flatMap(user -> {
                logger.debug("get all user {}",user);
                return Mono.just(new ApiResponse(true, "Users details has been fetched successfully", "data", user));
            }).block();
        } catch (Exception e) {
            logger.error("get all user {}",e.getMessage());
            return new ApiResponse(false, "Error has occurred in fetching users details", "data", new ArrayList<UserDto>());
        }

    }

    @Operation(summary = "Get user by username")
    @GetMapping("{username}")
    @Cacheable(value = "user",key = "#username")
    public ApiResponse getUserByName(@PathVariable String username) {
        logger.info("inside getUserByName");// to test the redis ,added this line , we can remove it after checking the redis
        try {
            Flux<UserDto> result = authService.getUserByUserName(username);
            if (result.blockFirst() != null) {
                return new ApiResponse(true, "User details has been fetched successfully", "data", result.blockFirst());
            } else {
                return new ApiResponse(false, "user not found", "data", new HashMap<>());

            }
        } catch (Exception e) {
            return new ApiResponse(false, "Error has occurred in fetching user details", "data", new HashMap<>());
        }
    }

    @Operation(summary = "Create new user")
    @PostMapping
    public ApiResponse saveUser(@RequestBody UserDto userDto) {
        Map<String, Object> map = new HashMap<>();
        try {
            UserDto result = authService.createUser(userDto);
            map.put("username", userDto.getUsername());
            return new ApiResponse(true, "User created successfully", "data", map);
        } catch (Exception e) {
            return new ApiResponse(false, "Error has occurred in creating user ", "data", map);
        }
    }

    @Operation(summary = "Update user information - firstname,lastname,email")
    @PutMapping()
    public ApiResponse updateUser(@RequestBody UserDto userDto) {
        Map<String, Object> map = new HashMap<>();

        try {
            String id = authService.getUserIdByUserName(userDto.getUsername());

            if (id != null) {
                map.put("username", userDto.getUsername());
                UserDto result = authService.updateUser(userDto, id);
                return new ApiResponse(true, "User details has been updated successfully", "data", map);
            } else {
                return new ApiResponse(false, "id not found", "data", map);
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Error has occurred in updating user details", "data", map);
        }
    }

    @Operation(summary = "Delete user by username")
    @DeleteMapping("{username}")
    public ApiResponse deleteUser(@PathVariable String username) {
        Map<String, Object> map = new HashMap<>();

        try {
            String id = authService.getUserIdByUserName(username);

            if (id != null) {
                map.put("username", username);
                UserDto result = authService.deleteUser(id);
                return new ApiResponse(true, "User has been successfully", "data", map);
            } else {
                return new ApiResponse(false, "id not found", "data", map);
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Error has occurred in deleting user", "data", map);
        }
    }

    @GetMapping("/test")
    public String test() {
        return "started";
    }

    @Operation(summary = "Get user token by username and password")
    @PostMapping("/token")
    public ApiResponse getAccessToken(@RequestBody LoginDto loginDto) {
        try {
            String token = authService.getToken(loginDto.getUserName(), loginDto.getPassword()).block();
            return new ApiResponse(true, "User token fetched", "data", token);
        }
        catch (Exception e)
        {
            return new ApiResponse(false, "unauthorized", "data", new HashMap<>());
        }

    }
}
