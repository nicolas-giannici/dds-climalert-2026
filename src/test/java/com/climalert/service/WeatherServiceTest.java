package com.climalert.service;

import com.climalert.dto.WeatherApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void obtenerClimaActual_cuandoApiResponde_retornaResponse() {
        ReflectionTestUtils.setField(weatherService, "baseUrl", "https://api.weatherapi.com/v1");
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-key");
        ReflectionTestUtils.setField(weatherService, "location", "CABA");

        WeatherApiResponse mockResponse = new WeatherApiResponse();
        WeatherApiResponse.Current current = new WeatherApiResponse.Current();
        current.setTempC(25.0);
        current.setHumidity(60.0);
        mockResponse.setCurrent(current);

        WeatherApiResponse.Location location = new WeatherApiResponse.Location();
        location.setName("Buenos Aires");
        mockResponse.setLocation(location);

        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class))).thenReturn(mockResponse);

        WeatherApiResponse result = weatherService.obtenerClimaActual();
        assertNotNull(result);
        assertEquals(25.0, result.getCurrent().getTempC());
        assertEquals(60.0, result.getCurrent().getHumidity());
    }

    @Test
    void obtenerClimaActual_cuandoApiFalla_retornaNull() {
        ReflectionTestUtils.setField(weatherService, "baseUrl", "https://api.weatherapi.com/v1");
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-key");
        ReflectionTestUtils.setField(weatherService, "location", "CABA");

        when(restTemplate.getForObject(anyString(), eq(WeatherApiResponse.class))).thenThrow(new RuntimeException("API error"));

        WeatherApiResponse result = weatherService.obtenerClimaActual();
        assertNull(result);
    }
}
