package com.lib.auth.security.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class ApplicationAuthenticationEntryPoint extends HttpBasicServerAuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationAuthenticationEntryPoint.class);

    private static final String SERVICE_NAME = "Service";

    private DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    private static HttpMessageWriter<Object> writer = new EncoderHttpMessageWriter<>(new Jackson2JsonEncoder());

    @Autowired
    ErrorWebExceptionHandler handler;

    public DataBuffer fromInserters() {
        return dataBufferFactory.allocateBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        LOGGER.error("Authentication Error: {}" + ex.getMessage() + ", " + ex.getClass() + " " + ex.getCause());
        return ApplicationSecurityUtility.errorResponseWriter(exchange, ex, writer, SERVICE_NAME);
    }
}
