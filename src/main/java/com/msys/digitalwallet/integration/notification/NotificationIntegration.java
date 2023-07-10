package com.msys.digitalwallet.integration.notification;

import com.msys.digitalwallet.integration.notification.request.ApiNotificationRequest;

public interface NotificationIntegration {

  public void sendNotification(ApiNotificationRequest request);
}
