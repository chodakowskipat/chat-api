package com.foundever.chatapi.model.dto;

import java.time.Instant;

/** Representation of a {@link com.foundever.chatapi.model.ChatMessage} entity. */
public record ChatMessageDto(
    Long id, Instant timestamp, String content, String username, Long userId, Long supportCaseId) {}
