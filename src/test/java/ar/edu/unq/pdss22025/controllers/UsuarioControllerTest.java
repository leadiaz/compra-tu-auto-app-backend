package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.mapper.CompraMapper;
import ar.edu.unq.pdss22025.models.Compra;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.dto.CompraResponse;
import ar.edu.unq.pdss22025.models.dto.FavoritoResponse;
import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.services.CompraService;
import ar.edu.unq.pdss22025.services.FavoritoService;
import ar.edu.unq.pdss22025.mapper.FavoritoMapper;
import ar.edu.unq.pdss22025.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoritoService favoritoService;
    @MockitoBean
    private FavoritoMapper favoritoMapper;
    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private CompraService compraService;
    @MockitoBean
    private CompraMapper compraMapper;

    @Test
    void setFavorito_ok() throws Exception {
        Favorito favorito = new Favorito();
        FavoritoResponse response = new FavoritoResponse();
        Mockito.when(favoritoService.definirFavorito(anyLong(), anyLong())).thenReturn(favorito);
        Mockito.when(favoritoMapper.toResponse(favorito)).thenReturn(response);
        mockMvc.perform(put("/usuarios/1/favorito/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void setFavorito_notFound() throws Exception {
        Mockito.when(favoritoService.definirFavorito(anyLong(), anyLong())).thenThrow(new IllegalArgumentException());
        mockMvc.perform(put("/usuarios/1/favorito/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void setFavorito_unprocessableEntity() throws Exception {
        Mockito.when(favoritoService.definirFavorito(anyLong(), anyLong())).thenThrow(new IllegalStateException());
        mockMvc.perform(put("/usuarios/1/favorito/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getFavorito_ok() throws Exception {
        Favorito favorito = new Favorito();
        FavoritoResponse response = new FavoritoResponse();
        Mockito.when(favoritoService.obtenerPorUsuario(1L)).thenReturn(Optional.of(favorito));
        Mockito.when(favoritoMapper.toResponse(favorito)).thenReturn(response);
        mockMvc.perform(
                get("/usuarios/1/favorito")
        ).andExpect(status().isOk());
    }

    @Test
    void getFavorito_noContent() throws Exception {
        Mockito.when(favoritoService.obtenerPorUsuario(1L)).thenReturn(Optional.empty());
        mockMvc.perform(
                get("/usuarios/1/favorito")
        ).andExpect(status().isNoContent());
    }

    @Test
    void getFavorito_notFound() throws Exception {
        Mockito.when(favoritoService.obtenerPorUsuario(1L)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(
                get("/usuarios/1/favorito")
        ).andExpect(status().isNotFound());
    }

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
    void obtenerTodosLosUsuarios_ok() throws Exception {
        Usuario usuario = Mockito.mock(Usuario.class);
        Mockito.when(usuario.getId()).thenReturn(1L);
        Mockito.when(usuario.getEmail()).thenReturn("test@test.com");
        Mockito.when(usuario.getNombre()).thenReturn("Test");
        Mockito.when(usuario.getApellido()).thenReturn("User");
        Mockito.when(usuario.getCreatedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(usuario.getActivo()).thenReturn(true);
        Mockito.when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(List.of(usuario));
        mockMvc.perform(
                get("/usuarios")
        ).andExpect(status().isOk());
    }

    @Test
    void obtenerUsuarioPorId_ok() throws Exception {
        Usuario usuario = Mockito.mock(Usuario.class);
        Mockito.when(usuario.getId()).thenReturn(1L);
        Mockito.when(usuario.getEmail()).thenReturn("test@test.com");
        Mockito.when(usuario.getNombre()).thenReturn("Test");
        Mockito.when(usuario.getApellido()).thenReturn("User");
        Mockito.when(usuario.getCreatedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(usuario.getActivo()).thenReturn(true);
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
    void obtenerUsuariosPorTipo_ok() throws Exception {
        Usuario usuario = Mockito.mock(Usuario.class);
        Mockito.when(usuario.getId()).thenReturn(1L);
        Mockito.when(usuario.getEmail()).thenReturn("test@test.com");
        Mockito.when(usuario.getNombre()).thenReturn("Test");
        Mockito.when(usuario.getApellido()).thenReturn("User");
        Mockito.when(usuario.getCreatedAt()).thenReturn(OffsetDateTime.now());
        Mockito.when(usuario.getActivo()).thenReturn(true);
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
}
