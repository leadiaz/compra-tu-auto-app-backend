package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.CredencialesInvalidasException;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.usuario.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import({UsuarioService.class, UsuarioServiceTest.TestConfig.class})
class UsuarioServiceTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    // ========== Tests para crearUsuario ==========

    @Test
    @DisplayName("Crear usuario de tipo COMPRADOR")
    void crearUsuario_TipoComprador_DeberiaCrearUsuarioComprador() {
        // Act
        Usuario resultado = usuarioService.crearUsuario(
                "nuevo@test.com",
                "password123",
                "Nuevo",
                "Usuario",
                "COMPRADOR"
        );

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertInstanceOf(UsuarioComprador.class, resultado);
        assertEquals("nuevo@test.com", resultado.getEmail());
        assertEquals("Nuevo", resultado.getNombre());
        assertEquals("Usuario", resultado.getApellido());
        assertTrue(resultado.getActivo());
        assertNotNull(resultado.getPassword());

        // Verificar que se guardó en la base de datos
        Optional<Usuario> usuarioGuardado = usuarioRepository.findById(resultado.getId());
        assertTrue(usuarioGuardado.isPresent());
        assertInstanceOf(UsuarioComprador.class, usuarioGuardado.get());
    }

    @Test
    @DisplayName("Crear usuario de tipo ADMIN")
    void crearUsuario_TipoAdmin_DeberiaCrearUsuarioAdmin() {
        // Act
        Usuario resultado = usuarioService.crearUsuario(
                "admin@test.com",
                "password123",
                "Admin",
                "Sistema",
                "ADMIN"
        );

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertInstanceOf(UsuarioAdmin.class, resultado);
        assertEquals("admin@test.com", resultado.getEmail());

        // Verificar que se guardó en la base de datos
        Optional<Usuario> usuarioGuardado = usuarioRepository.findById(resultado.getId());
        assertTrue(usuarioGuardado.isPresent());
        assertInstanceOf(UsuarioAdmin.class, usuarioGuardado.get());
    }

    @Test
    @DisplayName("Crear usuario de tipo CONCESIONARIA")
    void crearUsuario_TipoConcesionaria_DeberiaCrearUsuarioConcesionaria() {
        // Act
        Usuario resultado = usuarioService.crearUsuario(
                "concesionaria@test.com",
                "password123",
                "Concesionaria",
                "Test",
                "CONCESIONARIA"
        );

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertInstanceOf(UsuarioConcesionaria.class, resultado);
        assertEquals("concesionaria@test.com", resultado.getEmail());

        // Verificar que se guardó en la base de datos
        Optional<Usuario> usuarioGuardado = usuarioRepository.findById(resultado.getId());
        assertTrue(usuarioGuardado.isPresent());
        assertInstanceOf(UsuarioConcesionaria.class, usuarioGuardado.get());
    }

    @Test
    @DisplayName("Crear usuario sin tipo especificado debe crear COMPRADOR por defecto")
    void crearUsuario_TipoNull_DeberiaCrearUsuarioCompradorPorDefecto() {
        // Act
        Usuario resultado = usuarioService.crearUsuario(
                "nuevo@test.com",
                "password123",
                "Nuevo",
                "Usuario",
                null
        );

        // Assert
        assertNotNull(resultado);
        assertInstanceOf(UsuarioComprador.class, resultado);
    }

    @Test
    @DisplayName("Crear usuario con email duplicado debe lanzar excepción")
    void crearUsuario_EmailDuplicado_DeberiaLanzarExcepcion() {
        // Arrange - Crear un usuario existente
        usuarioService.crearUsuario(
                "existente@test.com",
                "password123",
                "Existente",
                "Usuario",
                "COMPRADOR"
        );

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(
                    "existente@test.com",
                    "password123",
                    "Nuevo",
                    "Usuario",
                    "COMPRADOR"
            );
        });
        assertTrue(exception.getMessage().contains("ya existe"));
    }

    // ========== Tests para obtenerTodosLosUsuarios ==========

    @Test
    @DisplayName("Obtener todos los usuarios")
    void obtenerTodosLosUsuarios_DeberiaRetornarLista() {
        // Arrange - Crear usuarios en la base de datos
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");
        usuarioService.crearUsuario("admin1@test.com", "pass", "Admin", "Sistema", "ADMIN");

        // Act
        List<Usuario> resultado = usuarioService.obtenerTodosLosUsuarios();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 2);
    }

    // ========== Tests para obtenerUsuarioPorId ==========

    @Test
    @DisplayName("Obtener usuario por ID cuando existe")
    void obtenerUsuarioPorId_UsuarioExiste_DeberiaRetornarUsuario() {
        // Arrange - Crear usuario en la base de datos
        Usuario usuarioCreado = usuarioService.crearUsuario(
                "comprador@test.com",
                "password123",
                "Juan",
                "Pérez",
                "COMPRADOR"
        );

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(usuarioCreado.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioCreado.getId(), resultado.get().getId());
        assertEquals("comprador@test.com", resultado.get().getEmail());
    }

    @Test
    @DisplayName("Obtener usuario por ID cuando no existe")
    void obtenerUsuarioPorId_UsuarioNoExiste_DeberiaRetornarEmpty() {
        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(999L);

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // ========== Tests para obtenerUsuarioPorEmail ==========

    @Test
    @DisplayName("Obtener usuario por email cuando existe")
    void obtenerUsuarioPorEmail_UsuarioExiste_DeberiaRetornarUsuario() {
        // Arrange - Crear usuario en la base de datos
        Usuario usuarioCreado = usuarioService.crearUsuario(
                "comprador@test.com",
                "password123",
                "Juan",
                "Pérez",
                "COMPRADOR"
        );

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorEmail("comprador@test.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioCreado.getId(), resultado.get().getId());
        assertEquals("comprador@test.com", resultado.get().getEmail());
    }

    @Test
    @DisplayName("Obtener usuario por email cuando no existe")
    void obtenerUsuarioPorEmail_UsuarioNoExiste_DeberiaRetornarEmpty() {
        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorEmail("inexistente@test.com");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    // ========== Tests para obtenerUsuariosPorTipo ==========

    @Test
    @DisplayName("Obtener usuarios por tipo COMPRADOR")
    void obtenerUsuariosPorTipo_Comprador_DeberiaRetornarCompradores() {
        // Arrange - Crear usuarios en la base de datos
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");
        usuarioService.crearUsuario("comprador2@test.com", "pass", "María", "García", "COMPRADOR");
        usuarioService.crearUsuario("admin1@test.com", "pass", "Admin", "Sistema", "ADMIN");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosPorTipo("COMPRADOR");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 2);
        resultado.forEach(usuario -> assertInstanceOf(UsuarioComprador.class, usuario));
    }

    @Test
    @DisplayName("Obtener usuarios por tipo ADMIN")
    void obtenerUsuariosPorTipo_Admin_DeberiaRetornarAdmins() {
        // Arrange - Crear usuarios en la base de datos
        usuarioService.crearUsuario("admin1@test.com", "pass", "Admin1", "Sistema", "ADMIN");
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosPorTipo("ADMIN");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 1);
        resultado.forEach(usuario -> assertInstanceOf(UsuarioAdmin.class, usuario));
    }

    @Test
    @DisplayName("Obtener usuarios por tipo CONCESIONARIA")
    void obtenerUsuariosPorTipo_Concesionaria_DeberiaRetornarConcesionarias() {
        // Arrange - Crear usuarios en la base de datos
        usuarioService.crearUsuario("concesionaria1@test.com", "pass", "Concesionaria1", "Test", "CONCESIONARIA");
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosPorTipo("CONCESIONARIA");

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 1);
        resultado.forEach(usuario -> assertInstanceOf(UsuarioConcesionaria.class, usuario));
    }

    @Test
    @DisplayName("Obtener usuarios por tipo inválido debe lanzar excepción")
    void obtenerUsuariosPorTipo_TipoInvalido_DeberiaLanzarExcepcion() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.obtenerUsuariosPorTipo("INVALIDO");
        });
        assertTrue(exception.getMessage().contains("no soportado"));
    }

    @Test
    @DisplayName("Obtener usuarios por tipo null debe retornar COMPRADOR por defecto")
    void obtenerUsuariosPorTipo_TipoNull_DeberiaRetornarCompradores() {
        // Arrange - Crear usuarios en la base de datos
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");
        usuarioService.crearUsuario("admin1@test.com", "pass", "Admin", "Sistema", "ADMIN");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosPorTipo(null);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 1);
        resultado.forEach(usuario -> assertInstanceOf(UsuarioComprador.class, usuario));
    }

    // ========== Tests para autenticar ==========

    @Test
    @DisplayName("Autenticar con credenciales válidas")
    void autenticar_CredencialesValidas_DeberiaRetornarUsuario() {
        // Arrange - Crear usuario en la base de datos
        String email = "comprador@test.com";
        String password = "password123";
        Usuario usuarioCreado = usuarioService.crearUsuario(email, password, "Juan", "Pérez", "COMPRADOR");

        // Act
        Usuario resultado = usuarioService.autenticar(email, password);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioCreado.getId(), resultado.getId());
        assertEquals(email, resultado.getEmail());
    }

    @Test
    @DisplayName("Autenticar con email inexistente debe lanzar excepción")
    void autenticar_EmailInexistente_DeberiaLanzarExcepcion() {
        // Act & Assert
        assertThrows(CredencialesInvalidasException.class, () -> {
            usuarioService.autenticar("inexistente@test.com", "password123");
        });
    }

    @Test
    @DisplayName("Autenticar con password incorrecta debe lanzar excepción")
    void autenticar_PasswordIncorrecta_DeberiaLanzarExcepcion() {
        // Arrange - Crear usuario en la base de datos
        String email = "comprador@test.com";
        String password = "password123";
        usuarioService.crearUsuario(email, password, "Juan", "Pérez", "COMPRADOR");

        // Act & Assert
        assertThrows(CredencialesInvalidasException.class, () -> {
            usuarioService.autenticar(email, "passwordIncorrecta");
        });
    }


    // ========== Tests para obtenerUsuariosConFiltros ==========

    @Test
    @DisplayName("Obtener usuarios sin filtros debe retornar todos")
    void obtenerUsuariosConFiltros_SinFiltros_DeberiaRetornarTodos() {
        // Arrange - Crear usuarios en la base de datos
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");
        usuarioService.crearUsuario("admin1@test.com", "pass", "Admin", "Sistema", "ADMIN");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosConFiltros(null, null);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 2);
    }

    @Test
    @DisplayName("Obtener usuarios filtrando por tipo COMPRADOR")
    void obtenerUsuariosConFiltros_FiltrarPorTipoComprador_DeberiaRetornarSoloCompradores() {
        // Arrange - Crear usuarios en la base de datos
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");
        usuarioService.crearUsuario("admin1@test.com", "pass", "Admin", "Sistema", "ADMIN");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosConFiltros("COMPRADOR", null);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 1);
        resultado.forEach(usuario -> assertInstanceOf(UsuarioComprador.class, usuario));
    }

    @Test
    @DisplayName("Obtener usuarios CONCESIONARIA sin concesionaria asignada")
    void obtenerUsuariosConFiltros_SinConcesionaria_DeberiaRetornarConcesionariasSinAsignar() {
        // Arrange - Crear usuario CONCESIONARIA sin concesionaria asignada
        usuarioService.crearUsuario("concesionaria1@test.com", "pass", "Concesionaria", "Test", "CONCESIONARIA");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosConFiltros(null, true);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 1);
        resultado.forEach(usuario -> {
            assertInstanceOf(UsuarioConcesionaria.class, usuario);
            UsuarioConcesionaria uc = (UsuarioConcesionaria) usuario;
            assertNull(uc.getConcesionaria());
        });
    }

    @Test
    @DisplayName("Obtener usuarios sin concesionaria con tipo COMPRADOR debe retornar lista vacía")
    void obtenerUsuariosConFiltros_SinConcesionariaConTipoComprador_DeberiaRetornarListaVacia() {
        // Arrange - Crear usuario COMPRADOR
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosConFiltros("COMPRADOR", true);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Obtener usuarios sin concesionaria con tipo CONCESIONARIA debe retornar concesionarias sin asignar")
    void obtenerUsuariosConFiltros_SinConcesionariaConTipoConcesionaria_DeberiaRetornarConcesionariasSinAsignar() {
        // Arrange - Crear usuario CONCESIONARIA sin concesionaria asignada
        usuarioService.crearUsuario("concesionaria1@test.com", "pass", "Concesionaria", "Test", "CONCESIONARIA");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosConFiltros("CONCESIONARIA", true);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 1);
        resultado.forEach(usuario -> assertInstanceOf(UsuarioConcesionaria.class, usuario));
    }

    @Test
    @DisplayName("Obtener usuarios con tipo en minúsculas debe funcionar")
    void obtenerUsuariosConFiltros_TipoConMinusculas_DeberiaFuncionar() {
        // Arrange - Crear usuarios en la base de datos
        usuarioService.crearUsuario("comprador1@test.com", "pass", "Juan", "Pérez", "COMPRADOR");

        // Act
        List<Usuario> resultado = usuarioService.obtenerUsuariosConFiltros("comprador", null);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() >= 1);
        resultado.forEach(usuario -> assertInstanceOf(UsuarioComprador.class, usuario));
    }

    // ========== Tests para obtenerUsuarioAutenticado ==========

    @Test
    @DisplayName("Obtener usuario autenticado cuando está autenticado")
    void obtenerUsuarioAutenticado_UsuarioAutenticado_DeberiaRetornarUsuario() {
        // Arrange - Crear usuario en la base de datos
        String email = "comprador@test.com";
        Usuario usuarioCreado = usuarioService.crearUsuario(email, "password123", "Juan", "Pérez", "COMPRADOR");

        // Configurar SecurityContext
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioAutenticado();

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioCreado.getId(), resultado.get().getId());
        assertEquals(email, resultado.get().getEmail());

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Obtener usuario autenticado cuando no está autenticado")
    void obtenerUsuarioAutenticado_NoAutenticado_DeberiaRetornarEmpty() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioAutenticado();

        // Assert
        assertTrue(resultado.isEmpty());

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Obtener usuario autenticado cuando el usuario no se encuentra en la BD")
    void obtenerUsuarioAutenticado_UsuarioNoEncontrado_DeberiaRetornarEmpty() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("inexistente@test.com");

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioAutenticado();

        // Assert
        assertTrue(resultado.isEmpty());

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    // ========== Tests para obtenerMenuUsuarioAutenticado ==========

    @Test
    @DisplayName("Obtener menú de usuario autenticado")
    void obtenerMenuUsuarioAutenticado_UsuarioAutenticado_DeberiaRetornarMenu() {
        // Arrange - Crear usuario en la base de datos
        String email = "comprador@test.com";
        usuarioService.crearUsuario(email, "password123", "Juan", "Pérez", "COMPRADOR");

        // Configurar SecurityContext
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);

        // Act
        Optional<List<ar.edu.unq.pdss22025.models.usuario.menu.MenuItem>> resultado = 
                usuarioService.obtenerMenuUsuarioAutenticado();

        // Assert
        assertTrue(resultado.isPresent());
        assertNotNull(resultado.get());

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Obtener menú cuando no está autenticado")
    void obtenerMenuUsuarioAutenticado_NoAutenticado_DeberiaRetornarEmpty() {
        // Arrange
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        Optional<List<ar.edu.unq.pdss22025.models.usuario.menu.MenuItem>> resultado = 
                usuarioService.obtenerMenuUsuarioAutenticado();

        // Assert
        assertTrue(resultado.isEmpty());

        // Cleanup
        SecurityContextHolder.clearContext();
    }
}

