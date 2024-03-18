package com.foundever.chatapi.mapper;

import com.foundever.chatapi.model.SupportCase;
import com.foundever.chatapi.model.dto.SupportCaseDto;
import org.mapstruct.Mapper;

/** A mapper between {@link SupportCase} and {@link SupportCaseDto} objects. */
@Mapper(componentModel = "spring", uses = ChatMessageMapper.class)
public interface SupportCaseMapper {

  /** Maps {@link SupportCaseDto to {@link SupportCase }. */
  SupportCase toSupportCase(SupportCaseDto supportCaseDto);

  /** Maps {@link SupportCase} to {@link SupportCaseDto}. */
  SupportCaseDto toSupportCaseDto(SupportCase supportCase);
}
