package ar.edu.unq.pdss22025.models.dto;

import lombok.Data;

@Data
public class CrearResenaRequest {
    private Long autoId;
    private Long usuarioId;
    private Integer rating;
    private String comentario;
}
