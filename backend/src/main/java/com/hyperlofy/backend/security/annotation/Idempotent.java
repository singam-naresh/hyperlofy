package com.hyperlofy.backend.security.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    int expireSeconds() default 86400; // Default 24 hours TTL for idempotency key
}
