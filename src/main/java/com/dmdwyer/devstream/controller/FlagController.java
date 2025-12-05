package com.dmdwyer.devstream.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.service.FlagService;
import com.dmdwyer.devstream.service.FlagEvaluationService;

import java.util.Map;

@RestController
@RequestMapping("/flags")
public class FlagController {
  private final FlagService service;
  private final FlagEvaluationService flagEvaluationService;

  public FlagController(FlagService service, FlagEvaluationService flagEvaluationService) { 
    this.service = service;
    this.flagEvaluationService = flagEvaluationService;
  }

  @PostMapping
  public ResponseEntity<FlagDto> create(@Valid @RequestBody FlagDto dto) {
    return ResponseEntity.ok(service.create(dto));
  }

  @GetMapping
  public Page<FlagDto> list(Pageable pageable) { return service.list(pageable); }

  @GetMapping("/{key}")
  public ResponseEntity<FlagDto> get(@PathVariable String key) {
    return service.get(key).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{key}")
  public ResponseEntity<FlagDto> update(@PathVariable String key, @RequestBody FlagDto patch) {
    return ResponseEntity.ok(service.update(key, patch));
  }

  @DeleteMapping("/{key}")
  public ResponseEntity<Void> delete(@PathVariable String key) {
    service.delete(key);
    return ResponseEntity.noContent().build();
  }

  // Evaluate: /flags/{key}/evaluate?userId=123&country=IE&plan=premium
  @GetMapping("/{key}/evaluate")
  public ResponseEntity<Map<String,String>> evaluate(
      @PathVariable String key,
      @RequestParam String userId,
      @RequestParam Map<String,String> attrs // includes any extra query params
  ) {
    attrs.remove("userId");
    String variant = service.evaluate(key, userId, attrs);
    return ResponseEntity.ok(Map.of("key", key, "userId", userId, "variant", variant));
  }

  @GetMapping("/flag-status")
  public String getFlagStatus(@RequestParam String flagKey, @RequestParam boolean enabled) {
    return flagEvaluationService.evaluateFlag(flagKey, enabled);
  }
}
