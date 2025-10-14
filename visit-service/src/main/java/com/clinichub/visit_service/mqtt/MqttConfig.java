package com.clinichub.visit_service.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;

import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
public class MqttConfig {

    @Value("${mqtt.broker}") String broker;                 // e.g. tcp://localhost:1883
    @Value("${mqtt.clientId:visit-service}") String clientId;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        var factory = new DefaultMqttPahoClientFactory();
        var options = new MqttConnectOptions();
        options.setServerURIs(new String[]{broker});
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttOut() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOut")
    public MessageHandler mqttOutbound(MqttPahoClientFactory factory) {
        var handler = new MqttPahoMessageHandler(clientId + "-publisher", factory);
        handler.setAsync(true);
        handler.setDefaultQos(0); // at-most-once
        return handler;
    }
}
