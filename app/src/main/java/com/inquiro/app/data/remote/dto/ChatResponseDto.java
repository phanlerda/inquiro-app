// File: com/inquiro/app/data/remote/dto/ChatResponseDto.java
package com.inquiro.app.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatResponseDto {

    // SỬA LỖI: Đổi tên từ "response" thành "answer" để khớp API
    @SerializedName("answer")
    private String answer;

    @SerializedName("sources")
    private List<SourceDto> sources;

    // Getter đã được cập nhật
    public String getAnswer() {
        return answer;
    }

    public List<SourceDto> getSources() {
        return sources;
    }
}