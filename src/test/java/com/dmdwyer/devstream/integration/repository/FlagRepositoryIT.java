package com.dmdwyer.devstream.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.dmdwyer.devstream.AbstractPostgresContainerTest;
import com.dmdwyer.devstream.entity.Flag;
import com.dmdwyer.devstream.repository.FlagRepository;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@SpringBootTest
@Import(AbstractPostgresContainerTest.class)
@Epic("Feature Flags Management")
@Feature("Flag Repository Layer")
public class FlagRepositoryIT {

    @Autowired
    private FlagRepository flagRepository;

    @BeforeEach
    void cleanup() {
        flagRepository.deleteAll();
    }

    @Test
    @Story("Persist and retrieve feature flag")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Tests basic CRUD operations - save and findByFlagKey with PostgreSQL")
    void shouldPersistAndLoadFeatureFlag() {
        Flag flag = new Flag();
        flag.setFlagKey("test-flag");
        flag.setEnabled(true);

        flagRepository.save(flag);

        Optional<Flag> loaded = flagRepository.findByFlagKey("test-flag");
        assertThat(loaded).isPresent();
        assertThat(loaded.get().isEnabled()).isTrue();
        assertThat(loaded.get().getFlagKey()).isEqualTo("test-flag");
    }

    @Test
    @Story("Find by non-existent key")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests that findByFlagKey returns empty Optional for non-existent keys")
    void shouldReturnEmptyForNonExistentKey() {
        Optional<Flag> result = flagRepository.findByFlagKey("non-existent");
        
        assertThat(result).isEmpty();
    }

    @Test
    @Story("Check flag key exists")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests existsByFlagKey for both existing and non-existing keys")
    void shouldCheckIfFlagKeyExists() {
        Flag flag = new Flag();
        flag.setFlagKey("exists-test");
        flag.setEnabled(true);
        flagRepository.save(flag);

        assertThat(flagRepository.existsByFlagKey("exists-test")).isTrue();
        assertThat(flagRepository.existsByFlagKey("does-not-exist")).isFalse();
    }

    @Test
    @Story("Paginated flag retrieval")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests pagination support for listing flags")
    void shouldSupportPagination() {
        // Create 10 flags
        for (int i = 0; i < 10; i++) {
            Flag flag = new Flag();
            flag.setFlagKey("flag-" + i);
            flag.setEnabled(i % 2 == 0);
            flagRepository.save(flag);
        }

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Flag> page = flagRepository.findAll(pageRequest);

        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.isFirst()).isTrue();
    }

    @Test
    @Story("Sorted flag retrieval")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests sorting support when retrieving flags")
    void shouldSupportSorting() {
        Flag flag1 = new Flag();
        flag1.setFlagKey("zebra");
        flag1.setEnabled(true);
        
        Flag flag2 = new Flag();
        flag2.setFlagKey("alpha");
        flag2.setEnabled(true);
        
        Flag flag3 = new Flag();
        flag3.setFlagKey("beta");
        flag3.setEnabled(true);

        flagRepository.saveAll(List.of(flag1, flag2, flag3));

        PageRequest sortedRequest = PageRequest.of(0, 10, Sort.by("flagKey").ascending());
        Page<Flag> sortedPage = flagRepository.findAll(sortedRequest);

        assertThat(sortedPage.getContent()).hasSize(3);
        assertThat(sortedPage.getContent().get(0).getFlagKey()).isEqualTo("alpha");
        assertThat(sortedPage.getContent().get(1).getFlagKey()).isEqualTo("beta");
        assertThat(sortedPage.getContent().get(2).getFlagKey()).isEqualTo("zebra");
    }

    @Test
    @Story("Update existing flag")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests updating an existing flag's properties")
    void shouldUpdateExistingFlag() {
        Flag flag = new Flag();
        flag.setFlagKey("update-test");
        flag.setEnabled(true);
        Flag saved = flagRepository.save(flag);

        saved.setEnabled(false);
        saved.setRulesJson("[{\"test\":\"rule\"}]");
        flagRepository.save(saved);

        Flag updated = flagRepository.findByFlagKey("update-test").orElseThrow();
        assertThat(updated.isEnabled()).isFalse();
        assertThat(updated.getRulesJson()).isEqualTo("[{\"test\":\"rule\"}]");
    }

    @Test
    @Story("Delete flag by entity")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests deletion of flag entity from repository")
    void shouldDeleteFlag() {
        Flag flag = new Flag();
        flag.setFlagKey("delete-test");
        flag.setEnabled(true);
        Flag saved = flagRepository.save(flag);

        flagRepository.delete(saved);

        assertThat(flagRepository.findByFlagKey("delete-test")).isEmpty();
        assertThat(flagRepository.count()).isEqualTo(0);
    }

    @Test
    @Story("Persist flag with complex JSON")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests persisting and retrieving flags with rulesJson and variantsJson")
    void shouldPersistFlagWithComplexJson() {
        Flag flag = new Flag();
        flag.setFlagKey("complex-flag");
        flag.setEnabled(true);
        flag.setRulesJson("[{\"if\":\"country=IE\",\"then\":\"variant-a\"}]");
        flag.setVariantsJson("{\"A\":50,\"B\":50}");
        
        flagRepository.save(flag);

        Flag loaded = flagRepository.findByFlagKey("complex-flag").orElseThrow();
        assertThat(loaded.getRulesJson()).contains("country=IE");
        assertThat(loaded.getVariantsJson()).contains("\"A\":50");
    }

    @Test
    @Story("Count all flags")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests counting total number of flags in repository")
    void shouldCountFlags() {
        assertThat(flagRepository.count()).isEqualTo(0);

        for (int i = 0; i < 3; i++) {
            Flag flag = new Flag();
            flag.setFlagKey("count-flag-" + i);
            flag.setEnabled(true);
            flagRepository.save(flag);
        }

        assertThat(flagRepository.count()).isEqualTo(3);
    }

    @Test
    @Story("Save multiple flags at once")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests batch saving of multiple flags using saveAll")
    void shouldSaveMultipleFlags() {
        Flag flag1 = new Flag();
        flag1.setFlagKey("batch-1");
        flag1.setEnabled(true);
        
        Flag flag2 = new Flag();
        flag2.setFlagKey("batch-2");
        flag2.setEnabled(false);
        
        Flag flag3 = new Flag();
        flag3.setFlagKey("batch-3");
        flag3.setEnabled(true);

        List<Flag> saved = flagRepository.saveAll(List.of(flag1, flag2, flag3));

        assertThat(saved).hasSize(3);
        assertThat(flagRepository.count()).isEqualTo(3);
    }
}

