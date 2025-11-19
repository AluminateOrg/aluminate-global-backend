package com.aluminate.aluminate_global_backend.config.client;

import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;

/**
 * Configuration class for Feign client setup.
 *
 * This class explicitly exposes the core Feign components (Contract, Encoder, Decoder)
 * as injectable Spring beans, using the Spring Cloud implementations to ensure
 * they correctly integrate with Spring MVC and Jackson for JSON processing.
 * This resolves the "No beans of 'Contract' type found" autowiring errors
 * in components like the DynamicFeignFactory.
 */
@Configuration
public class FeignConfig {

    private final ObjectFactory<HttpMessageConverters> messageConverters;

    // Inject the factory for Spring's standard message converters (includes Jackson for JSON)
    public FeignConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    // --- Core Feign Components Exposed as Injectable Beans ---

    /**
     * Exposes the Contract bean, which teaches Feign how to interpret Spring MVC annotations.
     */
    @Bean
    public Contract feignContract() {
        return new SpringMvcContract();
    }

    /**
     * Exposes the Encoder bean, which converts Java objects (DTOs) into the request body (JSON).
     * This uses Spring's converters to ensure proper JSON serialization.
     */
    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(this.messageConverters);
    }

    /**
     * Exposes the Decoder bean, which converts the response body (JSON) back into Java objects (DTOs).
     * This uses Spring's converters to ensure proper JSON deserialization.
     */
    @Bean
    public Decoder feignDecoder() {
        return new SpringDecoder(this.messageConverters);
    }

    // Note: The generic feignBuilder() bean is no longer needed since the DynamicFeignFactory
    // now manually builds the client using these injected components.
}