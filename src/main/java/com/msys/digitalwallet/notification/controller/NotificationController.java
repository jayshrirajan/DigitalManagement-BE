package com.msys.digitalwallet.notification.controller;


import com.msys.digitalwallet.notification.model.Notification;
import com.msys.digitalwallet.notification.model.OtpVerification;
import com.msys.digitalwallet.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/notification")
@CrossOrigin(maxAge = 3600)
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> sendSMSNotification(@RequestBody @Valid Notification notification) throws IOException {
        return ResponseEntity.ok(notificationService.sendNotification(notification));
    }

    @PostMapping(path = "/OTP")
    public ResponseEntity<String> sendOTP(@RequestBody @Valid OtpVerification otpVerification) {
        return ResponseEntity.ok(notificationService.sendOTP(otpVerification.getIdentifier(),otpVerification.getChannel()));
    }

    @PostMapping(path = "/verify")
    public ResponseEntity<String> verifyOTP(@RequestBody @Valid OtpVerification otpVerification) {
        return ResponseEntity.ok(notificationService.verifyOTP(otpVerification.getIdentifier(),otpVerification.getToken()));
    }

}
