package com.msys.digitalwallet.integration.notification.service;

import com.msys.digitalwallet.integration.notification.NotificationIntegration;
import com.msys.digitalwallet.integration.notification.request.ApiNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class NotificationIntegrationService implements NotificationIntegration {

  @Value("${services.notificationService.baseUrl}")
  private String baseUrl;

  private WebClient client = WebClient.create();

  @Override
  public void sendNotification(ApiNotificationRequest request) {
    try {
      String response =
          client
              .post()
              .uri(baseUrl + "/notification")
              .body(Mono.just(request), ApiNotificationRequest.class)
              .retrieve()
              .bodyToMono(String.class)
              .block();
      log.info("Notification email sent for Wallet account creation - {}", response);

    } catch (Exception e) {
      log.error("Notification email failed for Wallet account creation", e);
    }
  }
}
