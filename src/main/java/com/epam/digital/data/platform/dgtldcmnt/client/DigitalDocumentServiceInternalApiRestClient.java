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

import com.epam.digital.data.platform.dgtldcmnt.dto.InternalApiDocumentMetadataDto;
import com.epam.digital.data.platform.dgtldcmnt.dto.RemoteDocumentDto;
import com.epam.digital.data.platform.dgtldcmnt.dto.RemoteDocumentMetadataDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "digital-document-internal-api-client",
    url = "${digital-document-service.url}/internal-api/documents")
public interface DigitalDocumentServiceInternalApiRestClient {

  @PostMapping("/{processInstanceId}")
  RemoteDocumentMetadataDto upload(
      @PathVariable("processInstanceId") String processInstanceId,
      @RequestBody RemoteDocumentDto requestDto,
      @RequestHeader HttpHeaders headers);

  /**
   * Download document by process instance id and document id
   *
   * @param processInstanceId id of process-instance document
   * @param id                id of document
   * @param headers           any custom headers to be sent
   * @return digital document
   */
  @GetMapping("/{processInstanceId}/{id}")
  ResponseEntity<Resource> download(
      @PathVariable("processInstanceId") String processInstanceId,
      @PathVariable("id") String id,
      @RequestHeader HttpHeaders headers);

  /**
   * Get metadata of digital document
   *
   * @param processInstanceId id of process-instance document
   * @param id                id of document
   * @param headers           any custom headers to be sent
   * @return digital document metadata
   */
  @GetMapping("/{processInstanceId}/{id}/metadata")
  InternalApiDocumentMetadataDto getMetadata(
      @PathVariable("processInstanceId") String processInstanceId,
      @PathVariable("id") String id,
      @RequestHeader HttpHeaders headers);
}
