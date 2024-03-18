package com.foundever.chatapi.model.dto;

import java.util.List;

/** Representation of a {@link com.foundever.chatapi.model.SupportCase} entity. */
public record SupportCaseDto(Long id, String clientReference, List<ChatMessageDto> messages) {}
