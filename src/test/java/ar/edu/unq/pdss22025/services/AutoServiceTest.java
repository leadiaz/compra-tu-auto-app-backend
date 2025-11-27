package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
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
@Import(AutoService.class)
class AutoServiceTest {

    @Autowired
    private AutoService autoService;

    @Autowired
    private AutoRepository autoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Crear auto con datos válidos")
    void crearAuto_ok() {
        Auto auto = autoService.crearAuto("Toyota", "Corolla", 2024);

        assertNotNull(auto.getId());
        assertEquals("Toyota", auto.getMarca());
        assertEquals("Corolla", auto.getModelo());
        assertEquals(2024, auto.getAnioModelo());
    }

    @Test
    @DisplayName("Crear auto duplicado debe lanzar excepción")
    void crearAuto_duplicado() {
        autoService.crearAuto("Toyota", "Corolla", 2024);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> autoService.crearAuto("Toyota", "Corolla", 2024)
        );

        assertTrue(exception.getMessage().contains("No puede existir más de un auto"));
    }

    @Test
    @DisplayName("Eliminar auto existente")
    void eliminarAuto_ok() {
        Auto auto = autoService.crearAuto("Ford", "Focus", 2022);
        autoService.eliminarAuto(auto.getId());

        assertFalse(autoRepository.findById(auto.getId()).isPresent());
    }

    @Test
    @DisplayName("Eliminar auto inexistente lanza excepción")
    void eliminarAuto_notFound() {
        EntidadNoEncontradaException exception = assertThrows(
                EntidadNoEncontradaException.class,
                () -> autoService.eliminarAuto(999L)
        );

        assertTrue(exception.getMessage().contains("Auto con ID 999 no encontrado"));
    }
}

