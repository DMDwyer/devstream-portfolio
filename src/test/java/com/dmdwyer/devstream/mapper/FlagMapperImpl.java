package com.dmdwyer.devstream.mapper;

import org.springframework.stereotype.Component;

import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.entity.Flag;

@Component
public class FlagMapperImpl implements FlagMapper {
  @Override
  public Flag toEntity(FlagDto dto) {
    if (dto == null) return null;
    Flag flag = new Flag();
    if (dto.enabled() != null) {
      flag.setEnabled(dto.enabled());
    } else {
      flag.setEnabled(false);
    }
    flag.setFlagKey(dto.flagKey());
    flag.setVariantsJson(dto.variantsJson());
    flag.setRulesJson(dto.rulesJson());
    return flag;
  }

  @Override
  public FlagDto toDto(Flag entity) {
    if (entity == null) return null;
    return new FlagDto(entity.getId(), entity.getFlagKey(), entity.isEnabled(), entity.getVariantsJson(), entity.getRulesJson());
  }

  @Override
  public void updateEntity(Flag target, FlagDto patch) {
    if (patch == null || target == null) return;
    if (patch.flagKey() != null) target.setFlagKey(patch.flagKey());
    if (patch.enabled() != null) target.setEnabled(patch.enabled());
    if (patch.variantsJson() != null) target.setVariantsJson(patch.variantsJson());
    if (patch.rulesJson() != null) target.setRulesJson(patch.rulesJson());
  }
}
