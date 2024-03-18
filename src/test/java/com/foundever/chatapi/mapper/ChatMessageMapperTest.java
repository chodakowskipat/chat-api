package com.foundever.chatapi.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.foundever.chatapi.model.ChatMessage;
import com.foundever.chatapi.model.SupportCase;
import com.foundever.chatapi.model.dto.ChatMessageDto;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ChatMessageMapperTest {

  private static final Clock clock =
      Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));

  private final ChatMessageMapper mapper = Mappers.getMapper(ChatMessageMapper.class);

  @Test
  void toChatMessage_validChatMessageDto_validChatMessage() {
    ChatMessageDto chatMessageDto =
        new ChatMessageDto(123L, Instant.now(clock), "content", "username", 321L, 456L);

    ChatMessage actual = mapper.toChatMessage(chatMessageDto);

    assertThat(actual)
        .isEqualTo(
            ChatMessage.builder()
                .id(123L)
                .timestamp(Instant.now(clock))
                .content("content")
                .username("username")
                .userId(321L)
                .supportCase(SupportCase.builder().id(456L).build())
                .build());
  }

  @Test
  void toChatMessageDto_validChatMessage_validChatMessageDto() {
    ChatMessage chatMessage =
        ChatMessage.builder()
            .id(123L)
            .timestamp(Instant.now(clock))
            .content("content")
            .username("username")
            .userId(321L)
            .supportCase(SupportCase.builder().id(456L).build())
            .build();

    ChatMessageDto actual = mapper.toChatMessageDto(chatMessage);

    assertThat(actual)
        .isEqualTo(new ChatMessageDto(123L, Instant.now(clock), "content", "username", 321L, 456L));
  }

  @Test
  void fromId_validId_validSupportCase() {
    SupportCase supportCase = mapper.fromId(123L);

    assertThat(supportCase).isEqualTo(SupportCase.builder().id(123L).build());
  }
}
