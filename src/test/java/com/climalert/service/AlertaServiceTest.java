package com.climalert.service;

import com.climalert.entity.AlertNotification;
import com.climalert.entity.RegistroClima;
import com.climalert.repository.AlertNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private AlertNotificationRepository alertNotificationRepository;

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
    void evaluarYNotificar_cuandoEsNormal_noEnviaAlerta() {
        alertaService.evaluarYNotificar(registroNormal);
        verify(notificacionService, never()).enviarAlerta(any());
        verify(alertNotificationRepository, never()).save(any());
    }

    @Test
    void evaluarYNotificar_cuandoEsCriticoYSinRestricciones_enviaAlerta() {
        when(alertNotificationRepository.countByFechaEnvioAfter(any())).thenReturn(0L);
        when(alertNotificationRepository.findTopByOrderByFechaEnvioDesc()).thenReturn(Optional.empty());

        alertaService.evaluarYNotificar(registroCritico);

        verify(notificacionService, times(1)).enviarAlerta(registroCritico);
        verify(alertNotificationRepository, times(1)).save(any(AlertNotification.class));
    }

    @Test
    void evaluarYNotificar_cuandoEsCriticoPeroCooldownActivo_noEnvia() {
        when(alertNotificationRepository.countByFechaEnvioAfter(any())).thenReturn(0L);
        AlertNotification notifReciente = new AlertNotification(36.0, 70.0, LocalDateTime.now().minusHours(3));
        when(alertNotificationRepository.findTopByOrderByFechaEnvioDesc()).thenReturn(Optional.of(notifReciente));

        alertaService.evaluarYNotificar(registroCritico);

        verify(notificacionService, never()).enviarAlerta(any());
        verify(alertNotificationRepository, never()).save(any());
    }

    @Test
    void evaluarYNotificar_cuandoEsCriticoYCooldownExpirado_envia() {
        when(alertNotificationRepository.countByFechaEnvioAfter(any())).thenReturn(1L);
        AlertNotification notifAntigua = new AlertNotification(36.0, 70.0, LocalDateTime.now().minusHours(5));
        when(alertNotificationRepository.findTopByOrderByFechaEnvioDesc()).thenReturn(Optional.of(notifAntigua));

        alertaService.evaluarYNotificar(registroCritico);

        verify(notificacionService, times(1)).enviarAlerta(registroCritico);
        verify(alertNotificationRepository, times(1)).save(any(AlertNotification.class));
    }

    @Test
    void evaluarYNotificar_cuandoEsCriticoPeroLimiteDiarioAlcanzado_noEnvia() {
        when(alertNotificationRepository.countByFechaEnvioAfter(any())).thenReturn(3L);

        alertaService.evaluarYNotificar(registroCritico);

        verify(alertNotificationRepository, never()).findTopByOrderByFechaEnvioDesc();
        verify(notificacionService, never()).enviarAlerta(any());
        verify(alertNotificationRepository, never()).save(any());
    }
}
