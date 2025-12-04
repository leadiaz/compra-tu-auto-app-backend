package ar.edu.unq.pdss22025.models.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AutoResponse {
    private Long id;
    private String marca;
    private String modelo;
    private Integer anioModelo;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaActualizacion;

    public AutoResponse(Long id, String marca, String modelo, Integer anioModelo,
                        LocalDateTime fechaAlta, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.anioModelo = anioModelo;
        this.fechaAlta = fechaAlta;
        this.fechaActualizacion = fechaActualizacion;
    }
}

