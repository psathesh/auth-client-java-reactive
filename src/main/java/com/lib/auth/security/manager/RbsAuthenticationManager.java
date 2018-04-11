package com.lib.auth.security.manager;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;

public class RbsAuthenticationManager implements ReactiveAuthenticationManager {


	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
	    Authentication tokenAuthentication = new UsernamePasswordAuthenticationToken("USER", "N/A", null);
	    ((AbstractAuthenticationToken)tokenAuthentication).setDetails("USER DETAILS");
	    return Mono.just(tokenAuthentication);
	}

}
