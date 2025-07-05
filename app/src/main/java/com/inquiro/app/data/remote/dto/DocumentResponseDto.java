// File: com/inquiro/app/data/remote/dto/DocumentResponseDto.java
package com.inquiro.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class DocumentResponseDto {

    @SerializedName("id")
    private int id;

    @SerializedName("filename")
    private String filename;

    @SerializedName("status")
    private String status;

    @SerializedName("owner_id")
    private int ownerId;

    // Getters
    public int getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getStatus() {
        return status;
    }

    public int getOwnerId() {
        return ownerId;
    }
}