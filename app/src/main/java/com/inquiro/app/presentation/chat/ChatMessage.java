// File: com/inquiro/app/presentation/chat/ChatMessage.java
package com.inquiro.app.presentation.chat;

import androidx.annotation.Nullable;

import com.inquiro.app.data.remote.dto.SourceDto;

import java.util.List;
import java.util.UUID;

public class ChatMessage {
    public enum Sender {
        USER, BOT
    }

    private final String id;
    private final String text;
    private final Sender sender;
    // THÊM MỚI: Trường để lưu danh sách nguồn
    @Nullable
    private final List<SourceDto> sources;

    // Constructor cho tin nhắn của USER (không có sources)
    public ChatMessage(String text, Sender sender) {
        this(text, sender, null);
    }

    // Constructor cho tin nhắn của BOT (có thể có sources)
    public ChatMessage(String text, Sender sender, @Nullable List<SourceDto> sources) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.sender = sender;
        this.sources = sources;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Sender getSender() {
        return sender;
    }

    @Nullable
    public List<SourceDto> getSources() {
        return sources;
    }
}