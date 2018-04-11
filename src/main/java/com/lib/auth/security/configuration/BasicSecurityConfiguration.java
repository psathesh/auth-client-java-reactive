package com.lib.auth.security.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.core.userdetails.UserDetailsMapFactoryBean;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

import com.lib.auth.security.exception.ApplicationAuthenticationEntryPoint;
import com.lib.auth.security.exception.ApplicationAuthenticationSuccessHandler;
import com.lib.auth.security.manager.RbsAuthenticationManager;
import com.lib.auth.security.manager.RbsAuthorizationManager;

@SuppressWarnings("unused")
@ConfigurationProperties(prefix = "service.endpoint.security")
@Configuration
@EnableWebFluxSecurity
public class BasicSecurityConfiguration {

    /** The Constant log. */
    private static final Log LOGGER = LogFactory.getLog(BasicSecurityConfiguration.class);
    
    ServerWebExchangeMatcher basicPattern = new PathPatternParserServerWebExchangeMatcher("/v1/**");
    
    private Map<String, String> users = new HashMap<>();

    public Map<String, String> getUsers() {
        return this.users;
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return new MapReactiveUserDetailsService(getUserDetails());
    }

    private Collection<UserDetails> getUserDetails() {
        try {
            return new UserDetailsMapFactoryBean(users).getObject();
        } catch (Exception e) {
            LOGGER.error("Exception: Creating UserDetails ", e);
            return Collections.<UserDetails>emptyList();
        }
    }

    
    private List<SecurityWebFilterChain> securityWebFilterChains = new ArrayList<>();

    public AuthenticationWebFilter authenticationFilter() {
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(new RbsAuthenticationManager());
        authenticationFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        authenticationFilter.setAuthenticationSuccessHandler(new ApplicationAuthenticationSuccessHandler());
        authenticationFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(new ApplicationAuthenticationEntryPoint()));
        return authenticationFilter;
    }
    
    @Bean
    public List<SecurityWebFilterChain> getSecurityWebFilterChains() {
        LOGGER.debug("Invoking the webflux security chain list");

        securityWebFilterChains.add(springSecurityFilterChain(ServerHttpSecurity.http()));

        return securityWebFilterChains;
    }
    
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        LOGGER.info("Initializing ServerHttpSecurity and configurations");
        
        /**
        http.addFilterAt(new CustomSecurityContextServerWebExchangeWebFilter(), SecurityWebFiltersOrder.SECURITY_CONTEXT_SERVER_WEB_EXCHANGE);
        **/
        
        http.addFilterAt(authenticationFilter(), SecurityWebFiltersOrder.HTTP_BASIC);
        http.exceptionHandling().authenticationEntryPoint(new ApplicationAuthenticationEntryPoint());
        
        return http.csrf().disable()
                .securityMatcher(basicPattern)
                .authorizeExchange()
                    .pathMatchers("/v1/teacher/**").access(new RbsAuthorizationManager<>())
                .and().build();
    }

}
