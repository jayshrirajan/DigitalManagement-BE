package com.msys.digitalwallet.auth.controller;


import com.msys.digitalwallet.auth.dto.ApiResponse;
import com.msys.digitalwallet.auth.dto.LoginDto;
import com.msys.digitalwallet.auth.dto.UserDto;
import com.msys.digitalwallet.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth/user")
public class AuthControllerNew {

    private static final Logger logger = LoggerFactory.getLogger(AuthControllerNew.class);
    @Autowired
    AuthService authService;

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


}
