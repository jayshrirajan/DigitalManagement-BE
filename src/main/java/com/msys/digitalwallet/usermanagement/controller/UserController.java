package com.msys.digitalwallet.usermanagement.controller;

import com.msys.digitalwallet.usermanagement.dto.RestResponse;
import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    MessageSource messageSource;

    private static final byte[] secretKey = Base64.getDecoder().decode("hqHKBLV83LpCqzKpf8OvutbCs+O5wX5BPu3btWpEvXA=");
    private static final byte[] oldKey = Base64.getDecoder().decode("cUzurmCcL+K252XDJhhWI/A/+wxYXLgIm678bwsE2QM=");


    @PostMapping(value = "/create")
    public RestResponse createUser(@RequestBody @Valid User request) {

        //httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        return userService.createUser(request);
    }

    @PutMapping(value = "/update")
    public RestResponse updateUser(@RequestBody @Valid User request) {

        RestResponse restResponse;
        Map<String, Object> responseMap = new HashMap<>();

        boolean checkStatus = userService.updateUser(request);
        if (checkStatus) {
            responseMap.put("username", request.getUsername());
            restResponse = new RestResponse(true, "User details has been updated successfully", "data", responseMap);
        } else restResponse = new RestResponse(false, "Error reported during user updation", "data", responseMap);
        return restResponse;
    }

    @GetMapping(value = "/fetchAllUser")
    public ResponseEntity<List<User>> fetchAllUser() throws RuntimeException {
        return new ResponseEntity<>(userService.fetchAllUser(), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public Optional<User> getByUsername(@PathVariable("username") String username) {
        System.out.println("---------"+username);
        log.info("inside getUserByName"+username);// to test the redis ,added this line , we can remove it after checking the redis
        return userService.findByUsername(username);

    }

    @DeleteMapping(value = "/{username}")
    public Map<String, Object> deleteById(@PathVariable("username") String username) {
        return userService.delete(username);
    }

}