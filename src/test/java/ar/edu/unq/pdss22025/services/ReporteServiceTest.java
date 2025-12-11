package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.models.*;
import ar.edu.unq.pdss22025.models.dto.*;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import ar.edu.unq.pdss22025.repositories.CompraRepository;
import ar.edu.unq.pdss22025.repositories.ConcesionariaRepository;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import ar.edu.unq.pdss22025.repositories.ResenaRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(ReporteService.class)
class ReporteServiceTest {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private ConcesionariaRepository concesionariaRepository;

    @Autowired
    private OfertaAutoRepository ofertaAutoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioComprador comprador1;
    private UsuarioComprador comprador2;
    private Concesionaria concesionaria1;
    private Concesionaria concesionaria2;
    private Auto auto1;
    private Auto auto2;
    private Auto auto3;
    private OfertaAuto oferta1;
    private OfertaAuto oferta2;
    private OfertaAuto oferta3;

    @BeforeEach
    void setUp() {
        // Crear usuarios compradores
        comprador1 = new UsuarioComprador();
        comprador1.setEmail("comprador1@test.com");
        comprador1.setPassword("password");
        comprador1.setNombre("Juan");
        comprador1.setApellido("Pérez");
        comprador1.setActivo(true);
        comprador1 = usuarioRepository.save(comprador1);

        comprador2 = new UsuarioComprador();
        comprador2.setEmail("comprador2@test.com");
        comprador2.setPassword("password");
        comprador2.setNombre("María");
        comprador2.setApellido("García");
        comprador2.setActivo(true);
        comprador2 = usuarioRepository.save(comprador2);

        // Crear concesionarias
        concesionaria1 = Concesionaria.builder()
                .nombre("Concesionaria 1")
                .cuit("20-11111111-1")
                .activa(true)
                .build();
        concesionaria1 = concesionariaRepository.save(concesionaria1);

        concesionaria2 = Concesionaria.builder()
                .nombre("Concesionaria 2")
                .cuit("20-22222222-2")
                .activa(true)
                .build();
        concesionaria2 = concesionariaRepository.save(concesionaria2);

        // Crear autos
        auto1 = Auto.builder()
                .marca("Toyota")
                .modelo("Corolla")
                .anioModelo(2023)
                .build();
        auto1 = autoRepository.save(auto1);

        auto2 = Auto.builder()
                .marca("Honda")
                .modelo("Civic")
                .anioModelo(2023)
                .build();
        auto2 = autoRepository.save(auto2);

        auto3 = Auto.builder()
                .marca("Ford")
                .modelo("Focus")
                .anioModelo(2024)
                .build();
        auto3 = autoRepository.save(auto3);

        // Crear ofertas
        oferta1 = OfertaAuto.builder()
                .concesionaria(concesionaria1)
                .auto(auto1)
                .stock(10)
                .precioActual(new BigDecimal("25000.00"))
                .moneda("USD")
                .build();
        oferta1 = ofertaAutoRepository.save(oferta1);

        oferta2 = OfertaAuto.builder()
                .concesionaria(concesionaria1)
                .auto(auto2)
                .stock(10)
                .precioActual(new BigDecimal("22000.00"))
                .moneda("USD")
                .build();
        oferta2 = ofertaAutoRepository.save(oferta2);

        oferta3 = OfertaAuto.builder()
                .concesionaria(concesionaria2)
                .auto(auto3)
                .stock(10)
                .precioActual(new BigDecimal("20000.00"))
                .moneda("USD")
                .build();
        oferta3 = ofertaAutoRepository.save(oferta3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Obtener autos más vendidos sin filtros")
    void obtenerAutosMasVendidos_SinFiltros_DeberiaRetornarTop5() {
        // Given: Crear compras
        OffsetDateTime fechaBase = OffsetDateTime.now().withHour(12).withMinute(0).withSecond(0);
        
        // Auto1: 3 compras
        crearCompra(oferta1, comprador1, new BigDecimal("25000.00"), fechaBase);
        crearCompra(oferta1, comprador2, new BigDecimal("25000.00"), fechaBase.plusDays(1));
        crearCompra(oferta1, comprador1, new BigDecimal("25000.00"), fechaBase.plusDays(2));

        // Auto2: 2 compras
        crearCompra(oferta2, comprador2, new BigDecimal("22000.00"), fechaBase);
        crearCompra(oferta2, comprador1, new BigDecimal("22000.00"), fechaBase.plusDays(1));

        // Auto3: 1 compra
        crearCompra(oferta3, comprador1, new BigDecimal("20000.00"), fechaBase);

        entityManager.flush();
        entityManager.clear();

        // When
        ReporteTop5Response response = reporteService.obtenerAutosMasVendidos(null, null, null);

        // Then
        assertNotNull(response);
        assertNotNull(response.getItems());
        assertTrue(response.getItems().size() > 0);
        
        @SuppressWarnings("unchecked")
        List<AutoMasVendidoDTO> items = (List<AutoMasVendidoDTO>) response.getItems();
        
        // Verificar que el auto1 tiene más ventas
        AutoMasVendidoDTO topAuto = items.get(0);
        assertEquals(auto1.getId(), topAuto.getAutoId());
        assertEquals(3L, topAuto.getCantidadVentas());
    }

    @Test
    @DisplayName("Obtener autos más vendidos con filtros de fecha")
    void obtenerAutosMasVendidos_ConFiltrosFecha_DeberiaFiltrarCorrectamente() {
        // Given: Crear compras en diferentes fechas
        OffsetDateTime fechaInicio = OffsetDateTime.of(2024, 1, 15, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fechaFuera = OffsetDateTime.of(2024, 2, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        // Compra dentro del rango
        crearCompra(oferta1, comprador1, new BigDecimal("25000.00"), fechaInicio.plusDays(1));

        // Compra fuera del rango (no debería aparecer)
        crearCompra(oferta2, comprador2, new BigDecimal("22000.00"), fechaFuera);

        entityManager.flush();
        entityManager.clear();

        // When
        ReporteTop5Response response = reporteService.obtenerAutosMasVendidos(
                "2024-01-15", "2024-01-20", null);

        // Then
        assertNotNull(response);
        assertNotNull(response.getItems());
        
        @SuppressWarnings("unchecked")
        List<AutoMasVendidoDTO> items = (List<AutoMasVendidoDTO>) response.getItems();
        
        // Solo debería aparecer auto1
        assertEquals(1, items.size());
        assertEquals(auto1.getId(), items.get(0).getAutoId());
        assertEquals("2024-01-15", response.getFechaDesde());
        assertEquals("2024-01-20", response.getFechaHasta());
    }

    @Test
    @DisplayName("Obtener autos más vendidos con período mes")
    void obtenerAutosMasVendidos_ConPeriodoMes_DeberiaCalcularFechasCorrectamente() {
        // Given: Crear compra en el mes actual
        OffsetDateTime fechaActual = OffsetDateTime.now().withHour(12).withMinute(0).withSecond(0);
        crearCompra(oferta1, comprador1, new BigDecimal("25000.00"), fechaActual);

        entityManager.flush();
        entityManager.clear();

        // When
        ReporteTop5Response response = reporteService.obtenerAutosMasVendidos(null, null, "mes");

        // Then
        assertNotNull(response);
        assertEquals("mes", response.getPeriodo());
        assertNotNull(response.getItems());
    }

    @Test
    @DisplayName("Obtener usuarios con más compras sin filtros")
    void obtenerUsuariosMasCompras_SinFiltros_DeberiaRetornarTop5() {
        // Given: Crear compras
        OffsetDateTime fechaBase = OffsetDateTime.now().withHour(12).withMinute(0).withSecond(0);

        // Comprador1: 3 compras
        crearCompra(oferta1, comprador1, new BigDecimal("25000.00"), fechaBase);
        crearCompra(oferta2, comprador1, new BigDecimal("22000.00"), fechaBase.plusDays(1));
        crearCompra(oferta3, comprador1, new BigDecimal("20000.00"), fechaBase.plusDays(2));

        // Comprador2: 1 compra
        crearCompra(oferta1, comprador2, new BigDecimal("25000.00"), fechaBase);

        entityManager.flush();
        entityManager.clear();

        // When
        ReporteTop5Response response = reporteService.obtenerUsuariosMasCompras(null, null, null);

        // Then
        assertNotNull(response);
        assertNotNull(response.getItems());
        
        @SuppressWarnings("unchecked")
        List<UsuarioMasComprasDTO> items = (List<UsuarioMasComprasDTO>) response.getItems();
        
        assertTrue(items.size() > 0);
        UsuarioMasComprasDTO topUsuario = items.get(0);
        assertEquals(comprador1.getId(), topUsuario.getUsuarioId());
        assertEquals(3L, topUsuario.getCantidadCompras());
    }

    @Test
    @DisplayName("Obtener usuarios con más compras con filtros de fecha")
    void obtenerUsuariosMasCompras_ConFiltrosFecha_DeberiaFiltrarCorrectamente() {
        // Given
        OffsetDateTime fechaInicio = OffsetDateTime.of(2024, 1, 15, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fechaFuera = OffsetDateTime.of(2024, 2, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        crearCompra(oferta1, comprador1, new BigDecimal("25000.00"), fechaInicio.plusDays(1));
        crearCompra(oferta2, comprador2, new BigDecimal("22000.00"), fechaFuera);

        entityManager.flush();
        entityManager.clear();

        // When
        ReporteTop5Response response = reporteService.obtenerUsuariosMasCompras(
                "2024-01-15", "2024-01-20", null);

        // Then
        assertNotNull(response);
        
        @SuppressWarnings("unchecked")
        List<UsuarioMasComprasDTO> items = (List<UsuarioMasComprasDTO>) response.getItems();
        
        assertEquals(1, items.size());
        assertEquals(comprador1.getId(), items.get(0).getUsuarioId());
    }

    @Test
    @DisplayName("Obtener autos mejores rankeados")
    void obtenerAutosMejoresRankeados_DeberiaRetornarTop5() {
        // Given: Crear reseñas
        crearResena(comprador1, auto1, 9, "Excelente auto");
        crearResena(comprador2, auto1, 8, "Muy bueno");
        crearResena(comprador1, auto2, 7, "Buen auto");
        crearResena(comprador2, auto2, 6, "Regular");

        entityManager.flush();
        entityManager.clear();

        // When
        ReporteTop5Response response = reporteService.obtenerAutosMejoresRankeados(null, null, null);

        // Then
        assertNotNull(response);
        assertNotNull(response.getItems());
        
        @SuppressWarnings("unchecked")
        List<AutoRankingDTO> items = (List<AutoRankingDTO>) response.getItems();
        
        assertTrue(items.size() > 0);
        // Auto1 debería tener mejor promedio (8.5) que auto2 (6.5)
        AutoRankingDTO topAuto = items.get(0);
        assertEquals(auto1.getId(), topAuto.getAutoId());
        assertTrue(topAuto.getPromedioPuntaje() > 8.0);
    }

    @Test
    @DisplayName("Obtener agencias con más ventas sin filtros")
    void obtenerAgenciasMasVentas_SinFiltros_DeberiaRetornarTop5() {
        // Given: Crear compras
        OffsetDateTime fechaBase = OffsetDateTime.now().withHour(12).withMinute(0).withSecond(0);

        // Concesionaria1: 3 compras (2 ofertas diferentes)
        crearCompra(oferta1, comprador1, new BigDecimal("25000.00"), fechaBase);
        crearCompra(oferta1, comprador2, new BigDecimal("25000.00"), fechaBase.plusDays(1));
        crearCompra(oferta2, comprador1, new BigDecimal("22000.00"), fechaBase.plusDays(2));

        // Concesionaria2: 1 compra
        crearCompra(oferta3, comprador2, new BigDecimal("20000.00"), fechaBase);

        entityManager.flush();
        entityManager.clear();

        // When
        ReporteTop5Response response = reporteService.obtenerAgenciasMasVentas(null, null, null);

        // Then
        assertNotNull(response);
        assertNotNull(response.getItems());
        
        @SuppressWarnings("unchecked")
        List<AgenciaMasVentasDTO> items = (List<AgenciaMasVentasDTO>) response.getItems();
        
        assertTrue(items.size() > 0);
        AgenciaMasVentasDTO topAgencia = items.get(0);
        assertEquals(concesionaria1.getId(), topAgencia.getConcesionariaId());
        assertEquals(3L, topAgencia.getCantidadVentas());
    }

    @Test
    @DisplayName("Obtener agencias con más ventas con filtros de fecha")
    void obtenerAgenciasMasVentas_ConFiltrosFecha_DeberiaFiltrarCorrectamente() {
        // Given
        OffsetDateTime fechaInicio = OffsetDateTime.of(2024, 1, 15, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fechaFuera = OffsetDateTime.of(2024, 2, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        crearCompra(oferta1, comprador1, new BigDecimal("25000.00"), fechaInicio.plusDays(1));
        crearCompra(oferta2, comprador2, new BigDecimal("22000.00"), fechaFuera);

        entityManager.flush();
        entityManager.clear();

        // When
        ReporteTop5Response response = reporteService.obtenerAgenciasMasVentas(
                "2024-01-15", "2024-01-20", null);

        // Then
        assertNotNull(response);
        
        @SuppressWarnings("unchecked")
        List<AgenciaMasVentasDTO> items = (List<AgenciaMasVentasDTO>) response.getItems();
        
        assertEquals(1, items.size());
        assertEquals(concesionaria1.getId(), items.get(0).getConcesionariaId());
    }

    @Test
    @DisplayName("Obtener autos más vendidos con período trimestre")
    void obtenerAutosMasVendidos_ConPeriodoTrimestre_DeberiaCalcularFechasCorrectamente() {
        // When
        ReporteTop5Response response = reporteService.obtenerAutosMasVendidos(null, null, "trimestre");

        // Then
        assertNotNull(response);
        assertEquals("trimestre", response.getPeriodo());
    }

    @Test
    @DisplayName("Obtener autos más vendidos con período año")
    void obtenerAutosMasVendidos_ConPeriodoAño_DeberiaCalcularFechasCorrectamente() {
        // When
        ReporteTop5Response response = reporteService.obtenerAutosMasVendidos(null, null, "año");

        // Then
        assertNotNull(response);
        assertEquals("año", response.getPeriodo());
    }

    // Métodos auxiliares
    private Compra crearCompra(OfertaAuto oferta, UsuarioComprador comprador, 
                                BigDecimal precioUnitario, OffsetDateTime fechaCompra) {
        Compra compra = Compra.builder()
                .oferta(oferta)
                .comprador(comprador)
                .precioUnitario(precioUnitario)
                .cantidad(1)
                .total(precioUnitario)
                .fechaCompra(fechaCompra)
                .build();
        return compraRepository.save(compra);
    }

    private Resena crearResena(UsuarioComprador usuario, Auto auto, Integer rating, String comentario) {
        Resena resena = Resena.builder()
                .usuario(usuario)
                .auto(auto)
                .rating(rating)
                .comentario(comentario)
                .build();
        return resenaRepository.save(resena);
    }
}

