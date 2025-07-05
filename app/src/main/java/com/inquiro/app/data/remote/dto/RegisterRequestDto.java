// File: com/inquiro/app/data/remote/dto/RegisterRequestDto.java
package com.inquiro.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class RegisterRequestDto {

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    // Constructor
    public RegisterRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}