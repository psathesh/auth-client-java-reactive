package com.lib.auth.security.configuration;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class RbsAuthorizationManager<T> implements ReactiveAuthorizationManager<T> {

    private static final Log LOGGER = LogFactory.getLog(RbsAuthorizationManager.class);
    
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, T object) {
        
        LOGGER.info("authentication = " + authentication.block());
        
        ServerWebExchange exchange = ((AuthorizationContext)object).getExchange();
        Principal principal = exchange.getPrincipal().block();
        LOGGER.info("principal = " + principal);
        
        return authentication
            .map(a -> new AuthorizationDecision(a.isAuthenticated()))
            .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
