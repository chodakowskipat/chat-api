package com.foundever.chatapi.controller;

import com.foundever.chatapi.mapper.SupportCaseMapper;
import com.foundever.chatapi.model.dto.SupportCaseDto;
import com.foundever.chatapi.service.SupportCaseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/support/cases")
public class SupportCaseController {

  private final SupportCaseService supportCaseService;
  private final SupportCaseMapper supportCaseMapper;

  @GetMapping
  List<SupportCaseDto> getAllSupportCases() {
    log.atTrace().log("Received getAllSupportCases request.");
    return supportCaseService.getAll().stream().map(supportCaseMapper::toSupportCaseDto).toList();
  }

  @PostMapping
  SupportCaseDto createSupportCase(@RequestBody SupportCaseDto supportCaseDto) {
    log.atTrace().log("Received createSupportCase request: {}", supportCaseDto);
    var supportCase =
        supportCaseService.createSupportCase(supportCaseMapper.toSupportCase(supportCaseDto));
    log.atTrace().log("Saved new support case: {}", supportCase);
    return supportCaseMapper.toSupportCaseDto(supportCase);
  }

  @PatchMapping("/{id}")
  SupportCaseDto updateSupportCase(
      @PathVariable Long id, @RequestBody SupportCaseDto supportCaseDto) {
    log.atTrace().log("Received updateSupportCase request: {} for case id: {}", supportCaseDto, id);
    var supportCaseToUpdate = supportCaseMapper.toSupportCase(supportCaseDto);
    supportCaseToUpdate.setId(id);

    return supportCaseService
        .patchSupportCase(supportCaseToUpdate)
        .map(supportCaseMapper::toSupportCaseDto)
        .map(
            supportCase -> {
              log.atTrace().log("Updated supportCase: {}", supportCase);
              return supportCase;
            })
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Support case not found"));
  }
}
