package com.inghubs.creditmodule.dto;

import lombok.Data;

import java.util.List;

@Data
public class AuthResponse {
    private String token;
    private List<String> roles;

    public AuthResponse(String token, List<String> roles) {
        this.token = token;
        this.roles = roles;
    }

    public AuthResponse(String message) {
        this.token = message;
    }
}

