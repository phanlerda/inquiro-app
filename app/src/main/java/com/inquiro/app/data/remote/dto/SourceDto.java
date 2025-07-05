// File: com/inquiro/app/data/remote/dto/SourceDto.java
package com.inquiro.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class SourceDto {

    // SỬA LỖI: Thêm các trường khớp với API
    @SerializedName("document_id")
    private int documentId;

    @SerializedName("filename")
    private String filename;

    @SerializedName("text")
    private String text;

    // Getters
    public int getDocumentId() {
        return documentId;
    }

    public String getFilename() {
        return filename;
    }

    public String getText() {
        return text;
    }
}