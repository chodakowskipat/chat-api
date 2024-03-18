package com.foundever.chatapi.repository;

import com.foundever.chatapi.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository managing {@link ChatMessage} entities. */
public interface ChatMessageRepo extends JpaRepository<ChatMessage, Long> {}
