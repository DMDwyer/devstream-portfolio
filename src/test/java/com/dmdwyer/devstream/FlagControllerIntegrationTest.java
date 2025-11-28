package com.dmdwyer.devstream;

import com.dmdwyer.devstream.entity.Flag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlagControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createAndGetFlags() {
        String base = "http://localhost:" + port + "/flags";

        Flag f = new Flag();
        f.setFlagKey("integration-test-flag");
        f.setEnabled(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Flag> req = new HttpEntity<>(f, headers);

        ResponseEntity<Flag> createResp = restTemplate.postForEntity(base, req, Flag.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Flag created = createResp.getBody();
        assertThat(created).isNotNull();
        Long id = java.util.Objects.requireNonNull(created, "created is null").getId();
        assertThat(id).isNotNull();

        ResponseEntity<Flag[]> listResp = restTemplate.getForEntity(base, Flag[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Flag[] list = listResp.getBody();
        assertThat(list).isNotNull();
        assertThat(Arrays.stream(list).anyMatch(x -> "integration-test-flag".equals(x.getFlagKey()))).isTrue();
    }
}
