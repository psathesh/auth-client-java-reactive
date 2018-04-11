package com.lib.auth.security.exception;

import org.springframework.security.core.AuthenticationException;

public class ApplicationAuthenticationException extends AuthenticationException {

    public ApplicationAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public ApplicationAuthenticationException(String msg) {
        super(msg);
    }
}
