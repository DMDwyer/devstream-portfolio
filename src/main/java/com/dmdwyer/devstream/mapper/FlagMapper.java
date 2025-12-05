package com.dmdwyer.devstream.mapper;

import org.mapstruct.*;

import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.entity.Flag;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FlagMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "enabled", source = "enabled", defaultValue = "false")
  Flag toEntity(FlagDto dto);

  FlagDto toDto(Flag entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  void updateEntity(@MappingTarget Flag target, FlagDto patch);
  
  @AfterMapping
  default void updateEnabledIfNotNull(@MappingTarget Flag target, FlagDto patch) {
    if (patch.enabled() != null) {
      target.setEnabled(patch.enabled());
    }
  }
}
