package com.msys.digitalwallet.usermanagement.eventListeners;


import com.msys.digitalwallet.usermanagement.model.User;
import com.msys.digitalwallet.usermanagement.utils.EncryptionUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Encrypted;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;

import java.lang.reflect.Field;

public class MongoDBAfterLoadEventListener extends AbstractMongoEventListener<User> {

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Override
    public void onAfterLoad(AfterLoadEvent<User> event) {

        Document eventObject = event.getDocument();
        final Class<User> user = event.getType();

        for (Field field : user.getDeclaredFields()) {

            if (field.isAnnotationPresent(Encrypted.class)) {
                eventObject.put(field.getName(), this.encryptionUtil.decrypt(eventObject.get(field.getName()).toString()));
            }
        }
        super.onAfterLoad(event);
    }
}
