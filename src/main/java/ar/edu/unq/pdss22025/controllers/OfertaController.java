package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.OfertaResponse;
import ar.edu.unq.pdss22025.services.OfertaService;
import ar.edu.unq.pdss22025.mapper.OfertaMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import ar.edu.unq.pdss22025.models.dto.ErrorResponse;

@RestController
@RequestMapping("/ofertas")
@Validated
@Tag(name = "oferta-controller", description = "Operaciones relacionadas con ofertas y autos")
public class OfertaController {
    private final OfertaService ofertaService;
    private final OfertaMapper ofertaMapper;

    public OfertaController(OfertaService ofertaService, OfertaMapper ofertaMapper) {
        this.ofertaService = ofertaService;
        this.ofertaMapper = ofertaMapper;
    }

    @GetMapping
    @Operation(summary = "Listar ofertas por concesionaria", description = "Devuelve las ofertas asociadas a una concesionaria dada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de ofertas"),
            @ApiResponse(responseCode = "404", description = "No se encontraron ofertas para la concesionaria",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<OfertaResponse>> getOfertasByConcesionaria(@Parameter(description = "ID de la concesionaria", required = true) @RequestParam("concesionariaId") @NotNull Long concesionariaId) {
        List<ar.edu.unq.pdss22025.models.OfertaAuto> ofertas = ofertaService.listarPorConcesionaria(concesionariaId);
        if (ofertas == null || ofertas.isEmpty()) return ResponseEntity.notFound().build();
        List<OfertaResponse> response = ofertas.stream().map(ofertaMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/autos/{autoId}")
    @Operation(summary = "Listar ofertas por auto", description = "Devuelve las ofertas disponibles para un auto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de ofertas para el auto"),
            @ApiResponse(responseCode = "404", description = "No se encontraron ofertas para el auto",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<OfertaResponse>> getOfertasByAuto(@Parameter(description = "ID del auto", required = true) @PathVariable("autoId") @NotNull Long autoId) {
        List<ar.edu.unq.pdss22025.models.OfertaAuto> ofertas = ofertaService.listarPorAuto(autoId);
        if (ofertas == null || ofertas.isEmpty()) return ResponseEntity.notFound().build();
        List<OfertaResponse> response = ofertas.stream().map(ofertaMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
}
