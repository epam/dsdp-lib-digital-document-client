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

import com.epam.digital.data.platform.dgtldcmnt.config.FeignResponseDecoderConfig;
import com.epam.digital.data.platform.dgtldcmnt.config.InternalApiRestClientConfig;
import com.epam.digital.data.platform.dgtldcmnt.config.WireMockConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {WireMockConfig.class, InternalApiRestClientConfig.class,
    FeignResponseDecoderConfig.class})
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class BaseTest {

  @Autowired
  WireMockServer digitalDocumentService;

  @BeforeEach
  void beforeEach() {
    if (!digitalDocumentService.isRunning()) {
      digitalDocumentService.start();
    }
    digitalDocumentService.resetAll();
  }

  @AfterEach
  void afterEach() {
    digitalDocumentService.stop();
  }
}
