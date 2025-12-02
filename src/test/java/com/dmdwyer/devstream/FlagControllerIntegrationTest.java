package com.dmdwyer.devstream;

import com.dmdwyer.devstream.entity.Flag;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Feature Flags Management")
@Feature("Flag Controller API")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlagControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Story("Create and retrieve feature flags")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Integration test that creates a new feature flag via POST and verifies it can be retrieved via GET")
    public void createAndGetFlags() {
        String base = "http://localhost:" + port + "/flags";

        // Use Dto for requests and expect the controller to convert to entity
        com.dmdwyer.devstream.dto.FlagDto f = new com.dmdwyer.devstream.dto.FlagDto(null, "integration-test-flag", true, null, null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<com.dmdwyer.devstream.dto.FlagDto> req = new HttpEntity<>(f, headers);

        ResponseEntity<com.dmdwyer.devstream.dto.FlagDto> createResp = restTemplate.postForEntity(base, req, com.dmdwyer.devstream.dto.FlagDto.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        com.dmdwyer.devstream.dto.FlagDto created = createResp.getBody();
        assertThat(created).isNotNull();
        Long id = java.util.Objects.requireNonNull(created, "created is null").id();
        assertThat(id).isNotNull();

        ResponseEntity<String> listResp = restTemplate.getForEntity(base, String.class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = listResp.getBody();
        assertThat(body).isNotNull();
        assertThat(body).contains("integration-test-flag");
    }
}
