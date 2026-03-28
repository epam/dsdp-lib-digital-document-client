/*
 * Copyright 2023 EPAM Systems.
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

import static com.github.tomakehurst.wiremock.client.WireMock.aMultipart;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.epam.digital.data.platform.dgtldcmnt.config.InternalApiRestClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = DigitalDocumentServiceInternalApiRestClient.class)
@ContextConfiguration(classes = {InternalApiRestClientConfig.class})
class DigitalDocumentServiceInternalApiV2RestClientTest extends BaseTest {

  @Autowired
  DigitalDocumentServiceInternalApiV2RestClient restClient;

  @Test
  void shouldSendPutRequestToInternalApiUrl_multipartFormData() {
    var processInstanceId = "processInstanceId";
    var fileName = "fileName";
    var fileContent = "fileContent".getBytes();
    digitalDocumentService.stubFor(
        post(urlPathEqualTo(String.format("/internal-api/v2/documents/%s", processInstanceId)))
            .withMultipartRequestBody(aMultipart(fileName).withBody(binaryEqualTo(fileContent)))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody(
                    "{\"checksum\":\"someChecksum\"}"
                )));

    var mockMultipartFile = new MockMultipartFile(fileName, fileContent);
    var result = restClient.upload(processInstanceId, fileName, mockMultipartFile,
        new HttpHeaders());

    digitalDocumentService.verify(postRequestedFor(
        urlPathEqualTo(String.format("/internal-api/v2/documents/%s", processInstanceId))));

    assertThat(result.getChecksum()).isEqualTo(
        "someChecksum");
  }
}