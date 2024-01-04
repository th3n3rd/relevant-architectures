package com.example.architectures.ecommerce.infra.klarna;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment-gateway.klarna")
record KlarnaSettings(String uri) {}
