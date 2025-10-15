package ar.edu.unq.pdss22025.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponse {
    private Long id;
    private Long autoId;
    private Long usuarioId;
    private Integer rating;
    private String comentario;
    private OffsetDateTime createdAt;
}
