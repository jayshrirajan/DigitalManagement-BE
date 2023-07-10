package com.msys.digitalwallet.notification.integration;


import com.msys.digitalwallet.notification.enums.Channel;
import com.msys.digitalwallet.notification.model.Notification;

public interface IntegrationClient {
    String sendNotification(Notification notification) ;

    String verifyOTP(String identifier, String token);

    String sendOTP(String identifier, Channel channel );

}
