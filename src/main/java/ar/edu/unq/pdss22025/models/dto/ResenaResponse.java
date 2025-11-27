package ar.edu.unq.pdss22025.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponse {
    private Long id;
    private Long autoId;
    private String autoMarca;
    private String autoModelo;
    private Integer autoAnioModelo;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioApellido;
    private Integer puntaje;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
