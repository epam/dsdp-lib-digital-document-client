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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of {@link MultipartFile} where the file data is held as a byte array.
 */
@Builder
public class ByteArrayMultipartFile implements MultipartFile {

  @Builder.Default
  @Getter
  private final String name = "file";
  @Getter
  private final String originalFilename;
  @Getter
  @Builder.Default
  private final byte[] bytes = new byte[0];
  @Getter
  private final String contentType;

  @Override
  public boolean isEmpty() {
    return bytes.length == 0;
  }

  @Override
  public long getSize() {
    return bytes.length;
  }

  @Override
  @NonNull
  public InputStream getInputStream() {
    return new ByteArrayInputStream(bytes);
  }

  @Override
  public void transferTo(@NonNull File dest) {
    throw new UnsupportedOperationException("This method is not used during upload");
  }
}
