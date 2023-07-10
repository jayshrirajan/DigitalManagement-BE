package com.msys.digitalwallet.notification.config;



import com.msys.digitalwallet.notification.integration.IntegrationClient;
import com.msys.digitalwallet.notification.twilio.TwilioService;
import com.msys.digitalwallet.notification.twofactor.TwoFactorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class NotificationBeanConfiguration {

    @Value("${notification.service}")
    String notificationBean;

    @Primary
    @Bean
    public IntegrationClient twilioClient(){
        if(notificationBean.equals("Twilio"))
            return new TwilioService();
        else
            return new TwoFactorService();
    }

}
