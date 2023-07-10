package com.msys.digitalwallet.integration.notification.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiNotificationRequest {

    private String identifier;

    private String subject;

    private String message;

    private String cc;

    private Channel channel;
}