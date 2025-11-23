package com.dmdwyer.devstream.mapper;

import org.mapstruct.*;

import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.entity.Flag;

@Mapper(componentModel = "spring")
public interface FlagMapper {
  Flag toEntity(FlagDto dto);
  FlagDto toDto(Flag entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(@MappingTarget Flag target, FlagDto patch);
}
