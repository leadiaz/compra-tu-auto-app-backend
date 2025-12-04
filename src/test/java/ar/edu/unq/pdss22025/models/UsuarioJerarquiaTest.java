package ar.edu.unq.pdss22025.models;

import ar.edu.unq.pdss22025.models.usuario.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioJerarquiaTest {
    @Test
    @DisplayName("UsuarioAdmin: getters y setters")
    void usuarioAdminGettersSetters() {
        UsuarioAdmin admin = new UsuarioAdmin();
        admin.setEmail("a@a.com");
        admin.setPassword("pwd");
        admin.setNombre("Admin");
        admin.setApellido("Root");
        admin.setActivo(true);
        assertEquals("a@a.com", admin.getEmail());
        assertEquals("pwd", admin.getPassword());
        assertEquals("Admin", admin.getNombre());
        assertEquals("Root", admin.getApellido());
        assertTrue(admin.getActivo());
    }

    @Test
    @DisplayName("UsuarioConcesionaria: getters y setters")
    void usuarioConcesionariaGettersSetters() {
        UsuarioConcesionaria conc = new UsuarioConcesionaria();
        conc.setEmail("c@c.com");
        conc.setPassword("pwd");
        conc.setNombre("Conc");
        conc.setApellido("SA");
        conc.setActivo(false);
        assertEquals("c@c.com", conc.getEmail());
        assertEquals("pwd", conc.getPassword());
        assertEquals("Conc", conc.getNombre());
        assertEquals("SA", conc.getApellido());
        assertFalse(conc.getActivo());
    }

    @Test
    @DisplayName("UsuarioComprador: getters y setters")
    void usuarioCompradorGettersSetters() {
        UsuarioComprador comp = new UsuarioComprador();
        comp.setEmail("u@u.com");
        comp.setPassword("pwd");
        comp.setNombre("User");
        comp.setApellido("Buyer");
        comp.setActivo(true);
        assertEquals("u@u.com", comp.getEmail());
        assertEquals("pwd", comp.getPassword());
        assertEquals("User", comp.getNombre());
        assertEquals("Buyer", comp.getApellido());
        assertTrue(comp.getActivo());
    }
    @Test
    @DisplayName("Test de falla")
    void failureTest() {
        assertEquals(1, 2, "This test is designed to fail");
    }
}
