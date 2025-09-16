package com.luqi.weblog.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author: lu qi
 * @url: www.luqi.com
 * @date: TODO
 * @description: username or password is empty exception
 **/
public class UsernameOrPasswordNullException extends AuthenticationException {
    public UsernameOrPasswordNullException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UsernameOrPasswordNullException(String msg) {
        super(msg);
    }
}
