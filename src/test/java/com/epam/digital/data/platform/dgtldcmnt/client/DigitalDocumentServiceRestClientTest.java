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
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.epam.digital.data.platform.dgtldcmnt.config.RestClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = DigitalDocumentServiceRestClient.class)
@ContextConfiguration(classes = {RestClientConfig.class})
class DigitalDocumentServiceRestClientTest extends BaseTest {

  @Autowired
  DigitalDocumentServiceRestClient restClient;

  @Test
  void shouldSendDeleteRequest() {
    var processInstanceId = "processInstanceId";
    digitalDocumentService
        .stubFor(delete(urlEqualTo(String.format("/documents/%s", processInstanceId)))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)));

    restClient.delete(processInstanceId, new HttpHeaders());

    digitalDocumentService.verify(1,
        deleteRequestedFor(urlEqualTo(String.format("/documents/%s", processInstanceId))));
  }
}
