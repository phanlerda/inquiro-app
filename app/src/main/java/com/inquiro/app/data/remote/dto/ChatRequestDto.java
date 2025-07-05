// File: com/inquiro/app/data/remote/dto/ChatRequestDto.java
package com.inquiro.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatRequestDto {

    // SỬA LỖI: Đổi tên từ "message" thành "query" để khớp API
    @SerializedName("query")
    private String query;

    // SỬA LỖI: Đổi cấu trúc history thành Mảng của các Mảng String
    @SerializedName("history")
    private List<List<String>> history;

    @SerializedName("document_id")
    private Integer documentId;

    // Constructor đã được cập nhật
    public ChatRequestDto(String query, List<List<String>> history, Integer documentId) {
        this.query = query;
        this.history = history;
        this.documentId = documentId;
    }

    // Không cần lớp HistoryItem nữa
}