package com.dmdwyer.devstream.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "flags", indexes = @Index(name = "ux_flag_key", columnList = "flagKey", unique = true))
public class Flag {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String flagKey;          // e.g. "homepage_banner"

  @Column(nullable = false)
  private boolean enabled = false; // global kill-switch

  @Column(columnDefinition = "TEXT")
  private String variantsJson;     // e.g. {"A":50,"B":50}

  @Column(columnDefinition = "TEXT")
  private String rulesJson;        // e.g. [{"if":"country=IE","then":"A"}]

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  // getters/setters
  public Long getId() { return id; }
  public String getFlagKey() { return flagKey; }
  public void setFlagKey(String flagKey) { this.flagKey = flagKey; }
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
  public String getVariantsJson() { return variantsJson; }
  public void setVariantsJson(String variantsJson) { this.variantsJson = variantsJson; }
  public String getRulesJson() { return rulesJson; }
  public void setRulesJson(String rulesJson) { this.rulesJson = rulesJson; }
  public Instant getCreatedAt() { return createdAt; }
}
