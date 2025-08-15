package com.aluminate.aluminate_global_backend.config.kafka;

import com.aluminate.aluminate_global_backend.config.event.GlobalEvent;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private final Logger log = LoggerFactory.getLogger(KafkaProducerConfig.class);


    /**
     * Configures the Kafka producer factory with critical reliability settings.
     * - Ensures messages are fully replicated before acknowledging
     * - Sets appropriate timeouts to avoid blocking indefinitely
     * - Configures retries for transient errors
     */

    @Bean
    public ProducerFactory<String, GlobalEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Critical reliability configurations
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Ensure full replication
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // Number of retries
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // 2 minutes
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // 30 seconds
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 60000); // 1-minute max block

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, GlobalEvent> kafkaTemplate() {
        KafkaTemplate<String, GlobalEvent> template = new KafkaTemplate<>(producerFactory());

        // Configure producer listener for error handling
        template.setProducerListener(new ProducerListener<String, GlobalEvent>() {


            public void onError(ProducerRecord<String, GlobalEvent> record,
                                Exception exception,
                                Producer<String, GlobalEvent> producer) {
                log.error("Failed to send message: {}", record.value(), exception);
                // Add your recovery logic here (save to DB, retry later, etc.)
            }

            @Override
            public void onSuccess(ProducerRecord<String, GlobalEvent> record,
                                  RecordMetadata recordMetadata) {
                log.debug("Message sent successfully: {}", record.value());
            }
        });

        return template;
    }
}
