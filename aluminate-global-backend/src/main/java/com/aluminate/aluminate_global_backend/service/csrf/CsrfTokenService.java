package com.aluminate.aluminate_global_backend.service.csrf;


import com.aluminate.aluminate_global_backend.controller.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
public class CsrfTokenService {
    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public CsrfTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

    }

    public String generateAndStoreToken(String sessionId) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String csrfToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        try{



            String redisKey = "csrf:" + sessionId;
            redisTemplate.opsForValue().set(redisKey, csrfToken, Duration.ofDays(1)); // TTL for CSRF

        }catch(Exception e){

            throw new RuntimeException(e);
        }


        return csrfToken;
    }

    public boolean validateToken(String sessionId, String tokenFromClient) {
        String redisKey = "csrf:" + sessionId;

        String storedToken = redisTemplate.opsForValue().get(redisKey);
        return storedToken != null && storedToken.equals(tokenFromClient);
    }
}
