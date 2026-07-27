package com.hyperlofy.backend.security.interceptor;

import com.hyperlofy.backend.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RateLimiterInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterInterceptor.class);
    private final RedisTemplate<String, Object> redisTemplate;

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int STRICT_MAX_REQUESTS_PER_MINUTE = 10; // Login, OTP, Register

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        String uri = request.getRequestURI();

        int limit = MAX_REQUESTS_PER_MINUTE;
        if (uri.contains("/auth/") || uri.contains("/coupons/validate") || uri.contains("/checkout")) {
            limit = STRICT_MAX_REQUESTS_PER_MINUTE;
        }

        String redisKey = "rate_limit:" + clientIp + ":" + uri;

        try {
            Long count = redisTemplate.opsForValue().increment(redisKey, 1);
            if (count != null && count == 1) {
                redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
            }

            if (count != null && count > limit) {
                log.warn("Rate limit exceeded for IP [{}] on URI [{}]: count={}", clientIp, uri, count);
                response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
                response.setHeader("X-RateLimit-Remaining", "0");
                response.setHeader("Retry-After", "60");
                throw new BusinessException("Rate limit exceeded. Please try again in 60 seconds.", HttpStatus.TOO_MANY_REQUESTS);
            }

            response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(limit - (count != null ? count : 0)));
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.warn("Redis fallback during rate limiting check: {}", e.getMessage());
        }

        return true;
    }
}
