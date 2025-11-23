package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.CompraResponse;
import ar.edu.unq.pdss22025.models.dto.CrearCompraRequest;
import ar.edu.unq.pdss22025.services.CompraService;
import ar.edu.unq.pdss22025.mapper.CompraMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ar.edu.unq.pdss22025.models.dto.ErrorResponse;

@RestController
@RequestMapping("/compras")
@Validated
@Tag(name = "compra-controller", description = "Operaciones relacionadas con compras")
public class CompraController {
    private final CompraService compraService;
    private final CompraMapper compraMapper;

    public CompraController(CompraService compraService, CompraMapper compraMapper) {
        this.compraService = compraService;
        this.compraMapper = compraMapper;
    }

    @PostMapping
    @Operation(summary = "Crear compra", description = "Crea una compra a partir de una oferta y un comprador. Devuelve la compra creada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Compra creada correctamente"),
            @ApiResponse(responseCode = "404", description = "Datos inválidos o recurso no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Operación no procesable por reglas de negocio",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CompraResponse> crearCompra(@Valid @RequestBody CrearCompraRequest request) {
        var compra = compraService.crear(request);
        return ResponseEntity.ok(compraMapper.toResponse(compra));
    }
}
