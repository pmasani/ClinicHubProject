package com.clinichub.visit_service.mqtt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class MqttPublisher {

    private final MessageChannel mqttOut;

    @Value("${mqtt.topic:clinic/visits}")
    private String topicBase;

    public MqttPublisher(MessageChannel mqttOut) {
        this.mqttOut = mqttOut;
    }

    public void publishVisitStarted(String patientId) {
        String payload = """
      {"event":"VISIT_STARTED","patientId":"%s","at":"%s"}
      """.formatted(patientId, java.time.Instant.now());

        String topic = topicBase + "/" + patientId;

        mqttOut.send(
                MessageBuilder.withPayload(payload)
                        .setHeader(MqttHeaders.TOPIC, topic)
                        .build()
        );
    }
}
