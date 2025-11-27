package ar.edu.unq.pdss22025.models.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearFavoritoRequest {
    
    @NotNull(message = "El ID de la oferta es obligatorio")
    private Long ofertaId;
}

