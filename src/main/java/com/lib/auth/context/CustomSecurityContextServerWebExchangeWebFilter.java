package com.lib.auth.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.SecurityContextServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class CustomSecurityContextServerWebExchangeWebFilter implements WebFilter {

    private static final Log LOGGER = LogFactory.getLog(CustomSecurityContextServerWebExchangeWebFilter.class);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        LOGGER.info("custom CustomSecurityContextServerWebExchangeWebFilter -------------------------> " + exchange.getPrincipal().block());
        
        /** 
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication((Authentication) exchange.getPrincipal().block());
        return chain.filter(new SecurityContextServerWebExchange(exchange, Mono.just(securityContext)));
        **/
        
        /** **/
        return chain.filter(new SecurityContextServerWebExchange(exchange, ReactiveSecurityContextHolder.getContext()));
        
    }
}
