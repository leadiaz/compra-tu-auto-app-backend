package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.dto.AutoResponse;
import ar.edu.unq.pdss22025.models.dto.CrearAutoRequest;
import ar.edu.unq.pdss22025.models.dto.ErrorResponse;
import ar.edu.unq.pdss22025.services.AutoService;
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
@RequestMapping("autos")
@Tag(name = "auto-controller", description = "Operaciones para gestionar autos")
public class AutoController {

    private final AutoService autoService;

    public AutoController(AutoService autoService) {
        this.autoService = autoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear auto", description = "Crea un nuevo auto. Solo usuarios ADMIN pueden dar de alta autos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Auto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Ya existe un auto con la misma marca, modelo y año",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AutoResponse> crearAuto(@Valid @RequestBody CrearAutoRequest request) {
        Auto auto = autoService.crearAuto(
                request.getMarca(),
                request.getModelo(),
                request.getAnioModelo()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(auto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CONCESIONARIA')")
    @Operation(summary = "Listar autos", description = "Devuelve todos los autos disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de autos")
    })
    public ResponseEntity<List<AutoResponse>> listarAutos() {
        List<Auto> autos = autoService.listarAutos();
        List<AutoResponse> responses = autos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar auto", description = "Elimina un auto por su ID. Solo usuarios ADMIN pueden eliminar autos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Auto eliminado"),
            @ApiResponse(responseCode = "404", description = "Auto no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminarAuto(@PathVariable Long id) {
        autoService.eliminarAuto(id);
        return ResponseEntity.noContent().build();
    }

    private AutoResponse mapToResponse(Auto auto) {
        return new AutoResponse(
                auto.getId(),
                auto.getMarca(),
                auto.getModelo(),
                auto.getAnioModelo(),
                auto.getCreatedAt() != null ? auto.getCreatedAt().toLocalDateTime() : null,
                auto.getUpdatedAt() != null ? auto.getUpdatedAt().toLocalDateTime() : null
        );
    }
}

