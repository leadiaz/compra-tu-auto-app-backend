package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.models.dto.*;
import ar.edu.unq.pdss22025.repositories.CompraRepository;
import ar.edu.unq.pdss22025.repositories.ResenaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final CompraRepository compraRepository;
    private final ResenaRepository resenaRepository;

    public ReporteService(CompraRepository compraRepository, ResenaRepository resenaRepository) {
        this.compraRepository = compraRepository;
        this.resenaRepository = resenaRepository;
    }

    /**
     * Obtiene el Top 5 de autos más vendidos
     */
    @Transactional(readOnly = true)
    public ReporteTop5Response obtenerAutosMasVendidos(String fechaDesde, String fechaHasta, String periodo) {
        OffsetDateTime desde = calcularFechaDesde(fechaDesde, periodo);
        OffsetDateTime hasta = calcularFechaHasta(fechaHasta, periodo);

        List<Object[]> resultados;
        if (desde != null && hasta != null) {
            resultados = compraRepository.findTop5AutosMasVendidos(desde, hasta);
        } else {
            resultados = compraRepository.findTop5AutosMasVendidos();
        }

        List<AutoMasVendidoDTO> items = resultados.stream()
                .map(row -> AutoMasVendidoDTO.builder()
                        .autoId(((Number) row[0]).longValue())
                        .marca((String) row[1])
                        .modelo((String) row[2])
                        .anioModelo(((Number) row[3]).intValue())
                        .cantidadVentas(((Number) row[4]).longValue())
                        .precioPromedio(((BigDecimal) row[5]))
                        .totalIngresos(((BigDecimal) row[6]))
                        .build())
                .collect(Collectors.toList());

        ReporteTop5Response response = new ReporteTop5Response();
        response.setItems(items);
        response.setFechaDesde(fechaDesde);
        response.setFechaHasta(fechaHasta);
        response.setPeriodo(periodo);
        return response;
    }

    /**
     * Obtiene el Top 5 de usuarios con más compras
     */
    @Transactional(readOnly = true)
    public ReporteTop5Response obtenerUsuariosMasCompras(String fechaDesde, String fechaHasta, String periodo) {
        OffsetDateTime desde = calcularFechaDesde(fechaDesde, periodo);
        OffsetDateTime hasta = calcularFechaHasta(fechaHasta, periodo);

        List<Object[]> resultados;
        if (desde != null && hasta != null) {
            resultados = compraRepository.findTop5UsuariosMasCompras(desde, hasta);
        } else {
            resultados = compraRepository.findTop5UsuariosMasCompras();
        }

        List<UsuarioMasComprasDTO> items = resultados.stream()
                .map(row -> UsuarioMasComprasDTO.builder()
                        .usuarioId(((Number) row[0]).longValue())
                        .nombre((String) row[1])
                        .apellido((String) row[2])
                        .email((String) row[3])
                        .cantidadCompras(((Number) row[4]).longValue())
                        .totalGastado(((BigDecimal) row[5]))
                        .build())
                .collect(Collectors.toList());

        ReporteTop5Response response = new ReporteTop5Response();
        response.setItems(items);
        response.setFechaDesde(fechaDesde);
        response.setFechaHasta(fechaHasta);
        response.setPeriodo(periodo);
        return response;
    }

    /**
     * Obtiene el Top 5 de autos mejores rankeados
     */
    @Transactional(readOnly = true)
    public ReporteTop5Response obtenerAutosMejoresRankeados(String fechaDesde, String fechaHasta, String periodo) {
        // Para este reporte, usamos el método existente de ResenaService
        // pero podríamos filtrar por fecha si es necesario
        List<Object[]> resultados = resenaRepository.findTopAutosMejorRanqueados(5);

        List<AutoRankingDTO> items = resultados.stream()
                .map(row -> AutoRankingDTO.builder()
                        .autoId(((Number) row[0]).longValue())
                        .marca((String) row[1])
                        .modelo((String) row[2])
                        .anioModelo(((Number) row[3]).intValue())
                        .promedioPuntaje(((Number) row[4]).doubleValue())
                        .cantidadResenas(((Number) row[5]).longValue())
                        .build())
                .collect(Collectors.toList());

        ReporteTop5Response response = new ReporteTop5Response();
        response.setItems(items);
        response.setFechaDesde(fechaDesde);
        response.setFechaHasta(fechaHasta);
        response.setPeriodo(periodo);
        return response;
    }

    /**
     * Obtiene el Top 5 de agencias con más ventas
     */
    @Transactional(readOnly = true)
    public ReporteTop5Response obtenerAgenciasMasVentas(String fechaDesde, String fechaHasta, String periodo) {
        OffsetDateTime desde = calcularFechaDesde(fechaDesde, periodo);
        OffsetDateTime hasta = calcularFechaHasta(fechaHasta, periodo);

        List<Object[]> resultados;
        if (desde != null && hasta != null) {
            resultados = compraRepository.findTop5AgenciasMasVentas(desde, hasta);
        } else {
            resultados = compraRepository.findTop5AgenciasMasVentas();
        }

        List<AgenciaMasVentasDTO> items = resultados.stream()
                .map(row -> AgenciaMasVentasDTO.builder()
                        .concesionariaId(((Number) row[0]).longValue())
                        .razonSocial((String) row[1])
                        .cantidadVentas(((Number) row[2]).longValue())
                        .totalIngresos(((BigDecimal) row[3]))
                        .build())
                .collect(Collectors.toList());

        ReporteTop5Response response = new ReporteTop5Response();
        response.setItems(items);
        response.setFechaDesde(fechaDesde);
        response.setFechaHasta(fechaHasta);
        response.setPeriodo(periodo);
        return response;
    }

    private OffsetDateTime calcularFechaDesde(String fechaDesde, String periodo) {
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            return LocalDate.parse(fechaDesde).atStartOfDay().atOffset(ZoneOffset.UTC);
        }
        if (periodo != null) {
            return switch (periodo) {
                case "mes" -> OffsetDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                case "trimestre" -> {
                    int mesActual = OffsetDateTime.now().getMonthValue();
                    int primerMesTrimestre = ((mesActual - 1) / 3) * 3 + 1;
                    yield OffsetDateTime.now().withMonth(primerMesTrimestre).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                }
                case "año" -> OffsetDateTime.now().withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                default -> null;
            };
        }
        return null;
    }

    private OffsetDateTime calcularFechaHasta(String fechaHasta, String periodo) {
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            return LocalDate.parse(fechaHasta).atTime(23, 59, 59).atOffset(ZoneOffset.UTC);
        }
        if (periodo != null) {
            return OffsetDateTime.now().withHour(23).withMinute(59).withSecond(59);
        }
        return null;
    }
}

