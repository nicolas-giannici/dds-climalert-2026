package com.climalert.scheduler;

import com.climalert.entity.RegistroClima;
import com.climalert.repository.RegistroClimaRepository;
import com.climalert.service.AlertaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaSchedulerTest {

    @Mock
    private RegistroClimaRepository repository;

    @Mock
    private AlertaService alertaService;

    @InjectMocks
    private AlertaScheduler alertaScheduler;

    @Test
    void analizarUltimoRegistro_cuandoHayRegistro_evaluaAlerta() {
        RegistroClima registro = new RegistroClima(36.0, 70.0, "CABA", LocalDateTime.now());
        when(repository.findTopByOrderByFechaConsultaDesc()).thenReturn(Optional.of(registro));

        alertaScheduler.analizarUltimoRegistro();

        verify(alertaService, times(1)).evaluarYNotificar(registro);
    }

    @Test
    void analizarUltimoRegistro_cuandoNoHayRegistro_noEvaluaAlerta() {
        when(repository.findTopByOrderByFechaConsultaDesc()).thenReturn(Optional.empty());

        alertaScheduler.analizarUltimoRegistro();

        verify(alertaService, never()).evaluarYNotificar(any());
    }
}
