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

import com.epam.digital.data.platform.dgtldcmnt.dto.InternalApiDocumentMetadataDto;
import com.epam.digital.data.platform.dgtldcmnt.dto.RemoteDocumentMetadataDto;
import feign.Headers;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Feign client that is used for connecting to document service via v2 internal api.
 */
@FeignClient(
    name = "digital-document-internal-api-v2-client",
    url = "${digital-document-service.url}/internal-api/v2/documents")
public interface DigitalDocumentServiceInternalApiV2RestClient {

  /**
   * Upload document by process instance id
   *
   * @param processInstanceId id of process-instance document is needed to be uploaded for
   * @param filename          name of the file
   * @param file              the multipart file itself
   * @param headers           any custom headers to be sent
   * @return metadata dto for uploaded file
   */
  @PostMapping(value = "/{processInstanceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  InternalApiDocumentMetadataDto upload(
      @PathVariable("processInstanceId") String processInstanceId,
      @RequestParam("filename") String filename,
      @Param("file") MultipartFile file,
      @RequestHeader HttpHeaders headers);
}
