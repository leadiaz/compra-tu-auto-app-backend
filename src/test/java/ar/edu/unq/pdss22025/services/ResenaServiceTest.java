package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.UsuarioComprador;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.dto.CrearResenaRequest;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        // Deshabilitar Flyway en tests y generar esquema con Hibernate
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(ResenaService.class)
class ResenaServiceTest {

    @Autowired
    private ResenaService resenaService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AutoRepository autoRepository;

    private UsuarioComprador usuario1;
    private UsuarioComprador usuario2;
    private Auto auto;

    @BeforeEach
    void setUp() {
        // Crear usuarios
        usuario1 = new UsuarioComprador();
        usuario1.setEmail("usuario1@test.com");
        usuario1.setPassword("password");
        usuario1.setNombre("Ana");
        usuario1.setApellido("García");
        usuario1.setActivo(true);
        usuario1 = usuarioRepository.save(usuario1);

        usuario2 = new UsuarioComprador();
        usuario2.setEmail("usuario2@test.com");
        usuario2.setPassword("password");
        usuario2.setNombre("Carlos");
        usuario2.setApellido("López");
        usuario2.setActivo(true);
        usuario2 = usuarioRepository.save(usuario2);

        // Crear auto
        auto = Auto.builder()
                .marca("Toyota")
                .modelo("Corolla")
                .anioModelo(2023)
                .build();
        auto = autoRepository.save(auto);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void crear_ConDatosValidos_DeberiaCrearResena() {
        // Given
        CrearResenaRequest request = new CrearResenaRequest();
        request.setUsuarioId(usuario1.getId());
        request.setAutoId(auto.getId());
        request.setRating(5);
        request.setComentario("Excelente auto, muy recomendado");

        // When
        Resena resena = resenaService.crear(request);

        // Then
        assertNotNull(resena);
        assertNotNull(resena.getId());
        assertEquals(usuario1.getId(), resena.getUsuario().getId());
        assertEquals(auto.getId(), resena.getAuto().getId());
        assertEquals(Integer.valueOf(5), resena.getRating());
        assertEquals("Excelente auto, muy recomendado", resena.getComentario());
        assertNotNull(resena.getCreatedAt());
    }

    @Test
    void crear_SinComentario_DeberiaCrearResena() {
        // Given
        CrearResenaRequest request = new CrearResenaRequest();
        request.setUsuarioId(usuario1.getId());
        request.setAutoId(auto.getId());
        request.setRating(4);
        // Sin comentario (null)

        // When
        Resena resena = resenaService.crear(request);

        // Then
        assertNotNull(resena);
        assertEquals(Integer.valueOf(4), resena.getRating());
        assertNull(resena.getComentario());
    }

    @Test
    void crear_ConRatingInvalido_DeberiaLanzarExcepcion() {
        // Given
        CrearResenaRequest request = new CrearResenaRequest();
        request.setUsuarioId(usuario1.getId());
        request.setAutoId(auto.getId());
        request.setRating(6); // Rating fuera de rango

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> resenaService.crear(request)
        );
        assertEquals("Rating fuera de rango (1..5)", exception.getMessage());
    }

    @Test
    void crear_ConRatingCero_DeberiaLanzarExcepcion() {
        // Given
        CrearResenaRequest request = new CrearResenaRequest();
        request.setUsuarioId(usuario1.getId());
        request.setAutoId(auto.getId());
        request.setRating(0); // Rating fuera de rango

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> resenaService.crear(request)
        );
        assertEquals("Rating fuera de rango (1..5)", exception.getMessage());
    }

    @Test
    void crear_ConAutoInexistente_DeberiaLanzarExcepcion() {
        // Given
        CrearResenaRequest request = new CrearResenaRequest();
        request.setUsuarioId(usuario1.getId());
        request.setAutoId(999L); // Auto inexistente
        request.setRating(5);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> resenaService.crear(request)
        );
        assertEquals("Auto no encontrado", exception.getMessage());
    }

    @Test
    void crear_ConUsuarioInexistente_DeberiaLanzarExcepcion() {
        // Given
        CrearResenaRequest request = new CrearResenaRequest();
        request.setUsuarioId(999L); // Usuario inexistente
        request.setAutoId(auto.getId());
        request.setRating(5);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> resenaService.crear(request)
        );
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void listarPorAuto_DeberiaRetornarResenasOrdenadas() throws InterruptedException {
        // Given - Crear múltiples reseñas con diferentes timestamps
        CrearResenaRequest request1 = new CrearResenaRequest();
        request1.setUsuarioId(usuario1.getId());
        request1.setAutoId(auto.getId());
        request1.setRating(5);
        request1.setComentario("Primera reseña");
        
        resenaService.crear(request1);
        
        // Esperar un poco para asegurar diferente timestamp
        Thread.sleep(10);
        
        CrearResenaRequest request2 = new CrearResenaRequest();
        request2.setUsuarioId(usuario2.getId());
        request2.setAutoId(auto.getId());
        request2.setRating(4);
        request2.setComentario("Segunda reseña");
        
        resenaService.crear(request2);
        
        // When
        List<Resena> resenas = resenaService.listarPorAuto(auto.getId());

        // Then
        assertNotNull(resenas);
        assertEquals(2, resenas.size());
        
        // Verificar que están ordenadas por fecha de creación descendente (más reciente primero)
        assertTrue(resenas.get(0).getCreatedAt().isAfter(resenas.get(1).getCreatedAt()) ||
                  resenas.get(0).getCreatedAt().isEqual(resenas.get(1).getCreatedAt()));
        
        // La reseña más reciente debería ser la segunda
        assertEquals("Segunda reseña", resenas.get(0).getComentario());
        assertEquals("Primera reseña", resenas.get(1).getComentario());
    }

    @Test
    void listarPorAuto_ConAutoInexistente_DeberiaLanzarExcepcion() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> resenaService.listarPorAuto(999L)
        );
        assertEquals("Auto no encontrado", exception.getMessage());
    }

    @Test
    void listarPorAuto_SinResenas_DeberiaRetornarListaVacia() {
        // When
        List<Resena> resenas = resenaService.listarPorAuto(auto.getId());

        // Then
        assertNotNull(resenas);
        assertTrue(resenas.isEmpty());
    }
}