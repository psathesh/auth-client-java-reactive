package com.lib.auth.security.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class RBSApplicationErrorHandler {

    private static final Log LOGGER = LogFactory.getLog(
            RBSApplicationErrorHandler.class);

    public Mono<String> handleError(final Throwable throwable) {
        LOGGER.error("error in rbs auth scope call ", throwable);
        if (throwable instanceof WebClientResponseException) {
            throw  new ApplicationAuthenticationException(((WebClientResponseException) throwable).getResponseBodyAsString());
        }
        return Mono.empty();
    }
}
