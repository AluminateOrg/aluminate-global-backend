package com.aluminate.aluminate_global_backend.service.kafka;

import com.aluminate.aluminate_global_backend.config.event.GlobalEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for publishing global events to Kafka.
 * Uses organizationName as the key for partitioning and matching.
 */
@Service
public class EventPublisherService {
    private final KafkaTemplate<String, GlobalEvent> kafkaTemplate;

    @Value("${kafka.topic.global-events}")
    private String topicName;

    public EventPublisherService(KafkaTemplate<String, GlobalEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(GlobalEvent event) {
        // Use organizationName as the key for partitioning
        kafkaTemplate.send(topicName, event.getOrganizationName(), event);
    }
}
