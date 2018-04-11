/*
 * Copyright 2018, Pearson Education, Learning Technology Group
 *
 * ApplicationAuthenticationEntryPoint.java
 */
package com.lib.auth.security.exception;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Under the BasicAuthentication and RBSAuthentication when Used by the
 * ExceptionTraslationFilter to commence authentication via the
 * BasicAuthenticationFilter.
 * 
 * @author Team Unplugged
 *
 */
public class ApplicationAccessDeniedHandler implements ServerAccessDeniedHandler {
    private final HttpStatus httpStatus;

    private static final Log LOGGER = LogFactory.getLog(ApplicationAccessDeniedHandler.class);
    
    public ApplicationAccessDeniedHandler(HttpStatus httpStatus) {
        Assert.notNull(httpStatus, "httpStatus cannot be null");
        this.httpStatus = httpStatus;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException e) {
        System.out.println("CUSTOM ACCESS DENIED ----------------------------------------- ");
        LOGGER.info("CUSTOM ACCESS DENIED ----------------------------------------- ");
        
        return Mono.defer(() -> Mono.just(exchange.getResponse()))
            .flatMap(response -> {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
                DataBufferFactory dataBufferFactory = response.bufferFactory();
                DataBuffer buffer = dataBufferFactory.wrap(e.getMessage().getBytes(
                    Charset.defaultCharset()));
                return response.writeWith(Mono.just(buffer))
                    .doOnError( error -> DataBufferUtils.release(buffer));
        });
    }
}
