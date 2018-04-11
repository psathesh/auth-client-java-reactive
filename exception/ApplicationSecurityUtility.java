/*
 * Copyright 2018, Pearson Education, Learning Technology Group
 *
 * ApplicationAuthenticationEntryPoint.java
 */
package com.lib.auth.security.exception;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.ModelMap;
import org.springframework.web.reactive.result.view.HttpMessageWriterView;
import org.springframework.web.reactive.result.view.View;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Under the BasicAuthentication and RBSAuthentication when Used by the
 * ExceptionTraslationFilter to commence authentication via the
 * BasicAuthenticationFilter.
 * 
 * @author Team Unplugged
 *
 */
public class ApplicationSecurityUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSecurityUtility.class);

    private static final String REALM = "Basic realm";

    private static final String ERROR_ATTRIBUTES = "error_attributes";
    
    private static DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
    
    private ApplicationSecurityUtility() {}

    
    /**
     * {@inheritDoc}
     */
    public static Mono<Void> errorResponseHandler(ServerWebExchange exchange, Exception ex, WebExceptionHandler handler, String app) {
        processResponse(exchange, app);
        ex = new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);

        if (ex instanceof AuthenticationCredentialsNotFoundException || 
                ex instanceof BadCredentialsException) {
            ex = new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex);
        }
        return handler.handle(exchange, ex);
    }
    
    /**
     * {@inheritDoc}
     */
    public static Mono<Void> errorResponseRenderer(ServerWebExchange exchange, Exception ex, View view, String app) {
        processResponse(exchange, app);

        Map<String, ?> model = getErrorAttributes(exchange.getRequest(), ex, app);
        ((HttpMessageWriterView) view).setModelKeys(Collections.singleton(ERROR_ATTRIBUTES));
        
        return view.render(model, MediaType.APPLICATION_JSON, exchange);
    }
    
    /**
     * {@inheritDoc}
     */
    public static Mono<Void> errorResponseMapper(ServerWebExchange exchange, Exception ex, ObjectMapper mapper, String app) {
        ServerHttpResponse response = processResponse(exchange, app);
        
        Map<String, ?> model = getErrorAttributes(exchange.getRequest(), ex, app);
        try {
            DataBuffer dataBuffer = dataBufferFactory.wrap(mapper.writeValueAsBytes(model.get(ERROR_ATTRIBUTES)));
            return response.writeWith(Flux.just(dataBuffer));
        } catch (JsonProcessingException e) {
            LOGGER.error("Exception at mapper: ", e);
            return response.writeWith(Flux.just(dataBufferFactory.allocateBuffer()));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public static Mono<Void> errorResponseWriter(ServerWebExchange exchange, Exception ex, HttpMessageWriter<Object> writer, String app) {
        ServerHttpResponse response = processResponse(exchange, app);
        Map<String, ?> model = getErrorAttributes(exchange.getRequest(), ex, app);
        Object value = model.get(ERROR_ATTRIBUTES);

        Publisher<?> input = Mono.justOrEmpty(value);
        ResolvableType elementType = ResolvableType.forClass(value.getClass());
        return writer.write(input, elementType, MediaType.APPLICATION_JSON, response, Collections.emptyMap());
    }
    
    private static ServerHttpResponse processResponse(ServerWebExchange exchange, String app) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, String.join("=", REALM, app));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        return response;
    }
    
    private static Map<String, Object> getErrorAttributes(ServerHttpRequest request, Exception ex, String app) {
        
        ModelMap model = new ExtendedModelMap();
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", new Date());
        errorAttributes.put("path", request.getPath().value());
        errorAttributes.put("status", HttpStatus.UNAUTHORIZED.value());
        errorAttributes.put("error", HttpStatus.UNAUTHORIZED.name());
        errorAttributes.put("message", ex.getMessage());
        errorAttributes.put("application", app);

        model.addAttribute(ERROR_ATTRIBUTES, errorAttributes);
        return model;
    }
    
}

