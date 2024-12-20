package com.inghubs.creditmodule.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final Object[] params;

    public ResourceNotFoundException(String message, Object... params) {
        super(message);
        this.params = params;
    }

}
