/*
 * Copyright 2026 EPAM Systems.
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

package com.epam.digital.data.platform.dgtldcmnt.decoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.epam.digital.data.platform.starter.errorhandling.exception.ValidationException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DigitalDocumentErrorDecoderTest {

  private DigitalDocumentErrorDecoder decoder;

  @BeforeEach
  void setUp() {
    decoder = new DigitalDocumentErrorDecoder();
  }

  @Test
  void shouldReturnFeignExceptionForNon422Status() {
    Request request = Request.create(
        Request.HttpMethod.GET, "/test", Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
    Response response = Response.builder()
        .status(500)
        .reason("Internal Server Error")
        .headers(Collections.emptyMap())
        .body("error", StandardCharsets.UTF_8)
        .request(request)
        .build();

    Exception result = decoder.decode("methodKey", response);

    assertThat(result).isInstanceOf(FeignException.class);
    assertThat(((FeignException) result).status()).isEqualTo(500);
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenUnableToDecodeErrorMessage() {
    Request request = Request.create(
        Request.HttpMethod.POST, "/test", Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
    String invalidBody = "not a valid json";
    Response response = Response.builder()
        .status(422)
        .reason("Unprocessable Entity")
        .headers(Collections.emptyMap())
        .body(invalidBody, StandardCharsets.UTF_8)
        .request(request)
        .build();

    assertThatThrownBy(() -> decoder.decode("methodKey", response))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Unable to decode error message")
        .hasCauseInstanceOf(IOException.class);
  }

  @Test
  void shouldReturnValidationExceptionFor422Status() {
    Request request = Request.create(
        Request.HttpMethod.POST, "/test", Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
    String validationErrorJson = "{\"message\":\"File size exceeded\","
        + "\"traceId\":\"traceId123\","
        + "\"code\":\"422\","
        + "\"details\":null}";
    Response response = Response.builder()
        .status(422)
        .reason("Unprocessable Entity")
        .headers(Collections.emptyMap())
        .body(validationErrorJson, StandardCharsets.UTF_8)
        .request(request)
        .build();

    Exception result = decoder.decode("methodKey", response);

    assertThat(result).isInstanceOf(ValidationException.class);
    ValidationException validationException = (ValidationException) result;
    assertThat(validationException.getMessage()).isEqualTo("File size exceeded");
    assertThat(validationException.getTraceId()).isEqualTo("traceId123");
    assertThat(validationException.getCode()).isEqualTo("422");
    assertThat(validationException.getDetails()).isNull();
  }
}

