package com.expensetracker.expenseTracker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private EndpointLimit login    = new EndpointLimit();
    private EndpointLimit register = new EndpointLimit();

    @Data
    public static class EndpointLimit {
        private int capacity      = 5;
        private int refillTokens  = 5;
        private int refillMinutes = 15;
    }
}