package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.Usuario;
import ar.edu.unq.pdss22025.models.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.UsuarioComprador;
import ar.edu.unq.pdss22025.models.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.repositories.FavoritoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        // Deshabilitar Flyway y dejar que Hibernate genere el schema en H2 para tests
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(FavoritoService.class)
class FavoritoServiceTest {

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private OfertaAutoRepository ofertaAutoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private ConcesionariaRepository concesionariaRepository;

    private UsuarioComprador usuario;
    private OfertaAuto oferta1;
    private OfertaAuto oferta2;

    @BeforeEach
    void setUp() {
        // Crear usuario comprador
        usuario = new UsuarioComprador();
        usuario.setEmail("usuario@test.com");
        usuario.setPassword("password");
        usuario.setNombre("Ana");
        usuario.setApellido("García");
        usuario.setActivo(true);
        usuario = (UsuarioComprador) usuarioRepository.save(usuario);

        // Crear concesionaria
        Concesionaria concesionaria = Concesionaria.builder()
                .nombre("Concesionaria Test")
                .cuit("20-12345678-9")
                .activa(true)
                .build();
        concesionaria = concesionariaRepository.save(concesionaria);

        // Crear autos
        Auto auto1 = Auto.builder()
                .marca("Toyota")
                .modelo("Corolla")
                .anioModelo(2023)
                .build();
        auto1 = autoRepository.save(auto1);

        Auto auto2 = Auto.builder()
                .marca("Honda")
                .modelo("Civic")
                .anioModelo(2023)
                .build();
        auto2 = autoRepository.save(auto2);

        // Crear ofertas
        oferta1 = OfertaAuto.builder()
                .concesionaria(concesionaria)
                .auto(auto1)
                .stock(5)
                .precioActual(new BigDecimal("25000.00"))
                .moneda("USD")
                .build();
        oferta1 = ofertaAutoRepository.save(oferta1);

        oferta2 = OfertaAuto.builder()
                .concesionaria(concesionaria)
                .auto(auto2)
                .stock(3)
                .precioActual(new BigDecimal("28000.00"))
                .moneda("USD")
                .build();
        oferta2 = ofertaAutoRepository.save(oferta2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void definirFavorito_ConUsuarioYOfertaValidos_DeberiaCrearFavorito() {
        // When
        Favorito favorito = favoritoService.definirFavorito(usuario.getId(), oferta1.getId());

        // Then
        assertNotNull(favorito);
        assertNotNull(favorito.getId());
        assertEquals(usuario.getId(), favorito.getUsuario().getId());
        assertEquals(oferta1.getId(), favorito.getOferta().getId());

        // Verificar que se puede obtener
        Optional<Favorito> favoritoOpt = favoritoService.obtenerPorUsuario(usuario.getId());
        assertTrue(favoritoOpt.isPresent());
        assertEquals(oferta1.getId(), favoritoOpt.get().getOferta().getId());
    }

    @Test
    void definirFavorito_ReemplazarFavoritoExistente_DeberiaActualizarFavorito() {
        // Given - Crear favorito inicial
        Favorito favoritoInicial = favoritoService.definirFavorito(usuario.getId(), oferta1.getId());
        
        // When - Reemplazar con nueva oferta
        Favorito favoritoNuevo = favoritoService.definirFavorito(usuario.getId(), oferta2.getId());

        // Then
        assertNotNull(favoritoNuevo);
        assertEquals(oferta2.getId(), favoritoNuevo.getOferta().getId());

        // Verificar que solo existe un favorito para el usuario
        Optional<Favorito> favoritoOpt = favoritoService.obtenerPorUsuario(usuario.getId());
        assertTrue(favoritoOpt.isPresent());
        assertEquals(oferta2.getId(), favoritoOpt.get().getOferta().getId());

        // Verificar que el favorito anterior fue eliminado
        assertFalse(favoritoRepository.existsById(favoritoInicial.getId()));

        // Verificar constraint UNIQUE en base de datos
        long count = favoritoRepository.count();
        assertEquals(1, count, "Debe haber exactamente un favorito en la base de datos");
    }

    @Test
    void definirFavorito_ConUsuarioInexistente_DeberiaLanzarExcepcion() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> favoritoService.definirFavorito(999L, oferta1.getId())
        );
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void definirFavorito_ConOfertaInexistente_DeberiaLanzarExcepcion() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> favoritoService.definirFavorito(usuario.getId(), 999L)
        );
        assertEquals("Oferta no encontrada", exception.getMessage());
    }

    @Test
    void obtenerPorUsuario_SinFavorito_DeberiaRetornarEmpty() {
        // When
        Optional<Favorito> favoritoOpt = favoritoService.obtenerPorUsuario(usuario.getId());

        // Then
        assertTrue(favoritoOpt.isEmpty());
    }

    @Test
    void obtenerPorUsuario_ConUsuarioInexistente_DeberiaLanzarExcepcion() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> favoritoService.obtenerPorUsuario(999L)
        );
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void definirFavorito_ConUsuarioAdmin_DeberiaLanzarUnprocessable() {
        Usuario admin = new UsuarioAdmin();
        admin.setEmail("admin@test.com");
        admin.setPassword("pwd");
        admin.setNombre("Admin");
        admin.setApellido("Root");
        admin.setActivo(true);
        admin = usuarioRepository.save(admin);
        final Long adminId = admin.getId();

        assertThrows(IllegalStateException.class, () -> favoritoService.definirFavorito(adminId, oferta1.getId()));
    }

    @Test
    void definirFavorito_ConUsuarioConcesionaria_DeberiaLanzarUnprocessable() {
        Usuario concesionario = new UsuarioConcesionaria();
        concesionario.setEmail("conc@test.com");
        concesionario.setPassword("pwd");
        concesionario.setNombre("Conc");
        concesionario.setApellido("SA");
        concesionario.setActivo(true);
        concesionario = usuarioRepository.save(concesionario);
        final Long concId = concesionario.getId();

        assertThrows(IllegalStateException.class, () -> favoritoService.definirFavorito(concId, oferta1.getId()));
    }
}