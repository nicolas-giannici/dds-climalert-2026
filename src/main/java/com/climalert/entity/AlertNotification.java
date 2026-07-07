package com.climalert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class AlertNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double temperatura;

    private Double humedad;

    private LocalDateTime fechaEnvio;

    public AlertNotification() {}

    public AlertNotification(Double temperatura, Double humedad, LocalDateTime fechaEnvio) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.fechaEnvio = fechaEnvio;
    }

    public Long getId() { return id; }

    public Double getTemperatura() { return temperatura; }

    public Double getHumedad() { return humedad; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
}
