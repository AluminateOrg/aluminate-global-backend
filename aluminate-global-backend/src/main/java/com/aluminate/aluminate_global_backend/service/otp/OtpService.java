package com.aluminate.aluminate_global_backend.service.otp;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private final StringRedisTemplate redisTemplate;

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOtp(String email, String otp) {
        redisTemplate.opsForValue().set("otp:" + email, otp, 5, TimeUnit.MINUTES);
        System.out.println("redis data: " + redisTemplate.opsForValue().get("otp:" + email));
    }

    public String getOtp(String email) {
        return redisTemplate.opsForValue().get("otp:" + email);
    }

    public boolean verifyOtp(String email, String enteredOtp) {
        String storedOtp = getOtp(email);
        System.out.println("redis data: " + storedOtp);
        if (storedOtp != null && storedOtp.equals(enteredOtp)) {
            redisTemplate.delete("otp:" + email);
            return true;
        }
        return false;
    }
}