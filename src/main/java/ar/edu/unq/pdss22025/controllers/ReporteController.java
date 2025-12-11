package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.ErrorResponse;
import ar.edu.unq.pdss22025.models.dto.ReporteTop5Response;
import ar.edu.unq.pdss22025.services.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@Tag(name = "reporte-controller", description = "Operaciones de reportes administrativos")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/autos-mas-vendidos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Top 5 autos más vendidos", description = "Obtiene el Top 5 de autos más vendidos. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte de autos más vendidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReporteTop5Response> obtenerAutosMasVendidos(
            @Parameter(description = "Fecha desde (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaDesde,
            @Parameter(description = "Fecha hasta (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaHasta,
            @Parameter(description = "Período: mes, trimestre, año")
            @RequestParam(required = false) String periodo) {
        ReporteTop5Response response = reporteService.obtenerAutosMasVendidos(fechaDesde, fechaHasta, periodo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuarios-mas-compras")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Top 5 usuarios con más compras", description = "Obtiene el Top 5 de usuarios con más compras. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte de usuarios con más compras"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReporteTop5Response> obtenerUsuariosMasCompras(
            @Parameter(description = "Fecha desde (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaDesde,
            @Parameter(description = "Fecha hasta (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaHasta,
            @Parameter(description = "Período: mes, trimestre, año")
            @RequestParam(required = false) String periodo) {
        ReporteTop5Response response = reporteService.obtenerUsuariosMasCompras(fechaDesde, fechaHasta, periodo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/autos-mejores-rankeados")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Top 5 autos mejores rankeados", description = "Obtiene el Top 5 de autos mejores rankeados según promedio de puntaje. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte de autos mejores rankeados"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReporteTop5Response> obtenerAutosMejoresRankeados(
            @Parameter(description = "Fecha desde (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaDesde,
            @Parameter(description = "Fecha hasta (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaHasta,
            @Parameter(description = "Período: mes, trimestre, año")
            @RequestParam(required = false) String periodo) {
        ReporteTop5Response response = reporteService.obtenerAutosMejoresRankeados(fechaDesde, fechaHasta, periodo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agencias-mas-ventas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Top 5 agencias con más ventas", description = "Obtiene el Top 5 de agencias (concesionarias) con más ventas. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte de agencias con más ventas"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ReporteTop5Response> obtenerAgenciasMasVentas(
            @Parameter(description = "Fecha desde (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaDesde,
            @Parameter(description = "Fecha hasta (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaHasta,
            @Parameter(description = "Período: mes, trimestre, año")
            @RequestParam(required = false) String periodo) {
        ReporteTop5Response response = reporteService.obtenerAgenciasMasVentas(fechaDesde, fechaHasta, periodo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exportar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exportar reporte", description = "Exporta un reporte en formato Excel o PDF. Solo ADMIN puede acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte exportado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - Solo ADMIN puede acceder",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> exportarReporte(
            @Parameter(description = "Tipo de reporte: autos-mas-vendidos, usuarios-mas-compras, autos-mejores-rankeados, agencias-mas-ventas")
            @RequestParam String tipo,
            @Parameter(description = "Formato: excel o pdf")
            @RequestParam String formato,
            @Parameter(description = "Fecha desde (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaDesde,
            @Parameter(description = "Fecha hasta (formato: YYYY-MM-DD)")
            @RequestParam(required = false) String fechaHasta,
            @Parameter(description = "Período: mes, trimestre, año")
            @RequestParam(required = false) String periodo) {
        // TODO: Implementar exportación a Excel/PDF
        // Por ahora retornamos un mensaje indicando que está en desarrollo
        return ResponseEntity.ok("Exportación de reportes en desarrollo. Tipo: " + tipo + ", Formato: " + formato);
    }
}

