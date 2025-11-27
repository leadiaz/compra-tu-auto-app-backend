package ar.edu.unq.pdss22025.models.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearAutoRequest {

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotNull(message = "El año del modelo es obligatorio")
    @Min(value = 1900, message = "El año del modelo debe ser mayor o igual a 1900")
    private Integer anioModelo;
}

