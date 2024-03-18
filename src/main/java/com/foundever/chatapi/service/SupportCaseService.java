package com.foundever.chatapi.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.foundever.chatapi.model.ChatMessage;
import com.foundever.chatapi.model.SupportCase;
import com.foundever.chatapi.repository.ChatMessageRepo;
import com.foundever.chatapi.repository.SupportCaseRepo;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupportCaseService {

  private final ChatMessageRepo chatMessageRepo;
  private final SupportCaseRepo supportCaseRepo;

  /** Creates a new {@link SupportCase} connected with at least one existing {@link ChatMessage}. */
  @Transactional
  public SupportCase createSupportCase(SupportCase supportCase) {
    var chatMessagesIds = supportCase.getMessages().stream().map(ChatMessage::getId).toList();
    if (chatMessagesIds.isEmpty()) {
      throw new IllegalArgumentException("Need at least one message to create a support case for.");
    }

    var chatMessages = chatMessageRepo.findAllById(chatMessagesIds);
    var supportCasesIdsToChatMessagesIds =
        chatMessages.stream()
            .filter(chatMessage -> chatMessage.getSupportCase() != null)
            .collect(
                groupingBy(
                    message -> message.getSupportCase().getId(),
                    mapping(ChatMessage::getId, toList())));

    if (!supportCasesIdsToChatMessagesIds.isEmpty()) {
      throw new IllegalArgumentException(
          String.format(
              "Some of the messages are already assigned to a support case: %s",
              supportCasesIdsToChatMessagesIds));
    }

    supportCase.setMessages(chatMessages);
    var savedSupportCase = supportCaseRepo.save(supportCase);
    chatMessages.forEach(chatMessage -> chatMessage.setSupportCase(savedSupportCase));

    return savedSupportCase;
  }

  /** Returns all existing {@link SupportCase}. */
  public List<SupportCase> getAll() {
    return supportCaseRepo.findAll();
  }

  /**
   * Patches a {@link SupportCase} overwriting existing values with those from {@code
   * modifiedSupportCase} (if they are non-null).
   */
  @Transactional
  public Optional<SupportCase> patchSupportCase(SupportCase modifiedSupportCase) {
    return supportCaseRepo
        .findById(modifiedSupportCase.getId())
        .map(
            supportCase -> {
              supportCase.setClientReference(modifiedSupportCase.getClientReference());
              return supportCase;
            });
  }
}
