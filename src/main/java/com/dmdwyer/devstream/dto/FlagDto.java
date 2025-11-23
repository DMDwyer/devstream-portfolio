package com.dmdwyer.devstream.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FlagDto(
  Long id,
  @NotBlank @Pattern(regexp = "^[a-z0-9_\\-\\.]+$") String flagKey,
  Boolean enabled,
  @Size(max = 20000) String variantsJson,
  @Size(max = 20000) String rulesJson
) {}
