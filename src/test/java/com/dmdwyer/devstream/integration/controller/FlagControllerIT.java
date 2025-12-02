package com.dmdwyer.devstream.integration.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.dmdwyer.devstream.AbstractPostgresContainerTest;
import com.dmdwyer.devstream.dto.FlagDto;
import com.dmdwyer.devstream.repository.FlagRepository;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

@Epic("Feature Flags Management")
@Feature("Flag Controller API")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AbstractPostgresContainerTest.class)
public class FlagControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FlagRepository flagRepository;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port + "/flags";
        flagRepository.deleteAll();
    }

    @Test
    @Story("Create and retrieve feature flags")
    @Severity(SeverityLevel.CRITICAL)
    @Description("End-to-end test that creates a new feature flag via POST and verifies it can be retrieved via GET with PostgreSQL")
    void shouldCreateAndRetrieveFlag() {
        FlagDto dto = new FlagDto(null, "e2e-test-flag", true, null, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FlagDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<FlagDto> createResp = restTemplate.postForEntity(baseUrl, request, FlagDto.class);
        
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        FlagDto created = createResp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        assertThat(created.flagKey()).isEqualTo("e2e-test-flag");
        assertThat(created.enabled()).isTrue();

        ResponseEntity<FlagDto> getResp = restTemplate.getForEntity(baseUrl + "/e2e-test-flag", FlagDto.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody()).isNotNull();
        assertThat(getResp.getBody().id()).isEqualTo(created.id());
    }

    @Test
    @Story("List all flags with pagination")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests paginated listing of flags through REST API")
    void shouldListFlagsWithPagination() {
        // Create multiple flags
        for (int i = 0; i < 5; i++) {
            FlagDto dto = new FlagDto(null, "flag-" + i, true, null, null);
            HttpEntity<FlagDto> request = new HttpEntity<>(dto);
            restTemplate.postForEntity(baseUrl, request, FlagDto.class);
        }

        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "?page=0&size=3", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).contains("flag-0");
    }

    @Test
    @Story("Update flag via PATCH")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests partial update of flag through REST API")
    void shouldUpdateFlagViaPatch() {
        FlagDto dto = new FlagDto(null, "update-flag", true, null, null);
        restTemplate.postForEntity(baseUrl, new HttpEntity<>(dto), FlagDto.class);

        FlagDto patch = new FlagDto(null, null, false, null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<FlagDto> patchRequest = new HttpEntity<>(patch, headers);

        ResponseEntity<FlagDto> patchResp = restTemplate.exchange(
            baseUrl + "/update-flag",
            HttpMethod.PATCH,
            patchRequest,
            FlagDto.class
        );

        assertThat(patchResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(patchResp.getBody()).isNotNull();
        assertThat(patchResp.getBody().enabled()).isFalse();
        assertThat(patchResp.getBody().flagKey()).isEqualTo("update-flag");
    }

    @Test
    @Story("Delete flag via DELETE")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests deletion of flag through REST API")
    void shouldDeleteFlag() {
        FlagDto dto = new FlagDto(null, "delete-flag", true, null, null);
        restTemplate.postForEntity(baseUrl, new HttpEntity<>(dto), FlagDto.class);

        ResponseEntity<Void> deleteResp = restTemplate.exchange(
            baseUrl + "/delete-flag",
            HttpMethod.DELETE,
            null,
            Void.class
        );

        assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<FlagDto> getResp = restTemplate.getForEntity(baseUrl + "/delete-flag", FlagDto.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Story("Get non-existent flag returns 404")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests that requesting a non-existent flag returns 404 NOT FOUND")
    void shouldReturn404ForNonExistentFlag() {
        ResponseEntity<FlagDto> response = restTemplate.getForEntity(baseUrl + "/non-existent", FlagDto.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Story("Evaluate flag for user")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests flag evaluation endpoint with user attributes")
    void shouldEvaluateFlagForUser() {
        String rulesJson = "[{\"if\":\"country=IE\",\"then\":\"ireland-variant\"}]";
        FlagDto dto = new FlagDto(null, "eval-flag", true, rulesJson, null);
        restTemplate.postForEntity(baseUrl, new HttpEntity<>(dto), FlagDto.class);

        ResponseEntity<String> evalResp = restTemplate.getForEntity(
            baseUrl + "/eval-flag/evaluate?userId=user123&country=IE",
            String.class
        );

        assertThat(evalResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(evalResp.getBody()).isEqualTo("ireland-variant");
    }

    @Test
    @Story("Create duplicate flag returns error")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests that creating a flag with duplicate key returns 400 BAD REQUEST")
    void shouldRejectDuplicateFlag() {
        FlagDto dto = new FlagDto(null, "duplicate", true, null, null);
        restTemplate.postForEntity(baseUrl, new HttpEntity<>(dto), FlagDto.class);

        ResponseEntity<String> duplicateResp = restTemplate.postForEntity(
            baseUrl,
            new HttpEntity<>(dto),
            String.class
        );

        assertThat(duplicateResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Story("Create flag with variants")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests creating and evaluating flag with variant splits")
    void shouldCreateFlagWithVariants() {
        String variantsJson = "{\"A\":50,\"B\":50}";
        FlagDto dto = new FlagDto(null, "variants-flag", true, null, variantsJson);

        ResponseEntity<FlagDto> createResp = restTemplate.postForEntity(
            baseUrl,
            new HttpEntity<>(dto),
            FlagDto.class
        );

        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResp.getBody()).isNotNull();
        assertThat(createResp.getBody().variantsJson()).isEqualTo(variantsJson);

        ResponseEntity<String> evalResp = restTemplate.getForEntity(
            baseUrl + "/variants-flag/evaluate?userId=user456",
            String.class
        );

        assertThat(evalResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(evalResp.getBody()).isIn("A", "B");
    }
}
