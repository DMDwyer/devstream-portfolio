package com.dmdwyer.devstream.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.dmdwyer.devstream.AbstractPostgresContainerTest;
import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.repository.FlagRepository;
import com.dmdwyer.devstream.service.FlagService;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@SpringBootTest
@Import(AbstractPostgresContainerTest.class)
@Epic("Feature Flags Management")
@Feature("Flag Service Layer")
public class FlagServiceIT {

    @Autowired
    private FlagService flagService;

    @Autowired
    private FlagRepository flagRepository;

    @BeforeEach
    void cleanup() {
        flagRepository.deleteAll();
    }

    @Test
    @Story("Create flag and verify persistence")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests that flags created through service are properly persisted to PostgreSQL")
    void shouldCreateAndPersistFlag() {
        FlagDto dto = new FlagDto(null, "service-test-flag", true, null, null);
        
        FlagDto created = flagService.create(dto);
        
        assertThat(created.id()).isNotNull();
        assertThat(created.flagKey()).isEqualTo("service-test-flag");
        assertThat(created.enabled()).isTrue();
        
        // Verify persistence
        FlagDto retrieved = flagService.get("service-test-flag").orElseThrow();
        assertThat(retrieved.id()).isEqualTo(created.id());
    }

    @Test
    @Story("Prevent duplicate flag keys")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests that creating a flag with duplicate key throws IllegalArgumentException")
    void shouldPreventDuplicateKeys() {
        FlagDto dto = new FlagDto(null, "duplicate-key", true, null, null);
        flagService.create(dto);
        
        assertThatThrownBy(() -> flagService.create(dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Flag key already exists");
    }

    @Test
    @Story("Update existing flag")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests partial update of flag through service layer")
    void shouldUpdateFlag() {
        FlagDto dto = new FlagDto(null, "update-test", true, null, null);
        flagService.create(dto);
        
        FlagDto patch = new FlagDto(null, null, false, null, null);
        FlagDto updated = flagService.update("update-test", patch);
        
        assertThat(updated.enabled()).isFalse();
        assertThat(updated.flagKey()).isEqualTo("update-test");
    }

    @Test
    @Story("Delete flag")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests deletion of flag through service layer")
    void shouldDeleteFlag() {
        FlagDto dto = new FlagDto(null, "delete-test", true, null, null);
        flagService.create(dto);
        
        flagService.delete("delete-test");
        
        assertThat(flagService.get("delete-test")).isEmpty();
    }

    @Test
    @Story("Evaluate disabled flag")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests that disabled flags always return OFF regardless of rules or variants")
    void shouldReturnOffForDisabledFlag() {
        FlagDto dto = new FlagDto(null, "disabled-flag", false, null, null);
        flagService.create(dto);
        
        String result = flagService.evaluate("disabled-flag", "user123", Map.of());
        
        assertThat(result).isEqualTo("OFF");
    }

    @Test
    @Story("Evaluate flag with rules")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests rule-based evaluation where user attributes match conditions")
    void shouldEvaluateWithRules() {
        String rulesJson = "[{\"if\":\"country=IE\",\"then\":\"variant-ireland\"}]";
        FlagDto dto = new FlagDto(null, "rules-flag", true, rulesJson, null);
        flagService.create(dto);
        
        Map<String, String> attrs = new HashMap<>();
        attrs.put("country", "IE");
        
        String result = flagService.evaluate("rules-flag", "user123", attrs);
        
        assertThat(result).isEqualTo("variant-ireland");
    }

    @Test
    @Story("Evaluate flag with rules no match")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests that when rules don't match, fallback to variant split logic")
    void shouldFallbackToVariantsWhenRulesDoNotMatch() {
        String rulesJson = "[{\"if\":\"country=IE\",\"then\":\"variant-ireland\"}]";
        String variantsJson = "{\"A\":50,\"B\":50}";
        FlagDto dto = new FlagDto(null, "rules-fallback-flag", true, rulesJson, variantsJson);
        flagService.create(dto);
        
        Map<String, String> attrs = new HashMap<>();
        attrs.put("country", "US");
        
        String result = flagService.evaluate("rules-fallback-flag", "user123", attrs);
        
        // Should return either A or B based on consistent hashing
        assertThat(result).isIn("A", "B");
    }

    @Test
    @Story("Evaluate flag with variant splits")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests consistent hashing for variant assignment - same user gets same variant")
    void shouldConsistentlyAssignVariants() {
        String variantsJson = "{\"A\":50,\"B\":50}";
        FlagDto dto = new FlagDto(null, "variants-flag", true, null, variantsJson);
        flagService.create(dto);
        
        String result1 = flagService.evaluate("variants-flag", "user123", Map.of());
        String result2 = flagService.evaluate("variants-flag", "user123", Map.of());
        
        assertThat(result1).isIn("A", "B");
        assertThat(result2).isEqualTo(result1); // Consistent for same user
    }

    @Test
    @Story("Evaluate flag without variants")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests that enabled flag with no variants returns ON")
    void shouldReturnOnWhenNoVariantsConfigured() {
        FlagDto dto = new FlagDto(null, "simple-flag", true, null, null);
        flagService.create(dto);
        
        String result = flagService.evaluate("simple-flag", "user123", Map.of());
        
        assertThat(result).isEqualTo("ON");
    }

    @Test
    @Story("Evaluate non-existent flag")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests that evaluating non-existent flag throws NoSuchElementException")
    void shouldThrowExceptionForNonExistentFlag() {
        assertThatThrownBy(() -> flagService.evaluate("non-existent", "user123", Map.of()))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("Flag not found");
    }

    @Test
    @Story("Evaluate flag with complex rules")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests multiple rule conditions and first-match wins behavior")
    void shouldEvaluateComplexRules() {
        String rulesJson = "[" +
            "{\"if\":\"country=IE\",\"then\":\"ireland\"}," +
            "{\"if\":\"country=US\",\"then\":\"usa\"}," +
            "{\"if\":\"tier=premium\",\"then\":\"premium\"}" +
            "]";
        FlagDto dto = new FlagDto(null, "complex-rules-flag", true, rulesJson, null);
        flagService.create(dto);
        
        Map<String, String> ieAttrs = Map.of("country", "IE");
        Map<String, String> usAttrs = Map.of("country", "US");
        Map<String, String> premiumAttrs = Map.of("tier", "premium");
        
        assertThat(flagService.evaluate("complex-rules-flag", "user1", ieAttrs)).isEqualTo("ireland");
        assertThat(flagService.evaluate("complex-rules-flag", "user2", usAttrs)).isEqualTo("usa");
        assertThat(flagService.evaluate("complex-rules-flag", "user3", premiumAttrs)).isEqualTo("premium");
    }
}
