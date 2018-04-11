package com.lib.auth.security.exception;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.ModelMap;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class ApplicationSecurityUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSecurityUtility.class);

    private static final String REALM = "Basic realm";

    private static final String ERROR_ATTRIBUTES = "error_attributes";
    
    
    private ApplicationSecurityUtility() {}

    
    /**
     * {@inheritDoc}
     */
    public static Mono<Void> errorResponseWriter(ServerWebExchange exchange, Exception ex, HttpMessageWriter<Object> writer, String app) {
        LOGGER.debug("handling error response");
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

