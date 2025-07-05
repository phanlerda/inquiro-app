// File: com/inquiro/app/data/remote/dto/UserResponseDto.java
package com.inquiro.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class UserResponseDto {

    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("is_active")
    private boolean isActive;

    // Getters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return isActive;
    }
}