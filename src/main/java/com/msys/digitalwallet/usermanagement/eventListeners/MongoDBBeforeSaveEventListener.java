package com.msys.digitalwallet.usermanagement.eventListeners;


import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.utils.EncryptionUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Encrypted;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

import java.lang.reflect.Field;

public class MongoDBBeforeSaveEventListener extends AbstractMongoEventListener<User> {

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Override
    public void onBeforeSave(BeforeSaveEvent<User> event) {

        Document eventObject = event.getDocument();
        final User user = event.getSource();
        for (Field field : user.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Encrypted.class)) {
                eventObject.put(field.getName(), this.encryptionUtil.encrypt(eventObject.get(field.getName()).toString()));
            }
        }
        super.onBeforeSave(event);
    }

}
