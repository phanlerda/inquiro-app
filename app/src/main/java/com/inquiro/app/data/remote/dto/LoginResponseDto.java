// File: com/inquiro/app/data/remote/dto/LoginResponseDto.java
package com.inquiro.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponseDto {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;

    // Getters
    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}