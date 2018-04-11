package com.lib.auth.security.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.SecurityContextServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SuppressWarnings("unused")
public class ApplicationAuthenticationSuccessHandler
    implements ServerAuthenticationSuccessHandler {
    
    private static final Log LOGGER = LogFactory.getLog(ApplicationAuthenticationSuccessHandler.class);
    
    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
        Authentication authentication) {
        LOGGER.info("custom ApplicationAuthenticationSuccessHandler -------------------------> " + authentication.getName());
        ServerWebExchange exchange = webFilterExchange.getExchange();
        
        /** 
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        return webFilterExchange.getChain().filter(new SecurityContextServerWebExchange(exchange, Mono.just(securityContext)));
        **/
        
        /** **/ 
        return webFilterExchange.getChain().filter(exchange);
        
        
    }
}
