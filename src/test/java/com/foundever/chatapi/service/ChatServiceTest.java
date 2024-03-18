package com.foundever.chatapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.foundever.chatapi.model.ChatMessage;
import com.foundever.chatapi.model.SupportCase;
import com.foundever.chatapi.repository.ChatMessageRepo;
import com.foundever.chatapi.repository.SupportCaseRepo;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  private static final Clock clock =
      Clock.fixed(Instant.parse("2014-12-23T10:15:30.00Z"), ZoneId.of("UTC"));

  @Mock private ChatMessageRepo messageRepo;

  @Mock private SupportCaseRepo supportCaseRepo;

  @InjectMocks private ChatService chatService;

  @Test
  void addMessage_noSupportCase_messageSaved() {
    ChatMessage chatMessageToAdd =
        ChatMessage.builder()
            .userId(123L)
            .timestamp(Instant.now(clock))
            .content("this is a message")
            .build();
    ChatMessage addedChatMessage = chatMessageToAdd.toBuilder().id(12345L).build();
    when(messageRepo.save(chatMessageToAdd)).thenReturn(addedChatMessage);

    ChatMessage actual = chatService.addMessage(chatMessageToAdd);

    assertThat(actual).isEqualTo(addedChatMessage);
    verifyNoInteractions(supportCaseRepo);
  }

  @Test
  void addMessage_supportCaseExists_messageSaved() {
    SupportCase supportCase = SupportCase.builder().id(456L).messages(List.of()).build();
    ChatMessage chatMessageToAdd =
        ChatMessage.builder()
            .userId(123L)
            .supportCase(supportCase)
            .timestamp(Instant.now(clock))
            .content("this is a message")
            .build();
    ChatMessage addedChatMessage = chatMessageToAdd.toBuilder().id(12345L).build();
    when(messageRepo.save(chatMessageToAdd)).thenReturn(addedChatMessage);
    when(supportCaseRepo.findById(anyLong())).thenReturn(Optional.of(supportCase));

    ChatMessage savedMessage = chatService.addMessage(chatMessageToAdd);

    assertThat(savedMessage).isEqualTo(addedChatMessage);
  }

  @Test
  void addMessage_supportCaseExists_supportCaseUpdated() {
    SupportCase supportCase = SupportCase.builder().id(456L).build();
    SupportCase supportCaseWithMessages =
        supportCase.toBuilder()
            .messages(
                List.of(
                    ChatMessage.builder().id(1L).build(),
                    ChatMessage.builder().id(2L).build(),
                    ChatMessage.builder().id(3L).build()))
            .build();
    ChatMessage chatMessageToAdd =
        ChatMessage.builder()
            .userId(123L)
            .supportCase(supportCase)
            .timestamp(Instant.now(clock))
            .content("this is a message")
            .build();
    ChatMessage addedChatMessage = chatMessageToAdd.toBuilder().id(12345L).build();
    when(messageRepo.save(chatMessageToAdd)).thenReturn(addedChatMessage);
    when(supportCaseRepo.findById(anyLong())).thenReturn(Optional.of(supportCaseWithMessages));

    chatService.addMessage(chatMessageToAdd);

    assertThat(supportCaseWithMessages.getMessages())
        .containsExactly(
            ChatMessage.builder().id(1L).build(),
            ChatMessage.builder().id(2L).build(),
            ChatMessage.builder().id(3L).build(),
            addedChatMessage);
  }
}
