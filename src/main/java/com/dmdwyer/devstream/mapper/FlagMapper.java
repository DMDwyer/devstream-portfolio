package com.dmdwyer.devstream.mapper;

import org.mapstruct.*;

import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.entity.Flag;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FlagMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "enabled", defaultValue = "false")
  @Mapping(target = "createdAt", ignore = true)
  Flag toEntity(FlagDto dto);

  FlagDto toDto(Flag entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  void updateEntity(@MappingTarget Flag target, FlagDto patch);
}
