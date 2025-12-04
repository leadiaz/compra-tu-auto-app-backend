package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.exceptions.UsuarioNoValidoException;
import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.repositories.ConcesionariaRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(ConcesionariaService.class)
class ConcesionariaServiceTest {

    @Autowired
    private ConcesionariaService concesionariaService;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConcesionariaRepository concesionariaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioConcesionaria usuarioConcesionaria;
    private UsuarioComprador usuarioComprador;

    @BeforeEach
    void setUp() {
        // Crear usuario de tipo CONCESIONARIA
        usuarioConcesionaria = new UsuarioConcesionaria();
        usuarioConcesionaria.setEmail("concesionaria@test.com");
        usuarioConcesionaria.setPassword("password");
        usuarioConcesionaria.setNombre("Juan");
        usuarioConcesionaria.setApellido("Pérez");
        usuarioConcesionaria.setActivo(true);
        usuarioConcesionaria = usuarioRepository.save(usuarioConcesionaria);

        // Crear usuario de tipo COMPRADOR
        usuarioComprador = new UsuarioComprador();
        usuarioComprador.setEmail("comprador@test.com");
        usuarioComprador.setPassword("password");
        usuarioComprador.setNombre("María");
        usuarioComprador.setApellido("García");
        usuarioComprador.setActivo(true);
        usuarioComprador = usuarioRepository.save(usuarioComprador);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Crear concesionaria con datos válidos sin usuario")
    void crearConcesionaria_ConDatosValidosSinUsuario_DeberiaCrearConcesionaria() {
        // Given
        String nombre = "Concesionaria Test";
        String cuit = "20-12345678-9";
        String telefono = "011-1234-5678";
        String email = "test@concesionaria.com";
        String direccion = "Av. Test 123";

        // When
        Concesionaria concesionaria = concesionariaService.crearConcesionaria(
                nombre, cuit, telefono, email, direccion, null
        );

        // Then
        assertNotNull(concesionaria);
        assertNotNull(concesionaria.getId());
        assertEquals(nombre, concesionaria.getNombre());
        assertEquals(cuit, concesionaria.getCuit());
        assertEquals(telefono, concesionaria.getTelefono());
        assertEquals(email, concesionaria.getEmail());
        assertEquals(direccion, concesionaria.getDireccion());
        assertTrue(concesionaria.getActiva());
        assertNull(concesionaria.getUsuario());
    }

    @Test
    @DisplayName("Crear concesionaria con usuario válido de tipo CONCESIONARIA")
    void crearConcesionaria_ConUsuarioValido_DeberiaCrearConcesionariaYRelacionar() {
        // Given
        String nombre = "Concesionaria Test";
        String cuit = "20-12345678-9";

        // When
        Concesionaria concesionaria = concesionariaService.crearConcesionaria(
                nombre, cuit, null, null, null, usuarioConcesionaria.getId()
        );

        // Then
        assertNotNull(concesionaria);
        assertNotNull(concesionaria.getId());
        assertEquals(nombre, concesionaria.getNombre());
        assertEquals(cuit, concesionaria.getCuit());
        assertNotNull(concesionaria.getUsuario());
        assertEquals(usuarioConcesionaria.getId(), concesionaria.getUsuario().getId());

        // Verificar que el usuario tiene la concesionaria asociada
        UsuarioConcesionaria usuarioActualizado = (UsuarioConcesionaria) usuarioRepository.findById(usuarioConcesionaria.getId()).orElseThrow();
        assertNotNull(usuarioActualizado.getConcesionaria());
        assertEquals(concesionaria.getId(), usuarioActualizado.getConcesionaria().getId());
    }

    @Test
    @DisplayName("Crear concesionaria con CUIT duplicado debería lanzar excepción")
    void crearConcesionaria_ConCuitDuplicado_DeberiaLanzarExcepcion() {
        // Given
        Concesionaria existente = Concesionaria.builder()
                .nombre("Concesionaria Existente")
                .cuit("20-12345678-9")
                .activa(true)
                .build();
        concesionariaRepository.save(existente);

        // When & Then
        EntidadNoEncontradaException exception = assertThrows(
                EntidadNoEncontradaException.class,
                () -> concesionariaService.crearConcesionaria(
                        "Nueva Concesionaria",
                        "20-12345678-9",
                        null, null, null, null
                )
        );
        assertTrue(exception.getMessage().contains("Ya existe una concesionaria con el CUIT"));
    }

    @Test
    @DisplayName("Crear concesionaria con usuario inexistente debería lanzar excepción")
    void crearConcesionaria_ConUsuarioInexistente_DeberiaLanzarExcepcion() {
        // When & Then
        EntidadNoEncontradaException exception = assertThrows(
                EntidadNoEncontradaException.class,
                () -> concesionariaService.crearConcesionaria(
                        "Concesionaria Test",
                        "20-12345678-9",
                        null, null, null, 999L
                )
        );
        assertTrue(exception.getMessage().contains("Usuario con ID 999 no encontrado"));
    }

    @Test
    @DisplayName("Crear concesionaria con usuario que no es de tipo CONCESIONARIA debería lanzar excepción")
    void crearConcesionaria_ConUsuarioNoValido_DeberiaLanzarExcepcion() {
        // When & Then
        UsuarioNoValidoException exception = assertThrows(
                UsuarioNoValidoException.class,
                () -> concesionariaService.crearConcesionaria(
                        "Concesionaria Test",
                        "20-12345678-9",
                        null, null, null, usuarioComprador.getId()
                )
        );
        assertTrue(exception.getMessage().contains("no es de tipo CONCESIONARIA"));
    }

    @Test
    @DisplayName("Crear concesionaria con usuario que ya tiene una concesionaria asociada debería lanzar excepción")
    void crearConcesionaria_ConUsuarioYaAsociado_DeberiaLanzarExcepcion() {
        // Given - Crear una concesionaria y asociarla al usuario
        Concesionaria concesionariaExistente = Concesionaria.builder()
                .nombre("Concesionaria Existente")
                .cuit("20-11111111-1")
                .activa(true)
                .build();
        concesionariaExistente = concesionariaRepository.save(concesionariaExistente);
        usuarioConcesionaria.setConcesionaria(concesionariaExistente);
        usuarioRepository.save(usuarioConcesionaria);

        // When & Then
        UsuarioNoValidoException exception = assertThrows(
                UsuarioNoValidoException.class,
                () -> concesionariaService.crearConcesionaria(
                        "Nueva Concesionaria",
                        "20-12345678-9",
                        null, null, null, usuarioConcesionaria.getId()
                )
        );
        assertTrue(exception.getMessage().contains("ya tiene una concesionaria asociada"));
    }

    @Test
    @DisplayName("Crear concesionaria con solo campos requeridos")
    void crearConcesionaria_ConSoloCamposRequeridos_DeberiaCrearConcesionaria() {
        // Given
        String nombre = "Concesionaria Mínima";
        String cuit = "20-98765432-1";

        // When
        Concesionaria concesionaria = concesionariaService.crearConcesionaria(
                nombre, cuit, null, null, null, null
        );

        // Then
        assertNotNull(concesionaria);
        assertNotNull(concesionaria.getId());
        assertEquals(nombre, concesionaria.getNombre());
        assertEquals(cuit, concesionaria.getCuit());
        assertNull(concesionaria.getTelefono());
        assertNull(concesionaria.getEmail());
        assertNull(concesionaria.getDireccion());
        assertTrue(concesionaria.getActiva());
    }
}

