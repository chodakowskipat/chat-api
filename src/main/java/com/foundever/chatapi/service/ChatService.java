package com.foundever.chatapi.service;

import com.foundever.chatapi.model.ChatMessage;
import com.foundever.chatapi.model.SupportCase;
import com.foundever.chatapi.repository.ChatMessageRepo;
import com.foundever.chatapi.repository.SupportCaseRepo;
import java.util.ArrayList;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Service handling {@link ChatMessage}. */
@Service
@RequiredArgsConstructor
public class ChatService {

  private final ChatMessageRepo messageRepo;
  private final SupportCaseRepo supportCaseRepo;

  /** Adds a new {@link ChatMessage}. */
  @Transactional
  public ChatMessage addMessage(ChatMessage chatMessage) {
    var savedMessage = messageRepo.save(chatMessage);

    Optional.ofNullable(savedMessage.getSupportCase())
        .map(SupportCase::getId)
        .flatMap(supportCaseRepo::findById)
        .ifPresent(
            supportCase -> {
              chatMessage.setSupportCase(supportCase);
              var messages = new ArrayList<>(supportCase.getMessages());
              messages.add(savedMessage);

              supportCase.setMessages(messages);
            });

    return savedMessage;
  }
}
