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

package com.epam.digital.data.platform.dgtldcmnt.multipart;

import java.io.File;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ByteArrayMultipartFileTest {

  @Test
  void testMultipartFileProperties() {
    final var bytes = new byte[]{1, 2, 3};
    final var contentType = "text/plain";
    final var name = "name";
    final var originalFilename = "textText.txt";

    final var multipartFile = ByteArrayMultipartFile.builder()
        .bytes(bytes)
        .contentType(contentType)
        .name(name)
        .originalFilename(originalFilename)
        .build();

    Assertions.assertThat(multipartFile)
        .hasFieldOrPropertyWithValue("bytes", bytes)
        .hasFieldOrPropertyWithValue("contentType", contentType)
        .hasFieldOrPropertyWithValue("name", name)
        .hasFieldOrPropertyWithValue("originalFilename", originalFilename)
        .hasFieldOrPropertyWithValue("empty", false)
        .hasFieldOrPropertyWithValue("size", 3L);
  }

  @Test
  void testMultipartFileDefaults() {

    final var multipartFile = ByteArrayMultipartFile.builder().build();

    Assertions.assertThat(multipartFile)
        .hasFieldOrPropertyWithValue("name", "file")
        .hasFieldOrPropertyWithValue("empty", true)
        .hasFieldOrPropertyWithValue("size", 0L);
  }

  @Test
  void testMultipartFileInputStream() {
    final var bytes = "Some string".getBytes();

    final var multipartFile = ByteArrayMultipartFile.builder()
        .bytes(bytes)
        .build();

    final var inputStream = multipartFile.getInputStream();

    Assertions.assertThat(inputStream)
        .hasBinaryContent(bytes);
  }

  @Test
  void testTransferToIsUnsupported() {
    final var multipartFile = ByteArrayMultipartFile.builder().build();

    Assertions.assertThatThrownBy(() -> multipartFile.transferTo(new File("someFile")))
        .hasMessage("This method is not used during upload")
        .isInstanceOf(UnsupportedOperationException.class);
  }

}
