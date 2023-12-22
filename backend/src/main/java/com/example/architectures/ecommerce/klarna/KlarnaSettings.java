package com.example.architectures.ecommerce.klarna;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment-gateway.klarna")
record KlarnaSettings(String uri) {}
