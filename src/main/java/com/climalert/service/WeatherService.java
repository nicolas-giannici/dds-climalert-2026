package com.climalert.service;

import com.climalert.dto.WeatherApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;

    @Value("${weatherapi.base-url}")
    private String baseUrl;

    @Value("${weatherapi.api-key}")
    private String apiKey;

    @Value("${weatherapi.location}")
    private String location;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherApiResponse obtenerClimaActual() {
        String url = String.format("%s/current.json?key=%s&q=%s", baseUrl, apiKey, location);
        try {
            WeatherApiResponse response = restTemplate.getForObject(url, WeatherApiResponse.class);
            log.info("Clima obtenido exitosamente para {}", location);
            return response;
        } catch (Exception e) {
            log.error("Error al obtener clima de WeatherAPI: {}", e.getMessage());
            return null;
        }
    }
}
