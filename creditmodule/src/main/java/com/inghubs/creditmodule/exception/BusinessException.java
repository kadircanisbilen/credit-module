package com.inghubs.creditmodule.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Object[] params;

    public BusinessException(String message, Object... params) {
        super(message);
        this.params = params;
    }

}
