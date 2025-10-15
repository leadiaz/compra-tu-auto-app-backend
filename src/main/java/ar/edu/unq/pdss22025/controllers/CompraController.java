package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.CompraResponse;
import ar.edu.unq.pdss22025.models.dto.CrearCompraRequest;
import ar.edu.unq.pdss22025.services.CompraService;
import ar.edu.unq.pdss22025.mapper.CompraMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compras")
@Validated
public class CompraController {
    private final CompraService compraService;
    private final CompraMapper compraMapper;

    public CompraController(CompraService compraService, CompraMapper compraMapper) {
        this.compraService = compraService;
        this.compraMapper = compraMapper;
    }

    @PostMapping
    public ResponseEntity<CompraResponse> crearCompra(@Valid @RequestBody CrearCompraRequest request) {
        try {
            var compra = compraService.crear(request);
            return ResponseEntity.ok(compraMapper.toResponse(compra));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

