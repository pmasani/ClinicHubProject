package com.clinichub.visit_service.events;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class VisitEventProducer {
    private final KafkaTemplate<String, Object> template;

    public VisitEventProducer(KafkaTemplate<String, Object> template) {
        this.template = template;
    }

    public void publish(String topic, VisitStartedEvent evt) {
        template.send(topic, evt.patientId(), evt);  // key: patientId

    }
}
