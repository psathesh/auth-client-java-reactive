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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.core.userdetails.UserDetailsMapFactoryBean;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.MatcherSecurityWebFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authorization.ExceptionTranslationWebFilter;
import org.springframework.security.web.server.context.SecurityContextServerWebExchangeWebFilter;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.WebFilter;

import com.lib.auth.context.CustomSecurityContextServerWebExchangeWebFilter;
import com.lib.auth.security.exception.ApplicationAccessDeniedHandler;
import com.lib.auth.security.exception.ApplicationAuthenticationEntryPoint;
import com.lib.auth.security.exception.ApplicationAuthenticationSuccessHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    
    public UserDetailsRepositoryReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService());
    }
    
    private List<SecurityWebFilterChain> securityWebFilterChains = new ArrayList<>();

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
        
        new ApplicationAuthenticationSuccessHandler();
        SecurityWebFilterChain securityWebFilterChain = http.csrf().disable()
                .securityMatcher(basicPattern)
                .authorizeExchange()
                    .pathMatchers("/v1/student/**").hasAuthority("student")
                    .pathMatchers("/v1/teacher/**").hasAuthority("teacher")
                    .pathMatchers("/v1/openurl/**").permitAll()
                    .pathMatchers("/v1/context/**").access(new RbsAuthorizationManager<>())
                .and().authenticationManager(reactiveAuthenticationManager()).httpBasic()
                .and().build();

        exceptionHandling(securityWebFilterChain.getWebFilters().collectList(), basicPattern);
        return securityWebFilterChain;

    }
    
    public void exceptionHandling(Mono<List<WebFilter>> webFilter, ServerWebExchangeMatcher matcher) {
        LOGGER.debug("Setting entry points and access denied Handlers");
        webFilter.block().forEach(filter -> {
            if (filter instanceof ExceptionTranslationWebFilter) {
                ((ExceptionTranslationWebFilter) filter).setAccessDeniedHandler(new ApplicationAccessDeniedHandler(HttpStatus.FORBIDDEN));
                ((ExceptionTranslationWebFilter) filter).setAuthenticationEntryPoint(new ApplicationAuthenticationEntryPoint());
            } else if (filter instanceof AuthenticationWebFilter) {
                ((AuthenticationWebFilter) filter).setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(new ApplicationAuthenticationEntryPoint()));
                ((AuthenticationWebFilter) filter).setRequiresAuthenticationMatcher(matcher);
                ((AuthenticationWebFilter) filter).setAuthenticationSuccessHandler(new ApplicationAuthenticationSuccessHandler());
            }
        });
    }
}
