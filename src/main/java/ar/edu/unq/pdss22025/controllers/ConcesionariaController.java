package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.models.dto.ConcesionariaResponse;
import ar.edu.unq.pdss22025.models.dto.CrearConcesionariaRequest;
import ar.edu.unq.pdss22025.models.dto.ErrorResponse;
import ar.edu.unq.pdss22025.services.ConcesionariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("concesionarias")
@Tag(name = "concesionaria-controller", description = "Operaciones relacionadas con concesionarias")
public class ConcesionariaController {

    private final ConcesionariaService concesionariaService;

    public ConcesionariaController(ConcesionariaService concesionariaService) {
        this.concesionariaService = concesionariaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear concesionaria", description = "Crea una nueva concesionaria. Solo usuarios ADMIN pueden crear concesionarias. Opcionalmente se puede relacionar con un usuario de tipo CONCESIONARIA.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Concesionaria creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida o error en la creación",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede crear concesionarias",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado (si se proporciona usuarioId)",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Usuario no válido - El usuario no es de tipo CONCESIONARIA",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ConcesionariaResponse> crearConcesionaria(@Valid @RequestBody CrearConcesionariaRequest request) {
        Concesionaria concesionaria = concesionariaService.crearConcesionaria(
                request.getNombre(),
                request.getCuit(),
                request.getTelefono(),
                request.getEmail(),
                request.getDireccion(),
                request.getUsuarioId()
        );

        ConcesionariaResponse response = new ConcesionariaResponse(
                concesionaria.getId(),
                concesionaria.getNombre(),
                concesionaria.getCuit(),
                concesionaria.getTelefono(),
                concesionaria.getEmail(),
                concesionaria.getDireccion(),
                concesionaria.getActiva(),
                concesionaria.getUsuario() != null ? concesionaria.getUsuario().getNombre() : null,
                concesionaria.getCreatedAt() != null ? concesionaria.getCreatedAt().toLocalDateTime() : null,
                concesionaria.getUpdatedAt() != null ? concesionaria.getUpdatedAt().toLocalDateTime() : null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar concesionarias", description = "Devuelve todas las concesionarias del sistema. Solo usuarios ADMIN pueden acceder.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de concesionarias"),
        @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede listar concesionarias",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ConcesionariaResponse>> obtenerTodasLasConcesionarias() {
        List<Concesionaria> concesionarias = concesionariaService.obtenerTodasLasConcesionarias();
        List<ConcesionariaResponse> responses = concesionarias.stream()
                .map(concesionaria -> new ConcesionariaResponse(
                        concesionaria.getId(),
                        concesionaria.getNombre(),
                        concesionaria.getCuit(),
                        concesionaria.getTelefono(),
                        concesionaria.getEmail(),
                        concesionaria.getDireccion(),
                        concesionaria.getActiva(),
                        concesionaria.getUsuario() != null ? concesionaria.getUsuario().getNombre() : null,
                        concesionaria.getCreatedAt() != null ? concesionaria.getCreatedAt().toLocalDateTime() : null,
                        concesionaria.getUpdatedAt() != null ? concesionaria.getUpdatedAt().toLocalDateTime() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}

