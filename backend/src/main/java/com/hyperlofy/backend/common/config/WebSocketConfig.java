package com.hyperlofy.backend.common.config;

import com.hyperlofy.backend.security.jwt.JwtChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Spring WebSockets & STOMP Broker configuration to support
 * ultra-low latency bi-directional streaming of spatial GPS updates
 * and dispatch status transitions straight to customer and agent channels.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Topic prefix for Pub/Sub subscriptions (e.g. /topic/order/{id}, /topic/admin/dashboard)
        config.enableSimpleBroker("/topic", "/queue");
        
        // Application destination prefix for target client actions (e.g. /app/send-gps)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register connection border STOMP gateways
        registry.addEndpoint("/api/v1/ws-gateway")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}
