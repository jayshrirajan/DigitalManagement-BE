package com.msys.digitalwallet.wallet.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Service
@Slf4j
public class RedisService {
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    @Qualifier("gsonTemplate")
    private  Gson gson;



    public void setValue(Object key,Object value){
        redisTemplate.opsForValue().set(String.valueOf(key),gson.toJson(value));
    }

    public <T> T getValue(Object key,Type type){
        //Type sampleListType = new TypeToken<List<ApiUserWalletAccount>>(){}.getType();
        //Type sampleMapType = new TypeToken<Map<String, ApiUserWalletAccount>>(){}.getType();
        String savedStr = redisTemplate.opsForValue().get(String.valueOf(key));
        return gson.fromJson(savedStr,type);
    }

    public <T> T getValue(Object key, Class<T> classOfT) throws JsonSyntaxException {
        String savedStr = redisTemplate.opsForValue().get(String.valueOf(key));
        return gson.fromJson(savedStr,classOfT);

    }

    public void delete(Object key){
        redisTemplate.delete(String.valueOf(key));
    }
}
