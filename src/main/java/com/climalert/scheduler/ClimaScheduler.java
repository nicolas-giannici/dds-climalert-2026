package com.climalert.scheduler;

import com.climalert.dto.WeatherApiResponse;
import com.climalert.entity.RegistroClima;
import com.climalert.repository.RegistroClimaRepository;
import com.climalert.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClimaScheduler {

    private static final Logger log = LoggerFactory.getLogger(ClimaScheduler.class);

    private final WeatherService weatherService;
    private final RegistroClimaRepository repository;

    public ClimaScheduler(WeatherService weatherService, RegistroClimaRepository repository) {
        this.weatherService = weatherService;
        this.repository = repository;
    }

    @Scheduled(fixedRate = 300000)
    public void extraerYGuardarClima() {
        log.info("Ejecutando extracción de clima...");
        WeatherApiResponse response = weatherService.obtenerClimaActual();

        if (response == null || response.getCurrent() == null) {
            log.warn("No se pudo obtener datos climáticos");
            return;
        }

        RegistroClima registro = new RegistroClima(
                response.getCurrent().getTempC(),
                response.getCurrent().getHumidity(),
                response.getLocation() != null ? response.getLocation().getName() : "CABA",
                LocalDateTime.now()
        );

        repository.save(registro);
        log.info("Registro climático guardado: temp={}, humedad={}", registro.getTemperatura(), registro.getHumedad());
    }
}
