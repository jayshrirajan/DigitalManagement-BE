package com.msys.digitalwallet.notification.twilio;



import com.msys.digitalwallet.notification.enums.Channel;
import com.msys.digitalwallet.notification.integration.TwilioClient;
import com.msys.digitalwallet.notification.model.Notification;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class TwilioService implements TwilioClient {

    @Value("${twilio.account.sid}")
    public String accountSId;
    @Value("${twilio.auth.token}")
    public String authToken;

    @Value("${twilio.auth.service.sid}")
    public String serviceSid;

    @Value("${twilio.sendGrid.id}")
    public String sendGridid;

    public String sendOTP( String identifier, Channel channel ) {

        intiateTwilioProcess();
        Verification verification = Verification.creator(
                        serviceSid, // this is your verification sid
                        identifier, //this is your Twilio verified recipient phone number
                        channel.name()) // this is your channel type
                .create();

        return verification.getStatus();
    }

    private void intiateTwilioProcess() {
        Twilio.init(decoder(accountSId),decoder(authToken));
    }

    private String decoder(String encodedString) {
        return new String(Base64.getDecoder().decode(encodedString));
    }

    @Override
    public String sendNotification(Notification notification) {
        String response;
        System.out.println("Twilio service");
        if(notification.getChannel().equals(Channel.email)){
                response = sendNotificationEmail(notification);
        } else if(notification.getChannel().equals(Channel.whatsapp)){
            response = sendWhatsappNotification(notification.getIdentifier()
                    ,notification.getMessage());
        } else {
            response = sendNotificationSMS(notification.getIdentifier()
                    ,notification.getMessage());
        }

        return response;
    }

    public String verifyOTP(String identifier, String token) {
        intiateTwilioProcess();

        VerificationCheck verificationCheck = VerificationCheck.creator(
                            decoder(serviceSid))
                    .setTo(identifier)
                    .setCode(token)
                    .create();

        return verificationCheck.getStatus();

    }
    public String sendNotificationSMS(String identifier, String message){

        intiateTwilioProcess();

        Message twilioMessage = Message.creator(new PhoneNumber(identifier),
                new PhoneNumber("+12765826739"), message).create();

        return twilioMessage.getStatus().name();
    }

    public String sendWhatsappNotification(String identifier, String message){
        intiateTwilioProcess();
        Message twilioMessage = Message.creator(
                        new com.twilio.type.PhoneNumber("whatsapp:"+identifier),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        message)
                .create();

        return twilioMessage.getStatus().name();
    }

    public String sendNotificationEmail(Notification notification) {

        Email from = new Email("tarunkumar.ks1992@gmail.com");
        Email to = new Email(notification.getIdentifier());
        Content content = new Content("text/html", notification.getMessage());
        Mail mail = new Mail(from, notification.getSubject(), to, content);

        SendGrid sg = new SendGrid(decoder(sendGridid));
        Request request = new Request();
        Response response;
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        try {
            request.setBody(mail.build());
            response = sg.api(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return String.valueOf(response.getStatusCode());
    }
}
