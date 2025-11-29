package com.dmdwyer.devstream.mapper;

import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.entity.Flag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class FlagMapperTest {
  private final FlagMapper mapper = Mappers.getMapper(FlagMapper.class);

  @Test
  public void updateEntity_withNullEnabled_doesNotOverwrite() {
    Flag target = new Flag();
    target.setFlagKey("f");
    target.setEnabled(true);

    FlagDto patch = new FlagDto(null, null, null, null, null);
    mapper.updateEntity(target, patch);
    assertThat(target.isEnabled()).isTrue();
  }

  @Test
  public void updateEntity_withEnabledFalse_overwrites() {
    Flag target = new Flag();
    target.setEnabled(true);

    FlagDto patch = new FlagDto(null, null, Boolean.FALSE, null, null);
    mapper.updateEntity(target, patch);
    assertThat(target.isEnabled()).isFalse();
  }

  @Test
  public void toEntity_defaultEnabledIsFalse_whenDtoNull() {
    FlagDto dto = new FlagDto(null, "k", null, null, null);
    Flag entity = mapper.toEntity(dto);
    assertThat(entity.isEnabled()).isFalse();
  }
  
  @Test
  public void generatedImplementationShouldBePresent() {
    // Fail when no MapStruct generated implementation exists on the classpath
    assertDoesNotThrow(() -> Class.forName("com.dmdwyer.devstream.mapper.FlagMapperImpl"));
  }

}
