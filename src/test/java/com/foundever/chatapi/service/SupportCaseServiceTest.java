package com.foundever.chatapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.foundever.chatapi.model.ChatMessage;
import com.foundever.chatapi.model.SupportCase;
import com.foundever.chatapi.repository.ChatMessageRepo;
import com.foundever.chatapi.repository.SupportCaseRepo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SupportCaseServiceTest {

  @Mock private ChatMessageRepo chatMessageRepo;

  @Mock private SupportCaseRepo supportCaseRepo;

  @InjectMocks private SupportCaseService supportCaseService;

  @Test
  public void createSupportCase_noMessages_exceptionThrown() {
    SupportCase supportCase = SupportCase.builder().messages(List.of()).build();

    assertThatThrownBy(() -> supportCaseService.createSupportCase(supportCase))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Need at least one message to create a support case for.");
    verifyNoInteractions(supportCaseRepo);
  }

  @Test
  public void createSupportCase_messagesAlreadyAssigned_exceptionThrown() {
    ChatMessage newMessage = ChatMessage.builder().id(1L).build();
    ChatMessage alreadyAssignedMessage = ChatMessage.builder().id(2L).build();
    SupportCase supportCaseToAdd =
        SupportCase.builder().messages(List.of(newMessage, alreadyAssignedMessage)).build();

    when(chatMessageRepo.findAllById(anyList()))
        .thenReturn(
            List.of(
                newMessage,
                alreadyAssignedMessage.toBuilder()
                    .supportCase(SupportCase.builder().id(123L).build())
                    .build()));

    assertThatThrownBy(() -> supportCaseService.createSupportCase(supportCaseToAdd))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Some of the messages are already assigned to a support case: %s",
            Map.of(123L, List.of(2L)));
    verifyNoInteractions(supportCaseRepo);
  }

  @Test
  public void createSupportCase_validInput_caseCreated() {
    ChatMessage newMessage = ChatMessage.builder().id(1L).build();
    SupportCase supportCaseToAdd = SupportCase.builder().messages(List.of(newMessage)).build();
    SupportCase addedSupportCase = supportCaseToAdd.toBuilder().id(123L).build();
    when(chatMessageRepo.findAllById(anyList())).thenReturn(List.of(newMessage));
    when(supportCaseRepo.save(any())).thenReturn(addedSupportCase);

    SupportCase actual = supportCaseService.createSupportCase(supportCaseToAdd);

    assertThat(actual).isEqualTo(addedSupportCase);
  }

  @Test
  public void createSupportCase_validInput_messagesUpdated() {
    ChatMessage firstMessage = ChatMessage.builder().id(1L).build();
    ChatMessage secondMessage = ChatMessage.builder().id(1L).build();
    SupportCase supportCaseToAdd =
        SupportCase.builder().messages(List.of(firstMessage, secondMessage)).build();
    SupportCase addedSupportCase = supportCaseToAdd.toBuilder().id(123L).build();
    when(chatMessageRepo.findAllById(anyList())).thenReturn(List.of(firstMessage, secondMessage));
    when(supportCaseRepo.save(any())).thenReturn(addedSupportCase);

    supportCaseService.createSupportCase(supportCaseToAdd);

    assertAll(
        () -> assertThat(firstMessage.getSupportCase()).isEqualTo(addedSupportCase),
        () -> assertThat(secondMessage.getSupportCase()).isEqualTo(addedSupportCase));
  }

  @Test
  public void getAll_returnsAllMCases() {
    when(supportCaseRepo.findAll())
        .thenReturn(
            List.of(SupportCase.builder().id(1L).build(), SupportCase.builder().id(2L).build()));

    List<SupportCase> actual = supportCaseService.getAll();

    assertThat(actual)
        .containsExactly(
            SupportCase.builder().id(1L).build(), SupportCase.builder().id(2L).build());
  }

  @Test
  public void patchSupportCase_changedClientReference_changeSaved() {
    SupportCase existingSupportCase =
        SupportCase.builder()
            .id(1L)
            .messages(List.of(ChatMessage.builder().id(100L).build()))
            .clientReference("oldClientReference")
            .build();
    SupportCase modifiedSupportCase =
        SupportCase.builder().id(1L).clientReference("newClientReference").build();
    when(supportCaseRepo.findById(1L)).thenReturn(Optional.of(existingSupportCase));

    Optional<SupportCase> actual = supportCaseService.patchSupportCase(modifiedSupportCase);

    assertThat(actual)
        .map(SupportCase::getClientReference)
        .isPresent()
        .contains("newClientReference");
  }

  @Test
  public void patchSupportCase_caseNotFound_emptyOptional() {
    SupportCase supportCase =
        SupportCase.builder().id(1L).clientReference("newClientReference").build();
    when(supportCaseRepo.findById(1L)).thenReturn(Optional.empty());

    Optional<SupportCase> actual = supportCaseService.patchSupportCase(supportCase);

    assertThat(actual).isEmpty();
  }
}
