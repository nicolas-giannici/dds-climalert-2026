package com.climalert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class RegistroClima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double temperatura;

    private Double humedad;

    private String ubicacion;

    private LocalDateTime fechaConsulta;

    public RegistroClima() {}

    public RegistroClima(Double temperatura, Double humedad, String ubicacion, LocalDateTime fechaConsulta) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.ubicacion = ubicacion;
        this.fechaConsulta = fechaConsulta;
    }

    public Long getId() { return id; }

    public Double getTemperatura() { return temperatura; }

    public Double getHumedad() { return humedad; }

    public String getUbicacion() { return ubicacion; }

    public LocalDateTime getFechaConsulta() { return fechaConsulta; }

    @Override
    public String toString() {
        return "RegistroClima{" +
                "temperatura=" + temperatura +
                ", humedad=" + humedad +
                ", ubicacion='" + ubicacion + '\'' +
                ", fechaConsulta=" + fechaConsulta +
                '}';
    }
}
