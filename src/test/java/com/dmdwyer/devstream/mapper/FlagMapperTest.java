package com.dmdwyer.devstream.mapper;

import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.entity.Flag;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Feature Flags Management")
@Feature("Flag Mapper")
public class FlagMapperTest {
  private final FlagMapper mapper = Mappers.getMapper(FlagMapper.class);

  @Test
  @Story("Partial entity updates")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Verify that null values in DTO patch do not overwrite existing entity values")
  public void updateEntity_withNullEnabled_doesNotOverwrite() {
    Flag target = new Flag();
    target.setFlagKey("f");
    target.setEnabled(true);

    FlagDto patch = new FlagDto(null, null, null, null, null);
    mapper.updateEntity(target, patch);
    assertThat(target.isEnabled()).isTrue();
  }

  @Test
  @Story("Partial entity updates")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Verify that explicit false values in DTO patch do overwrite existing entity values")
  public void updateEntity_withEnabledFalse_overwrites() {
    Flag target = new Flag();
    target.setEnabled(true);

    FlagDto patch = new FlagDto(null, null, Boolean.FALSE, null, null);
    mapper.updateEntity(target, patch);
    assertThat(target.isEnabled()).isFalse();
  }

  @Test
  @Story("DTO to Entity mapping")
  @Severity(SeverityLevel.NORMAL)
  @Description("Verify that enabled field defaults to false when not specified in DTO")
  public void toEntity_defaultEnabledIsFalse_whenDtoNull() {
    FlagDto dto = new FlagDto(null, "k", null, null, null);
    Flag entity = mapper.toEntity(dto);
    assertThat(entity.isEnabled()).isFalse();
  }
  
  @Test
  @Story("MapStruct code generation")
  @Severity(SeverityLevel.BLOCKER)
  @Description("Verify that MapStruct annotation processor has generated the FlagMapperImpl class")
  public void generatedImplementationShouldBePresent() {
    // Fail when no MapStruct generated implementation exists on the classpath
    assertDoesNotThrow(() -> Class.forName("com.dmdwyer.devstream.mapper.FlagMapperImpl"));
  }

}
