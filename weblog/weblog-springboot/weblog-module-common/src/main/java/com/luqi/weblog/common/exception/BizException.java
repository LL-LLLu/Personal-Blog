package com.luqi.weblog.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: lu qi
 * @url: www.luqi.com
 * @date: 2023-08-15 9:52
 * @description: Business Exception
 **/
@Getter
@Setter
public class BizException extends RuntimeException {
    // Exception Code
    private String errorCode;
    // Error Message
    private String errorMessage;

    public BizException(BaseExceptionInterface baseExceptionInterface) {
        this.errorCode = baseExceptionInterface.getErrorCode();
        this.errorMessage = baseExceptionInterface.getErrorMessage();
    }
}