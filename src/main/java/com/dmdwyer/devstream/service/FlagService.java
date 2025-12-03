package com.dmdwyer.devstream.service;

import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.entity.Flag;
import com.dmdwyer.devstream.mapper.FlagMapper;
import com.dmdwyer.devstream.repository.FlagRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Transactional
public class FlagService {
  private final FlagRepository repo;
  private final FlagMapper mapper;
  private final ObjectMapper om;
  private final Logger logger = LoggerFactory.getLogger(FlagService.class);
  private final Counter flagEvaluationCounter;

  public FlagService(FlagRepository repo, FlagMapper mapper, ObjectMapper om, MeterRegistry meterRegistry) {
    this.repo = repo; this.mapper = mapper; this.om = om;
    this.flagEvaluationCounter = Counter.builder("flag.evaluations")
        .description("Number of flag evaluations")
        .register(meterRegistry);
  }

  // CRUD
  public FlagDto create(FlagDto dto) {
    if (repo.existsByFlagKey(dto.flagKey())) {
      throw new IllegalArgumentException("Flag key already exists: " + dto.flagKey());
    }
    Flag f = mapper.toEntity(dto);
    return mapper.toDto(repo.save(f));
  }

  public Page<FlagDto> list(Pageable pageable) {
    return repo.findAll(pageable).map(mapper::toDto);
  }

  public Optional<FlagDto> get(String key) {
    flagEvaluationCounter.increment();
    return repo.findByFlagKey(key).map(mapper::toDto);
  }

  public FlagDto update(String key, FlagDto patch) {
    Flag f = repo.findByFlagKey(key).orElseThrow(() -> new NoSuchElementException("Flag not found: " + key));
    mapper.updateEntity(f, patch);
    return mapper.toDto(f);
  }

  public void delete(String key) {
    repo.findByFlagKey(key).ifPresent(repo::delete);
  }

  // Evaluation
  public String evaluate(String key, String userId, Map<String,String> attrs) {
    Flag f = repo.findByFlagKey(key).orElseThrow(() -> new NoSuchElementException("Flag not found: " + key));
    if (!f.isEnabled()) return "OFF";

    // 1) Rules (very simple demo parser: "field=value" → return variant)
    if (f.getRulesJson() != null && !f.getRulesJson().isBlank()) {
      try {
        for (JsonNode rule : om.readTree(f.getRulesJson())) {
          JsonNode ifNode = rule.get("if");
          JsonNode thenNode = rule.get("then");
          String cond = ifNode != null ? ifNode.asText("") : ""; // e.g. country=IE
          String then = thenNode != null ? thenNode.asText("") : ""; // e.g. A
          String[] parts = cond.split("=", 2);
          if (parts.length == 2) {
            String field = parts[0].trim();
            String expected = parts[1].trim();
            if (expected.equalsIgnoreCase(attrs.getOrDefault(field, ""))) {
              return then;
            }
          }
        }
      } catch (Exception e) {
        logger.debug("Failed to parse rules JSON for flag {}: {}", key, e.getMessage());
      }
    }

    // 2) Variant split via consistent hashing
    Map<String,Integer> splits = parseSplits(f.getVariantsJson()); // {"A":50,"B":50}
    if (splits.isEmpty()) return "ON"; // fallback when no variants configured

    int bucket = Math.floorMod(positiveHash(userId), 100);
    int cumulative = 0;
    for (Map.Entry<String,Integer> e : splits.entrySet()) {
      cumulative += e.getValue();
      if (bucket < cumulative) return e.getKey();
    }
    return splits.keySet().iterator().next(); // safety fallback
  }

  private Map<String,Integer> parseSplits(String json) {
    Map<String,Integer> map = new LinkedHashMap<>();
    if (json == null || json.isBlank()) return map;
    try {
      JsonNode n = om.readTree(json);
      n.fieldNames().forEachRemaining(k -> map.put(k, n.get(k).asInt()));
    } catch (Exception ignored) {}
    return map;
  }

  private static int positiveHash(String s) {
    if (s == null) s = "";
    int h = Arrays.hashCode(s.getBytes(StandardCharsets.UTF_8));
    if (h == Integer.MIN_VALUE) h = 0; // avoid Math.abs overflow
    return Math.abs(h);
  }
}
