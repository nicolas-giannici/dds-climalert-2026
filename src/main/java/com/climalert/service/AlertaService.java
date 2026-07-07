package com.climalert.service;

import com.climalert.entity.RegistroClima;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private static final double TEMPERATURA_UMBRAL = 35.0;
    private static final double HUMEDAD_UMBRAL = 60.0;

    private final NotificacionService notificacionService;

    public AlertaService(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    public boolean esCondicionCritica(RegistroClima registro) {
        return registro.getTemperatura() > TEMPERATURA_UMBRAL && registro.getHumedad() > HUMEDAD_UMBRAL;
    }

    public void evaluarYNotificar(RegistroClima registro) {
        if (esCondicionCritica(registro)) {
            log.warn("Condición crítica detectada: {}", registro);
            notificacionService.enviarAlerta(registro);
        } else {
            log.info("Condición normal: temp={}, humedad={}", registro.getTemperatura(), registro.getHumedad());
        }
    }
}
