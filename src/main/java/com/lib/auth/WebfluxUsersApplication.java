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
    WebClient webClient() {
        return WebClient.create();
    }
    
    @Bean
    RouterFunction<ServerResponse> helloRouterFunction(HelloHandler helloHandler) {
        return RouterFunctions
                .route(RequestPredicates.path("/v1/teacher"), helloHandler::getTeachers)
                .andRoute(RequestPredicates.path("/v1/teacher/data"), helloHandler::getTeachers)
                
                .andRoute(RequestPredicates.path("/v1/student"), helloHandler::getStudents)
                .andRoute(RequestPredicates.path("/v1/both"), helloHandler::getBoth)
                .andRoute(RequestPredicates.path("/v1/openurl"), helloHandler::getOpenURL)
                .andRoute(RequestPredicates.path("/v1/context"), helloHandler::getContext);
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebfluxUsersApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.run();
    }

    /**
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
      return args -> {
        System.out.println("Let's inspect the beans provided by Spring Boot:---------");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
          System.out.println(beanName);
        }
      };
    }
    **/
    
    class HelloHandler {
        
        public  Mono<ServerResponse> getTeachers(ServerRequest serverRequest) {
            return ServerResponse.ok().body(Mono.just("getting teachers"), String.class);
        }

        
        public  Mono<ServerResponse> getStudents(ServerRequest serverRequest) {
            return ServerResponse.ok().body(Mono.just("getting students"), String.class);
        }

        public  Mono<ServerResponse> getBoth(ServerRequest serverRequest) {
            return ServerResponse.ok().body(Mono.just("getting both"), String.class);
        }
        
        public  Mono<ServerResponse> getOpenURL(ServerRequest serverRequest) {
            return ServerResponse.ok().body(Mono.just("getting open urls"), String.class);
        }
        
        public  Mono<ServerResponse> getContext(ServerRequest serverRequest) {
            return ServerResponse.ok().body(Mono.just("getting context"), String.class);
        }
        
    }
    
}
