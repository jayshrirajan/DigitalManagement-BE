package com.msys.digitalwallet.notification.service;



import com.msys.digitalwallet.notification.enums.Channel;
import com.msys.digitalwallet.notification.integration.IntegrationClient;
import com.msys.digitalwallet.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    IntegrationClient integrationClient;

    public String sendOTP(String identifier, Channel channel ) {

        String response = integrationClient.sendOTP(identifier,channel);
        return response;
    }
    public String verifyOTP(String identifier, String token) {

        String response = integrationClient.verifyOTP(identifier,token);
        return response;
    }

    public String sendNotification(Notification notification) {
        return integrationClient.sendNotification(notification);
    }
}
