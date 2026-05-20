package com.smartlab.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtAuthResponse {

    private String token;
    private String username;
    private String role;
}
