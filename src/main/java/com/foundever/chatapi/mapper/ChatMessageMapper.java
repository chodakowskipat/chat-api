package com.foundever.chatapi.mapper;

import com.foundever.chatapi.model.ChatMessage;
import com.foundever.chatapi.model.SupportCase;
import com.foundever.chatapi.model.dto.ChatMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** A mapper between {@link ChatMessage} and {@link ChatMessageDto} objects. */
@Mapper(componentModel = "spring")
public interface ChatMessageMapper {

  /** Maps {@link ChatMessageDto to {@link ChatMessage}. */
  @Mapping(target = "supportCase", source = "supportCaseId")
  ChatMessage toChatMessage(ChatMessageDto chatMessageDto);

  /** Maps {@link ChatMessage} to {@link ChatMessageDto}. */
  @Mapping(target = "supportCaseId", source = "supportCase.id")
  ChatMessageDto toChatMessageDto(ChatMessage chatMessage);

  /** Creates a {@link SupportCase} with just the {@code id} field. */
  default SupportCase fromId(Long id) {
    if (id == null) {
      return null;
    }
    return SupportCase.builder().id(id).build();
  }
}
