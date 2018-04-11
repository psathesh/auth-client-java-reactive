package com.lib.auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@ComponentScan(basePackages = { "com.lib.auth" })
@SpringBootApplication
@EnableWebFlux
public class WebfluxUsersApplication {

    
    @Autowired
    ApplicationContext context;
    
    @Autowired
    WebClient webClient;
    
    @Bean
    HelloHandler helloHandler() {
        return new HelloHandler();
    }
    
    @Bean
    WebClient createWebClient() {
        return WebClient.create();
    }
    
    @Bean
    RouterFunction<ServerResponse> helloRouterFunction(HelloHandler helloHandler) {
        return RouterFunctions
                .route(RequestPredicates.path("/v1/teacher"), helloHandler::getTeachers);
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebfluxUsersApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.run();
    }
    
    class HelloHandler {
        
        public  Mono<ServerResponse> getTeachers(ServerRequest serverRequest) {
            return ServerResponse.ok().body(Mono.just("getting teachers"), String.class);
        }
        
    }
    
}
