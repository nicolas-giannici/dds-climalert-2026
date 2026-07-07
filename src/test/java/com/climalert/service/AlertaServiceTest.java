package com.climalert.service;

import com.climalert.entity.RegistroClima;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private AlertaService alertaService;

    private RegistroClima registroNormal;
    private RegistroClima registroCritico;

    @BeforeEach
    void setUp() {
        registroNormal = new RegistroClima(25.0, 50.0, "CABA", LocalDateTime.now());
        registroCritico = new RegistroClima(36.0, 70.0, "CABA", LocalDateTime.now());
    }

    @Test
    void esCondicionCritica_cuandoTemperaturaYHumedadSuperanUmbral_retornaTrue() {
        assertTrue(alertaService.esCondicionCritica(registroCritico));
    }

    @Test
    void esCondicionCritica_cuandoTemperaturaEsMenorOIgual_retornaFalse() {
        assertFalse(alertaService.esCondicionCritica(registroNormal));
    }

    @Test
    void esCondicionCritica_cuandoHumedadEsMenorOIgual_retornaFalse() {
        RegistroClima registro = new RegistroClima(36.0, 50.0, "CABA", LocalDateTime.now());
        assertFalse(alertaService.esCondicionCritica(registro));
    }

    @Test
    void esCondicionCritica_cuandoTemperaturaEsExactamente35_retornaFalse() {
        RegistroClima registro = new RegistroClima(35.0, 70.0, "CABA", LocalDateTime.now());
        assertFalse(alertaService.esCondicionCritica(registro));
    }

    @Test
    void esCondicionCritica_cuandoHumedadEsExactamente60_retornaFalse() {
        RegistroClima registro = new RegistroClima(36.0, 60.0, "CABA", LocalDateTime.now());
        assertFalse(alertaService.esCondicionCritica(registro));
    }

    @Test
    void evaluarYNotificar_cuandoEsCritico_enviaAlerta() {
        alertaService.evaluarYNotificar(registroCritico);
        verify(notificacionService, times(1)).enviarAlerta(registroCritico);
    }

    @Test
    void evaluarYNotificar_cuandoEsNormal_noEnviaAlerta() {
        alertaService.evaluarYNotificar(registroNormal);
        verify(notificacionService, never()).enviarAlerta(any());
    }
}
