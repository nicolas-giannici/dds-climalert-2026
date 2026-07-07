package com.climalert.service;

import com.climalert.entity.AlertNotification;
import com.climalert.entity.RegistroClima;
import com.climalert.repository.AlertNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AlertaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaService.class);

    private static final double TEMPERATURA_UMBRAL = 35.0;
    private static final double HUMEDAD_UMBRAL = 60.0;

    private static final long MAX_ALERTAS_POR_DIA = 3;
    private static final long COOLDOWN_HORAS = 4;

    private final NotificacionService notificacionService;
    private final AlertNotificationRepository alertNotificationRepository;

    public AlertaService(NotificacionService notificacionService, AlertNotificationRepository alertNotificationRepository) {
        this.notificacionService = notificacionService;
        this.alertNotificationRepository = alertNotificationRepository;
    }

    public boolean esCondicionCritica(RegistroClima registro) {
        return registro.getTemperatura() > TEMPERATURA_UMBRAL && registro.getHumedad() > HUMEDAD_UMBRAL;
    }

    public void evaluarYNotificar(RegistroClima registro) {
        if (!esCondicionCritica(registro)) {
            log.info("Condición normal: temp={}, humedad={}", registro.getTemperatura(), registro.getHumedad());
            return;
        }

        log.warn("Condición crítica detectada: {}", registro);

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioDelDia = ahora.toLocalDate().atStartOfDay();

        long alertasHoy = alertNotificationRepository.countByFechaEnvioAfter(inicioDelDia);
        if (alertasHoy >= MAX_ALERTAS_POR_DIA) {
            log.info("Límite diario de {} alertas alcanzado, omitiendo notificación", MAX_ALERTAS_POR_DIA);
            return;
        }

        var ultimaNotificacion = alertNotificationRepository.findTopByOrderByFechaEnvioDesc();
        if (ultimaNotificacion.isPresent()) {
            LocalDateTime ultimaFecha = ultimaNotificacion.get().getFechaEnvio();
            if (ultimaFecha.plusHours(COOLDOWN_HORAS).isAfter(ahora)) {
                log.info("Cooldown de {} horas activo (última alerta: {}), omitiendo notificación", COOLDOWN_HORAS, ultimaFecha);
                return;
            }
        }

        notificacionService.enviarAlerta(registro);
        alertNotificationRepository.save(new AlertNotification(registro.getTemperatura(), registro.getHumedad(), ahora));
        log.info("Alerta notificada y registrada exitosamente");
    }
}
