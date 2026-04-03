package com.aluminate.aluminate_global_backend.config.factory;

import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.aluminate.aluminate_global_backend.config.client.DynamicOrgClient;
/**
 * Factory class for creating dynamic Feign clients targeting different organization backends.
 * This allows the application to communicate with multiple organization services
 * by constructing Feign clients at runtime with the appropriate base URLs.
 */

@Component
@RequiredArgsConstructor
public class DynamicFeignFactory {

    // These are automatically wired from Spring's Feign configuration.
    // We need to inject the raw Encoder/Decoder/Contract here to ensure the dynamic
    // client uses the Spring-configured Jackson/JSON serializers.
    private final Contract contract;
    private final Encoder encoder;
    private final Decoder decoder;

    /**
     * Builds and returns an instance of DynamicOrgClient targeting the given URL.
     *
     * @param url The base URL for the target organization backend.
     * @return A dynamically created Feign client instance.
     */
    public DynamicOrgClient build(String url) {
        // We use the injected components to correctly configure the Feign client
        // for handling complex Java objects (DTOs) and Spring annotations.
        return Feign.builder()
                .contract(contract)
                .encoder(encoder) // FIX: Ensures JSON serialization of @RequestBody DTOs
                .decoder(decoder) // Ensures JSON deserialization of ResponseEntity body
                .target(DynamicOrgClient.class, url);
    }
}
