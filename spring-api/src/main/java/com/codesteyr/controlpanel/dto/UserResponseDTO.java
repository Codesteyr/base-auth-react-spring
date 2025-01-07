package com.codesteyr.controlpanel.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private String name;
    private String email;
    private String message;
    private String role;
    private int coin;
    private int statusCode;
    private String error;
    private String token;
    private String refreshToken;
    private String expirationTime;
}
