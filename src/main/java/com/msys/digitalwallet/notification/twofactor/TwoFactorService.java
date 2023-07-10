package com.msys.digitalwallet.notification.twofactor;


import com.msys.digitalwallet.notification.enums.Channel;
import com.msys.digitalwallet.notification.integration.TwoFactorClient;
import com.msys.digitalwallet.notification.model.Notification;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TwoFactorService implements TwoFactorClient {
    @Override
    public String sendNotification(Notification notification) {
        System.out.println("2 Factor");
        if(notification.getChannel() == Channel.email || notification.getChannel() == Channel.whatsapp){
            throw new RuntimeException();
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "module=TRANS_SMS&apikey=6ed5ae4e-c6f8-11ed-81b6-0200cd936042&to=91XXXXXXXXXX,91YYYYYYYYYY&from=HEADER&msg=DLT Approved Message Text Goes Here");
        Request request = new Request.Builder()
                .url("https://2factor.in/API/R1/")
                .method("POST", body)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response.message();
    }

    @Override
    public String verifyOTP(String identifier, String token) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://2factor.in/API/V1/6ed5ae4e-c6f8-11ed-81b6-0200cd936042/SMS/VERIFY3/+919790794687/"+token)
                .method("GET",null)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response);
        return response.message();
    }

    @Override
    public String sendOTP(String identifier, Channel channel) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://2factor.in/API/V1/6ed5ae4e-c6f8-11ed-81b6-0200cd936042/SMS/+919790794687/AUTOGEN/sample")
                .method("GET",null)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response);
        return response.message();
    }
}
