package com.hyperlofy.backend.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtTokenProvider.validateToken(token)) {
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    String role = jwtTokenProvider.getRoleFromToken(token);
                    String userId = jwtTokenProvider.getUserIdFromToken(token);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                    accessor.setUser(authentication);
                    log.info("WebSocket user verified: {}, Role: {}, UserId: {}", email, role, userId);
                } else {
                    throw new AccessDeniedException("Invalid JWT token for WebSocket connection.");
                }
            } else {
                throw new AccessDeniedException("No Authorization header provided for WebSocket connection.");
            }
        }

        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination != null) {
                UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) accessor.getUser();
                if (user == null) {
                    throw new AccessDeniedException("Unauthenticated subscription request to topic: " + destination);
                }
                String role = user.getAuthorities().iterator().next().getAuthority();

                log.info("Checking STOMP SUBSCRIBE access on destination: {} with role: {}", destination, role);

                // Admin dashboard topics require ADMIN or SUPER_ADMIN
                if (destination.startsWith("/topic/admin/") && 
                        !role.equals("ROLE_ADMIN") && 
                        !role.equals("ROLE_SUPER_ADMIN")) {
                    throw new AccessDeniedException("Access denied to admin channel topic: " + destination);
                }
                // Agent specific topics require AGENT or ADMIN role
                if (destination.startsWith("/topic/agent/") && 
                        !role.equals("ROLE_AGENT") && 
                        !role.equals("ROLE_ADMIN") && 
                        !role.equals("ROLE_SUPER_ADMIN")) {
                    throw new AccessDeniedException("Access denied to agent channel topic: " + destination);
                }
            }
        }

        return message;
    }
}
