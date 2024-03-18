package com.foundever.chatapi.repository;

import com.foundever.chatapi.model.SupportCase;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository managing {@link SupportCase} entities. */
public interface SupportCaseRepo extends JpaRepository<SupportCase, Long> {}
