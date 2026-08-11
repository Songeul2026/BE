package com._geul2geul.songeul.remittance.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
public class OcrClient {

    private final RestTemplate restTemplate;
    private final String ocrUrl;

    public OcrClient(@Value("${app.ocr.base-url}") String baseUrl,
                      @Value("${app.ocr.connect-timeout-ms}") int connectTimeoutMs,
                      @Value("${app.ocr.read-timeout-ms}") int readTimeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restTemplate = new RestTemplate(requestFactory);
        this.ocrUrl = baseUrl + "/api/ocr";
    }

    public OcrResponse requestOcr(MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", toResource(image));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(ocrUrl, request, OcrResponse.class);
    }

    private ByteArrayResource toResource(MultipartFile image) {
        try {
            byte[] bytes = image.getBytes();
            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            };
        } catch (IOException e) {
            throw new UncheckedIOException("이미지를 읽을 수 없습니다.", e);
        }
    }

}
