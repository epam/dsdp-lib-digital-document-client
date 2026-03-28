/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.dgtldcmnt.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.digital.data.platform.dgtldcmnt.config.InternalApiRestClientConfig;
import com.epam.digital.data.platform.dgtldcmnt.dto.RemoteDocumentDto;
import com.epam.digital.data.platform.starter.errorhandling.exception.ValidationException;
import java.io.IOException;
import java.net.URL;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = DigitalDocumentServiceInternalApiRestClient.class)
@ContextConfiguration(classes = {InternalApiRestClientConfig.class})
class DigitalDocumentServiceInternalApiRestClientTest extends BaseTest {

  @Autowired
  DigitalDocumentServiceInternalApiRestClient restClient;

  @Test
  void shouldSendPutRequestToInternalApiUrl() throws IOException {
    var processInstanceId = "processInstanceId";
    digitalDocumentService
        .stubFor(post(urlEqualTo(String.format("/internal-api/documents/%s", processInstanceId)))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody(
                    "{\"checksum\":\"5f1faf30062b7ebe4e9e60c9a6e3ea05f21a779f1122f2e6121946e5f328b087\"}"
                )));

    RemoteDocumentDto documentDto = RemoteDocumentDto.builder()
        .remoteFileLocation(new URL(
            "https://www.google.com.ua/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"))
        .filename("filename")
        .build();

    var result = restClient.upload(processInstanceId, documentDto, new HttpHeaders());

    digitalDocumentService.verify(1,
        postRequestedFor(
            urlEqualTo(String.format("/internal-api/documents/%s", processInstanceId))));

    assertThat(result.getChecksum()).isEqualTo(
        "5f1faf30062b7ebe4e9e60c9a6e3ea05f21a779f1122f2e6121946e5f328b087");
  }

  @Test
  @SneakyThrows
  void shouldDownloadDocument() {
    var processInstanceId = "processInstanceId";
    var id = "id";
    var data = new byte[]{1, 2, 3};

    digitalDocumentService
        .stubFor(
            get(urlEqualTo(String.format("/internal-api/documents/%s/%s", processInstanceId, id)))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "image/png")
                    .withStatus(200)
                    .withBody(data)));

    var result = restClient.download(processInstanceId, id, new HttpHeaders());
    var byteArray = result.getBody().getInputStream().readAllBytes();
    assertThat(byteArray).isEqualTo(data);
  }

  @Test
  void shouldGetDocumentMetadata() {
    var processInstanceId = "processInstanceId";
    var id = "documentId";

    digitalDocumentService
        .stubFor(
            get(urlEqualTo(
                String.format("/internal-api/documents/%s/%s/metadata", processInstanceId, id)))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(
                        "{\"checksum\":\"someCheckSum\",\"name\":\"document.csv\",\"id\":\"documentId\"}"
                    )));

    var result = restClient.getMetadata(processInstanceId, id, new HttpHeaders());

    assertThat(result).hasFieldOrPropertyWithValue("checksum", "someCheckSum")
        .hasFieldOrPropertyWithValue("name", "document.csv")
        .hasFieldOrPropertyWithValue("id", id);
  }

  @Test
  void shouldHandleValidationException() throws IOException {
    var processInstanceId = "processInstanceId";
    digitalDocumentService
        .stubFor(post(urlEqualTo(String.format("/internal-api/documents/%s", processInstanceId)))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(422)
                .withBody(
                    "{\"message\":\"File size exceeded 1 MB\",\n"
                        + "\"traceId\": \"traceId\",\n"
                        + "\"code\": \"422\",\n"
                        + "\"details\": null}"
                )));

    RemoteDocumentDto documentDto = RemoteDocumentDto.builder()
        .remoteFileLocation(new URL(
            "https://www.google.com.ua/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"))
        .filename("filename")
        .build();

    var exception = assertThrows(
        ValidationException.class, () -> restClient.upload(processInstanceId, documentDto, new HttpHeaders()));

    assertEquals("File size exceeded 1 MB", exception.getMessage());
    assertEquals("traceId", exception.getTraceId());
    assertEquals("422", exception.getCode());
    assertNull(exception.getDetails());
  }
}