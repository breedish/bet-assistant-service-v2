package com.breedish.bet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author zenind
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BetApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BetApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private JacksonTester jacksonTester;

    @Before
    public void init() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void validJsonIsSavedAndRetrieved() throws Exception {
        String statsBody = Resources.toString(
            BetApplicationIntegrationTest.class.getResource("/stats.json"),
            Charset.defaultCharset()
        );

        final String file = UUID.randomUUID().toString();
        final String path = fileEndpointPath(file);

        ResponseEntity entity = restTemplate.postForEntity(
            path, new HttpEntity<>(statsBody, headers()), ResponseEntity.class);
        assertEquals(CREATED.value(), entity.getStatusCodeValue());

        ResponseEntity<String> response = restTemplate.exchange(path, GET, new HttpEntity(headers()), String.class);
        assertEquals(OK.value(), response.getStatusCodeValue());
        assertEquals(statsBody, response.getBody());
    }

    @Test
    public void invalidJsonIsRejected() {
        final String file = UUID.randomUUID().toString();
        final String path = fileEndpointPath(file);

        ResponseEntity entity = restTemplate.postForEntity(
            path, new HttpEntity<>("{name:1 description:'title'}", headers()), ResponseEntity.class
        );
        assertEquals(BAD_REQUEST.value(), entity.getStatusCodeValue());
    }

    @Test
    public void getMissingFile() {
        final String file = UUID.randomUUID().toString();
        final String path = fileEndpointPath(file);

        ResponseEntity<String> response = restTemplate.exchange(path, GET, new HttpEntity(headers()), String.class);
        assertEquals(NOT_FOUND.value(), response.getStatusCodeValue());
    }

    private String fileEndpointPath(String file) {
        return "/api/v2/files/test" + file;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-bet-version", "v2");
        headers.set("Content-Type", "application/json");
        return headers;
    }

}
