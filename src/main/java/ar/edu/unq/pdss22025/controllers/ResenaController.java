package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.ResenaResponse;
import ar.edu.unq.pdss22025.models.dto.CrearResenaRequest;
import ar.edu.unq.pdss22025.services.ResenaService;
import ar.edu.unq.pdss22025.mapper.ResenaMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/resenas")
@Validated
@Tag(name = "resena-controller", description = "Operaciones relacionadas con reseñas de autos")
public class ResenaController {
    private final ResenaService resenaService;
    private final ResenaMapper resenaMapper;

    public ResenaController(ResenaService resenaService, ResenaMapper resenaMapper) {
        this.resenaService = resenaService;
        this.resenaMapper = resenaMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPRADOR') or hasRole('ADMIN')")
    @Operation(summary = "Crear reseña", description = "Crea una reseña para un auto por parte de un usuario y devuelve la reseña creada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reseña creada correctamente"),
            @ApiResponse(responseCode = "404", description = "Datos inválidos o recurso no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ResenaResponse> crearResena(@Valid @RequestBody CrearResenaRequest request) {
        var resena = resenaService.crear(request);
        return ResponseEntity.ok(resenaMapper.toResponse(resena));
    }

    @GetMapping("/autos/{autoId}")
    @PreAuthorize("hasAnyRole('COMPRADOR', 'CONCESIONARIA', 'ADMIN')")
    @Operation(summary = "Listar reseñas por auto", description = "Obtiene todas las reseñas asociadas a un auto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de reseñas para el auto"),
            @ApiResponse(responseCode = "404", description = "No se encontraron reseñas o el auto es inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ResenaResponse>> getResenasByAuto(@Parameter(description = "ID del auto", required = true) @PathVariable("autoId") @NotNull Long autoId) {
        List<ar.edu.unq.pdss22025.models.Resena> resenas = resenaService.listarPorAuto(autoId);
        if (resenas == null || resenas.isEmpty()) return ResponseEntity.notFound().build();
        List<ResenaResponse> response = resenas.stream().map(resenaMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
}
