package com.msys.digitalwallet.notification.model;

import com.msys.digitalwallet.notification.enums.Channel;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class OtpVerification {

    private String identifier;

    private Channel Channel;

    private String token;

}
