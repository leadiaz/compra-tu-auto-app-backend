package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.mapper.CompraMapper;
import ar.edu.unq.pdss22025.models.Compra;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.dto.CompraResponse;
import ar.edu.unq.pdss22025.services.CompraService;
import ar.edu.unq.pdss22025.services.UsuarioService;
import ar.edu.unq.pdss22025.services.JwtService;
import ar.edu.unq.pdss22025.services.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.models.usuario.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.usuario.menu.MenuItem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UsuarioController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private CompraService compraService;
    @MockitoBean
    private CompraMapper compraMapper;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void getComprasByUsuario_ok() throws Exception {
        Compra compra = new Compra();
        CompraResponse compraResponse = new CompraResponse();
        Mockito.when(compraService.listarPorCompradorId(1L)).thenReturn(List.of(compra));
        Mockito.when(compraMapper.toResponse(compra)).thenReturn(compraResponse);
        mockMvc.perform(
                get("/usuarios/1/compras")
        ).andExpect(status().isOk());
    }

    @Test
    void getComprasByUsuario_notFound() throws Exception {
        Mockito.when(compraService.listarPorCompradorId(1L)).thenReturn(Collections.emptyList());
        mockMvc.perform(
                get("/usuarios/1/compras")
        ).andExpect(status().isNotFound());
    }

    @Test
    void crearUsuario_ok() throws Exception {
        Usuario usuario = Mockito.mock(Usuario.class);
        Mockito.when(usuario.getId()).thenReturn(1L);
        Mockito.when(usuario.getEmail()).thenReturn("test@test.com");
        Mockito.when(usuario.getNombre()).thenReturn("Test");
        Mockito.when(usuario.getApellido()).thenReturn("User");
        Mockito.when(usuario.getCreatedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(usuario.getActivo()).thenReturn(true);
        Mockito.when(usuario.getRol()).thenReturn(ar.edu.unq.pdss22025.models.usuario.Rol.COMPRADOR);
        Mockito.when(usuarioService.crearUsuario(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(usuario);
        String body = "{\"email\":\"test@test.com\",\"password\":\"pwd\",\"nombre\":\"Test\",\"apellido\":\"User\",\"tipoUsuario\":\"COMPRADOR\"}";
        mockMvc.perform(
                post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        ).andExpect(status().isCreated());
    }

    @Test
    void crearUsuario_badRequest() throws Exception {
        Mockito.when(usuarioService.crearUsuario(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenThrow(new RuntimeException("Error"));
        String body = "{\"email\":\"test@test.com\",\"password\":\"pwd\",\"nombre\":\"Test\",\"apellido\":\"User\",\"tipoUsuario\":\"COMPRADOR\"}";
        mockMvc.perform(
                post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerTodosLosUsuarios_ok() throws Exception {
        Usuario usuario = Mockito.mock(Usuario.class);
        Mockito.when(usuario.getId()).thenReturn(1L);
        Mockito.when(usuario.getEmail()).thenReturn("test@test.com");
        Mockito.when(usuario.getNombre()).thenReturn("Test");
        Mockito.when(usuario.getApellido()).thenReturn("User");
        Mockito.when(usuario.getCreatedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(usuario.getActivo()).thenReturn(true);
        Mockito.when(usuario.getRol()).thenReturn(ar.edu.unq.pdss22025.models.usuario.Rol.COMPRADOR);
        Mockito.when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(List.of(usuario));
        mockMvc.perform(
                get("/usuarios")
        ).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "ADMIN")
    void obtenerUsuarioPorId_ok() throws Exception {
        Usuario usuario = Mockito.mock(Usuario.class);
        Mockito.when(usuario.getId()).thenReturn(1L);
        Mockito.when(usuario.getEmail()).thenReturn("test@test.com");
        Mockito.when(usuario.getNombre()).thenReturn("Test");
        Mockito.when(usuario.getApellido()).thenReturn("User");
        Mockito.when(usuario.getCreatedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(usuario.getActivo()).thenReturn(true);
        Mockito.when(usuario.getRol()).thenReturn(ar.edu.unq.pdss22025.models.usuario.Rol.COMPRADOR);
        Mockito.when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.of(usuario));
        mockMvc.perform(
                get("/usuarios/1")
        ).andExpect(status().isOk());
    }

    @Test
    void obtenerUsuarioPorId_notFound() throws Exception {
        Mockito.when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(Optional.empty());
        mockMvc.perform(
                get("/usuarios/1")
        ).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerUsuariosPorTipo_ok() throws Exception {
        Usuario usuario = Mockito.mock(Usuario.class);
        Mockito.when(usuario.getId()).thenReturn(1L);
        Mockito.when(usuario.getEmail()).thenReturn("test@test.com");
        Mockito.when(usuario.getNombre()).thenReturn("Test");
        Mockito.when(usuario.getApellido()).thenReturn("User");
        Mockito.when(usuario.getCreatedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(usuario.getActivo()).thenReturn(true);
        Mockito.when(usuario.getRol()).thenReturn(ar.edu.unq.pdss22025.models.usuario.Rol.COMPRADOR);
        Mockito.when(usuarioService.obtenerUsuariosPorTipo("COMPRADOR")).thenReturn(List.of(usuario));
        mockMvc.perform(
                get("/usuarios/por-tipo/COMPRADOR")
        ).andExpect(status().isOk());
    }

    @Test
    void obtenerUsuariosPorTipo_badRequest() throws Exception {
        Mockito.when(usuarioService.obtenerUsuariosPorTipo("COMPRADOR")).thenThrow(new RuntimeException("Error"));
        mockMvc.perform(
                get("/usuarios/por-tipo/COMPRADOR")
        ).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "COMPRADOR")
    void obtenerMiMenu_comprador_ok() throws Exception {
        UsuarioComprador usuario = new UsuarioComprador();
        usuario.setEmail("comprador@test.com");
        usuario.setId(1L);
        
        List<MenuItem> menuItems = usuario.getMenuItems();
        
        Mockito.when(usuarioService.obtenerMenuUsuarioAutenticado())
                .thenReturn(Optional.of(menuItems));
        
        mockMvc.perform(
                get("/usuarios/mi-menu")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items.length()").value(3))
        .andExpect(jsonPath("$.items[0].label").value("Ofertas"))
        .andExpect(jsonPath("$.items[0].icon").value("shopping-cart"))
        .andExpect(jsonPath("$.items[0].route").value("/dashboard/ofertas"))
        .andExpect(jsonPath("$.items[0].orden").value(1));
    }

    @Test
    @WithMockUser(roles = "CONCESIONARIA")
    void obtenerMiMenu_concesionaria_ok() throws Exception {
        UsuarioConcesionaria usuario = new UsuarioConcesionaria();
        usuario.setEmail("concesionaria@test.com");
        usuario.setId(2L);
        
        List<MenuItem> menuItems = usuario.getMenuItems();
        
        Mockito.when(usuarioService.obtenerMenuUsuarioAutenticado())
                .thenReturn(Optional.of(menuItems));
        
        mockMvc.perform(
                get("/usuarios/mi-menu")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items.length()").value(4))
        .andExpect(jsonPath("$.items[0].label").value("Mis Ofertas"))
        .andExpect(jsonPath("$.items[0].icon").value("store"))
        .andExpect(jsonPath("$.items[0].route").value("/ofertas"))
        .andExpect(jsonPath("$.items[0].orden").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerMiMenu_admin_ok() throws Exception {
        UsuarioAdmin usuario = new UsuarioAdmin();
        usuario.setEmail("admin@test.com");
        usuario.setId(3L);
        
        List<MenuItem> menuItems = usuario.getMenuItems();
        
        Mockito.when(usuarioService.obtenerMenuUsuarioAutenticado())
                .thenReturn(Optional.of(menuItems));
        
        mockMvc.perform(
                get("/usuarios/mi-menu")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items.length()").value(7))
        .andExpect(jsonPath("$.items[0].label").value("Usuarios"))
        .andExpect(jsonPath("$.items[0].icon").value("users"))
        .andExpect(jsonPath("$.items[0].route").value("/usuarios"))
        .andExpect(jsonPath("$.items[0].orden").value(1));
    }

    @Test
    void obtenerMiMenu_noAutenticado_unauthorized() throws Exception {
        Mockito.when(usuarioService.obtenerMenuUsuarioAutenticado())
                .thenReturn(Optional.empty());
        
        mockMvc.perform(
                get("/usuarios/mi-menu")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerMiMenu_usuarioNoEncontrado_unauthorized() throws Exception {
        // Cuando el usuario no se encuentra, el servicio retorna Optional.empty()
        // que se traduce en 401 UNAUTHORIZED
        Mockito.when(usuarioService.obtenerMenuUsuarioAutenticado())
                .thenReturn(Optional.empty());
        
        mockMvc.perform(
                get("/usuarios/mi-menu")
        )
        .andExpect(status().isUnauthorized());
    }
}
