package com.aluminate.aluminate_global_backend.config;

import com.sendgrid.SendGrid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SengridConfig {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Bean
    public SendGrid sendGrid() {
        log.info("Initializing SendGrid client");
        return new SendGrid(sendGridApiKey);
    }

}