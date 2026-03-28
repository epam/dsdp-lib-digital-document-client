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

import com.epam.digital.data.platform.dgtldcmnt.dto.DownloadDigitalDocumentDto;
import com.epam.digital.data.platform.dgtldcmnt.dto.SharedWithRequestDto;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Feign client for digital document service v1alpha REST API.
 * Provides declarative HTTP client methods for uploading and downloading documents
 * from remote digital document storage service.
 */
@FeignClient(
    name = "digital-document-service-v1alpha-client",
    url = "${digital-document-service.url}/v1alpha/documents")
public interface DigitalDocumentServiceV1AlphaRestClient {

  /**
   * Uploads a multipart document file to remote storage.
   * Sends file content with optional custom filename. Requires X-Access-Token header
   * for authentication. Returns metadata of uploaded document including generated ID,
   * checksum, content type, and size.
   *
   * @param file the multipart file to upload (PDF, PNG, JPG/JPEG, CSV, ASICs, P7S formats)
   * @param filename optional custom filename; if not provided, uses original filename from multipart file
   * @param headers HTTP headers including X-Access-Token for authentication
   * @return document metadata including generated ID, filename, content type, checksum, and size
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  DownloadDigitalDocumentDto upload(
      @Param("file") MultipartFile file,
      @RequestParam(value = "filename", required = false) String filename,
      @RequestHeader HttpHeaders headers);

  /**
   * Downloads a document by its unique identifier from remote storage.
   * Retrieves document content and metadata. Requires X-Access-Token header
   * for authentication. Returns document as downloadable resource with appropriate
   * Content-Type and Content-Disposition headers.
   *
   * @param id the unique document identifier
   * @param headers HTTP headers including X-Access-Token for authentication
   * @return ResponseEntity containing document as downloadable resource with proper headers
   */
  @GetMapping("/{id}")
  ResponseEntity<Resource> download(
      @PathVariable("id") String id,
      @RequestHeader HttpHeaders headers);

  @PostMapping("/shared-with")
  void sharedWith(@RequestBody SharedWithRequestDto requestDto, @RequestHeader HttpHeaders headers);
}
