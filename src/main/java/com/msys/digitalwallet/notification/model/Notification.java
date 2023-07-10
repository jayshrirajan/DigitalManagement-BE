package com.msys.digitalwallet.notification.model;


import com.msys.digitalwallet.notification.enums.Channel;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Notification {

    private String identifier;

    private String subject;

    private String message;

    private String cc;

    private Channel channel;
}
