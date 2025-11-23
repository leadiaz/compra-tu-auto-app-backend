package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.models.Compra;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.models.dto.CrearCompraRequest;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import ar.edu.unq.pdss22025.repositories.ConcesionariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        // Deshabilitar Flyway en tests y generar esquema con Hibernate
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(CompraService.class)
class CompraServiceTest {

    @Autowired
    private CompraService compraService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OfertaAutoRepository ofertaAutoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private ConcesionariaRepository concesionariaRepository;

    private UsuarioComprador comprador;
    private OfertaAuto oferta;

    @BeforeEach
    void setUp() {
        // Crear usuario comprador
        comprador = new UsuarioComprador();
        comprador.setEmail("comprador@test.com");
        comprador.setPassword("password");
        comprador.setNombre("Juan");
        comprador.setApellido("Pérez");
        comprador.setActivo(true);
        comprador = usuarioRepository.save(comprador);

        // Crear concesionaria
        Concesionaria concesionaria = Concesionaria.builder()
                .nombre("Concesionaria Test")
                .cuit("20-12345678-9")
                .activa(true)
                .build();
        concesionaria = concesionariaRepository.save(concesionaria);

        // Crear auto
        Auto auto = Auto.builder()
                .marca("Toyota")
                .modelo("Corolla")
                .anioModelo(2023)
                .build();
        auto = autoRepository.save(auto);

        // Crear oferta con stock
        oferta = OfertaAuto.builder()
                .concesionaria(concesionaria)
                .auto(auto)
                .stock(5)
                .precioActual(new BigDecimal("25000.00"))
                .moneda("USD")
                .build();
        oferta = ofertaAutoRepository.save(oferta);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void crearCompra_ConOfertaValida_DeberiaCrearCompra() {
        // Given
        CrearCompraRequest request = new CrearCompraRequest();
        request.setOfertaId(oferta.getId());
        request.setCompradorId(comprador.getId());
        request.setPrecioCerrado(new BigDecimal("24000.00"));

        // When
        Compra compra = compraService.crear(request);

        // Then
        assertNotNull(compra);
        assertNotNull(compra.getId());
        assertEquals(oferta.getId(), compra.getOferta().getId());
        assertEquals(comprador.getId(), compra.getComprador().getId());
        assertEquals(new BigDecimal("24000.00"), compra.getPrecioUnitario());
        assertEquals(Integer.valueOf(1), compra.getCantidad());
        assertEquals(new BigDecimal("24000.00"), compra.getTotal());

        // Verificar que el stock se redujo
        OfertaAuto ofertaActualizada = ofertaAutoRepository.findById(oferta.getId()).orElseThrow();
        assertEquals(Integer.valueOf(4), ofertaActualizada.getStock());
    }

    @Test
    void crearCompra_ConOfertaInexistente_DeberiaLanzarExcepcion() {
        // Given
        CrearCompraRequest request = new CrearCompraRequest();
        request.setOfertaId(999L); // ID inexistente
        request.setCompradorId(comprador.getId());

        // When & Then
        EntidadNoEncontradaException exception = assertThrows(
                EntidadNoEncontradaException.class,
                () -> compraService.crear(request)
        );
        assertEquals("Oferta no encontrada", exception.getMessage());
    }

    @Test
    void crearCompra_ConCompradorInexistente_DeberiaLanzarExcepcion() {
        // Given
        CrearCompraRequest request = new CrearCompraRequest();
        request.setOfertaId(oferta.getId());
        request.setCompradorId(999L); // ID inexistente

        // When & Then
        EntidadNoEncontradaException exception = assertThrows(
                EntidadNoEncontradaException.class,
                () -> compraService.crear(request)
        );
        assertEquals("Comprador no encontrado", exception.getMessage());
    }

    @Test
    void crearCompra_ConStockInsuficiente_DeberiaLanzarExcepcion() {
        // Given
        oferta.setStock(0);
        ofertaAutoRepository.save(oferta);

        CrearCompraRequest request = new CrearCompraRequest();
        request.setOfertaId(oferta.getId());
        request.setCompradorId(comprador.getId());

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> compraService.crear(request)
        );
        assertEquals("Oferta sin stock disponible", exception.getMessage());
    }

    @Test
    void listarPorCompradorId_DeberiaRetornarComprasDelComprador() {
        // Given
        CrearCompraRequest request = new CrearCompraRequest();
        request.setOfertaId(oferta.getId());
        request.setCompradorId(comprador.getId());
        compraService.crear(request);

        // When
        var compras = compraService.listarPorCompradorId(comprador.getId());

        // Then
        assertNotNull(compras);
        assertEquals(1, compras.size());
        assertEquals(comprador.getId(), compras.get(0).getComprador().getId());
    }
}