package com.climalert.scheduler;

import com.climalert.entity.RegistroClima;
import com.climalert.repository.RegistroClimaRepository;
import com.climalert.service.AlertaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AlertaScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertaScheduler.class);

    private final RegistroClimaRepository repository;
    private final AlertaService alertaService;

    public AlertaScheduler(RegistroClimaRepository repository, AlertaService alertaService) {
        this.repository = repository;
        this.alertaService = alertaService;
    }

    @Scheduled(fixedRate = 60000)
    public void analizarUltimoRegistro() {
        log.info("Analizando último registro climático...");
        var ultimoRegistro = repository.findTopByOrderByFechaConsultaDesc();

        if (ultimoRegistro.isEmpty()) {
            log.info("No hay registros climáticos para analizar");
            return;
        }

        RegistroClima registro = ultimoRegistro.get();
        alertaService.evaluarYNotificar(registro);
    }
}
