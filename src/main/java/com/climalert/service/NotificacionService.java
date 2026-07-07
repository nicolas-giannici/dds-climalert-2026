package com.climalert.service;

import com.climalert.entity.RegistroClima;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionService.class);

    private final JavaMailSender mailSender;

    @Value("${climalert.destinatarios}")
    private String destinatariosRaw;

    public NotificacionService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarAlerta(RegistroClima registro) {
        List<String> destinatarios = Arrays.asList(destinatariosRaw.split(","));

        String asunto = String.format("ALERTA CLIMÁTICA - %s - %.1f°C / %.1f%% humedad",
                registro.getUbicacion(), registro.getTemperatura(), registro.getHumedad());

        String cuerpo = String.format("""
                ALERTA CLIMÁTICA - Climalert
                ==============================
                Ubicación: %s
                Temperatura: %.1f °C
                Humedad: %.1f %%
                Fecha: %s
                                
                Se ha detectado una condición climática peligrosa.
                Temperatura > 35°C y Humedad > 60%%
                """,
                registro.getUbicacion(),
                registro.getTemperatura(),
                registro.getHumedad(),
                registro.getFechaConsulta().toString());

        for (String destinatario : destinatarios) {
            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(destinatario.trim());
                mensaje.setSubject(asunto);
                mensaje.setText(cuerpo);
                mailSender.send(mensaje);
                log.info("Alerta enviada a {}", destinatario.trim());
            } catch (Exception e) {
                log.error("Error al enviar alerta a {}: {}", destinatario.trim(), e.getMessage());
            }
        }
    }
}
