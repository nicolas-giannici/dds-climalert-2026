package com.climalert.scheduler;

import com.climalert.dto.WeatherApiResponse;
import com.climalert.repository.RegistroClimaRepository;
import com.climalert.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClimaSchedulerTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private RegistroClimaRepository repository;

    @InjectMocks
    private ClimaScheduler climaScheduler;

    @Test
    void extraerYGuardarClima_cuandoApiResponde_guardaRegistro() {
        WeatherApiResponse mockResponse = new WeatherApiResponse();
        WeatherApiResponse.Current current = new WeatherApiResponse.Current();
        current.setTempC(28.0);
        current.setHumidity(55.0);
        mockResponse.setCurrent(current);

        WeatherApiResponse.Location location = new WeatherApiResponse.Location();
        location.setName("Buenos Aires");
        mockResponse.setLocation(location);

        when(weatherService.obtenerClimaActual()).thenReturn(mockResponse);

        climaScheduler.extraerYGuardarClima();

        verify(repository, times(1)).save(any());
    }

    @Test
    void extraerYGuardarClima_cuandoApiFalla_noGuardaRegistro() {
        when(weatherService.obtenerClimaActual()).thenReturn(null);

        climaScheduler.extraerYGuardarClima();

        verify(repository, never()).save(any());
    }
}
