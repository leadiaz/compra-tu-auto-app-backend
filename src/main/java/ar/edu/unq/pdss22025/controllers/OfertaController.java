package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.exceptions.CredencialesInvalidasException;
import ar.edu.unq.pdss22025.models.dto.CrearOfertaRequest;
import ar.edu.unq.pdss22025.models.dto.OfertaResponse;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.services.OfertaService;
import ar.edu.unq.pdss22025.services.UsuarioService;
import ar.edu.unq.pdss22025.mapper.OfertaMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/ofertas")
@Validated
@Tag(name = "oferta-controller", description = "Operaciones relacionadas con ofertas y autos")
public class OfertaController {
    private final OfertaService ofertaService;
    private final OfertaMapper ofertaMapper;
    private final UsuarioService usuarioService;

    public OfertaController(OfertaService ofertaService, OfertaMapper ofertaMapper, UsuarioService usuarioService) {
        this.ofertaService = ofertaService;
        this.ofertaMapper = ofertaMapper;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CONCESIONARIA')")
    @Operation(summary = "Crear oferta", description = "Crea una nueva oferta de auto. Solo usuarios CONCESIONARIA pueden crear ofertas para su propia concesionaria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Oferta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo CONCESIONARIA puede crear ofertas", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Auto no encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe una oferta para esta concesionaria y este auto", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "El usuario no tiene una concesionaria asociada o no está activa", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OfertaResponse> crearOferta(@Valid @RequestBody CrearOfertaRequest request) {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no autenticado"));
        
        var oferta = ofertaService.crearOferta(
                usuario,
                request.getAutoId(),
                request.getStock(),
                request.getPrecioActual(),
                request.getMoneda()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ofertaMapper.toResponse(oferta));
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
    @PreAuthorize("hasAnyRole('COMPRADOR', 'CONCESIONARIA', 'ADMIN')")
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

    @GetMapping("/mis-ofertas")
    @PreAuthorize("hasRole('CONCESIONARIA')")
    @Operation(summary = "Listar mis ofertas", description = "Devuelve todas las ofertas publicadas por la concesionaria del usuario autenticado. Solo usuarios CONCESIONARIA pueden acceder a este endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de ofertas de la concesionaria"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo CONCESIONARIA puede acceder", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontraron ofertas para la concesionaria",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "El usuario no tiene una concesionaria asociada", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<OfertaResponse>> getMisOfertas() {
        Usuario usuario = usuarioService.obtenerUsuarioAutenticado()
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario no autenticado"));
        
        List<ar.edu.unq.pdss22025.models.OfertaAuto> ofertas = ofertaService.listarPorUsuarioConcesionaria(usuario);
        if (ofertas == null || ofertas.isEmpty()) {
            return ResponseEntity.ok(List.of()); // Retornar lista vacía en lugar de 404
        }
        List<OfertaResponse> response = ofertas.stream().map(ofertaMapper::toResponse).toList();
        return ResponseEntity.ok(response);
    }
}
