package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.exceptions.CredencialesInvalidasException;
import ar.edu.unq.pdss22025.models.usuario.Rol;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.services.UsuarioService;
import ar.edu.unq.pdss22025.services.JwtService;
import ar.edu.unq.pdss22025.services.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void login_ok() throws Exception {
        Usuario usuario = Mockito.mock(Usuario.class);
        Mockito.when(usuario.getId()).thenReturn(1L);
        Mockito.when(usuario.getEmail()).thenReturn("test@test.com");
        Mockito.when(usuario.getNombre()).thenReturn("Test");
        Mockito.when(usuario.getApellido()).thenReturn("User");
        Mockito.when(usuario.getCreatedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(usuario.getActivo()).thenReturn(true);

        Mockito.when(usuarioService.autenticar(Mockito.anyString(), Mockito.anyString())).thenReturn(usuario);
        Mockito.when(usuario.getRol()).thenReturn(Rol.COMPRADOR);
        Mockito.when(jwtService.generateToken(Mockito.anyLong(), Mockito.anyString(), Mockito.any(Rol.class))).thenReturn("test-token");

        String body = "{\"usuario\":\"test@test.com\",\"password\":\"pwd\"}";

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void login_unauthorized() throws Exception {
        Mockito.when(usuarioService.autenticar(Mockito.anyString(), Mockito.anyString())).thenThrow(new CredencialesInvalidasException());

        String body = "{\"usuario\":\"test@test.com\",\"password\":\"wrong\"}";

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized());
    }
}

