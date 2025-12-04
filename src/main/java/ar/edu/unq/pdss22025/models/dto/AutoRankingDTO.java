package ar.edu.unq.pdss22025.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoRankingDTO {
    private Long autoId;
    private String marca;
    private String modelo;
    private Integer anioModelo;
    private Double promedioPuntaje;
    private Long cantidadResenas;
}

