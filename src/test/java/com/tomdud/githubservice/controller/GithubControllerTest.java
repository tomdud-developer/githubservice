package com.tomdud.githubservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private WireMockServer wireMockServer;

    private final String CONTROLLER_BASE_URL = "api/v1/github/repositories";
    private final String TEST_USERNAME = "test-username";

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();

        configureFor("localhost", wireMockServer.port());

        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/" + TEST_USERNAME + "/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("mocked-github-response-user-repos-success.json")));

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/users/*/repos"))
                .willReturn(WireMock.aResponse()
                        .withStatus(404)));


        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/" + TEST_USERNAME + "/AstrometryDataCompressionProject/branches"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("mocked-github-response-repo-branch-success.json")));
    }

    @AfterEach
    void afterEach() {
        wireMockServer.stop();
    }

    @Test
    void getUserNotForkedRepositoriesInformation() {
        //given
        String expectedResponseJson;
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("github-controller/expected_response.json")) {
            expectedResponseJson = new String(inputStream.readAllBytes());
        } catch (IOException ioException) {
            throw new RuntimeException("Problem with loading expected json response file");
        }

        //then
        webTestClient
                .get()
                .uri(CONTROLLER_BASE_URL + "/{username}", TEST_USERNAME)
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(expectedResponseJson);
    }

    @Test
    void testGetUserNotForkedRepositoriesInformationErrorBecauseUserNotFound() {
        //then
        webTestClient
                .get()
                .uri(CONTROLLER_BASE_URL + "/{username}", TEST_USERNAME + "_NOT_EXIST")
                .header(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Username with name test-username_NOT_EXIST not found on GitHub");
    }

    @Test
    void testGetUserNotForkedRepositoriesInformationErrorBecauseOfBadAcceptHeader() {
        //then
        webTestClient
                .get()
                .uri(CONTROLLER_BASE_URL + "/" + TEST_USERNAME)
                .header(HttpHeaders.ACCEPT, APPLICATION_XML_VALUE)
                .exchange()
                .expectStatus().isEqualTo(HttpStatusCode.valueOf(406))
                .expectBody()
                .jsonPath("$.status").isEqualTo(406)
                .jsonPath("$.message").isEqualTo("You provided wrong Accept header, there is a acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE);
    }

}