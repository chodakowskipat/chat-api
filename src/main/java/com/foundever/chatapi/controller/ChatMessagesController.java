package com.foundever.chatapi.controller;

import com.foundever.chatapi.mapper.ChatMessageMapper;
import com.foundever.chatapi.model.dto.ChatMessageDto;
import com.foundever.chatapi.service.ChatService;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/messages")
public class ChatMessagesController {

  private final Clock clock;
  private final ChatService chatService;
  private final ChatMessageMapper chatMessageMapper;

  @PostMapping
  ChatMessageDto addChatMessage(@RequestBody ChatMessageDto chatMessageDto) {
    log.atTrace().log("Received addChatMessage request: {}", chatMessageDto);

    var messageToSave =
        chatMessageMapper.toChatMessage(chatMessageDto).toBuilder()
            .timestamp(clock.instant())
            .build();

    var savedMessage = chatService.addMessage(messageToSave);
    log.atTrace().log("Saved new message: {}", savedMessage);

    return chatMessageMapper.toChatMessageDto(savedMessage);
  }
}
