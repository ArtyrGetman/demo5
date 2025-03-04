package com.example.demo.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestTemplateService {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final ObjectMapper objectMapper;


    public String getRequest(String url, Map<String, String> paramMap) {
        try {
            String response = null;
            String finalUrl = buildUrl(url, paramMap);
            ResponseEntity<String> responseEntity = restTemplate.exchange(finalUrl, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, paramMap);
            response = responseEntity.getBody();
            return response;
        } catch (RestClientException e) {
            throw new RestClientException("REST ERROR ->" + e);
        }
    }

    private static String buildUrl(String url, Map<String, String> paramMap) {
        String finalUrl = url;
        if (!paramMap.isEmpty()) {
            StringBuilder newUrlBuilder = new StringBuilder(url).append("?");
            paramMap.forEach((k, v) -> newUrlBuilder.append(k).append("=").append(v).append("&"));
            String urlRequest = newUrlBuilder.toString();
            finalUrl = urlRequest.substring(0, urlRequest.length() - 1);
        }
        return finalUrl;
    }


    public <T> Object jsonToObjectConverter(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalStateException("Не можливо виконати конвертацію у об'єкт. " + json + " Помилка: " + e);
        }
    }


}
