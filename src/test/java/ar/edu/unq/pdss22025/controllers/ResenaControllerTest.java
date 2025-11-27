package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.ResenaResponse;
import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.services.ResenaService;
import ar.edu.unq.pdss22025.mapper.ResenaMapper;
import ar.edu.unq.pdss22025.services.JwtService;
import ar.edu.unq.pdss22025.services.UsuarioDetailsService;
import ar.edu.unq.pdss22025.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ResenaController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class ResenaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResenaService resenaService;
    @MockitoBean
    private ResenaMapper resenaMapper;
    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void crearResena_ok() throws Exception {
        UsuarioComprador usuario = new UsuarioComprador();
        usuario.setId(1L);
        Resena resena = new Resena();
        ResenaResponse response = new ResenaResponse();
        Mockito.when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(Optional.of(usuario));
        Mockito.when(resenaService.crearReseña(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString())).thenReturn(resena);
        Mockito.when(resenaMapper.toResponse(resena)).thenReturn(response);
        mockMvc.perform(post("/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"autoId\": 1, \"puntaje\": 8, \"comentario\": \"Test\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void crearResena_notFound() throws Exception {
        UsuarioComprador usuario = new UsuarioComprador();
        usuario.setId(1L);
        Mockito.when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(Optional.of(usuario));
        Mockito.when(resenaService.crearReseña(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString()))
                .thenThrow(new ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException("Auto no encontrado"));
        mockMvc.perform(post("/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"autoId\": 1, \"puntaje\": 8, \"comentario\": \"Test\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearResena_conflict() throws Exception {
        UsuarioComprador usuario = new UsuarioComprador();
        usuario.setId(1L);
        Mockito.when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(Optional.of(usuario));
        Mockito.when(resenaService.crearReseña(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString()))
                .thenThrow(new ar.edu.unq.pdss22025.exceptions.ResenaYaExisteException("Ya existe una reseña"));
        mockMvc.perform(post("/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"autoId\": 1, \"puntaje\": 8, \"comentario\": \"Test\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getResenasByAuto_ok() throws Exception {
        Resena resena = new Resena();
        ResenaResponse response = new ResenaResponse();
        Mockito.when(resenaService.listarReseñasDeAuto(1L)).thenReturn(List.of(resena));
        Mockito.when(resenaMapper.toResponse(resena)).thenReturn(response);
        mockMvc.perform(get("/resenas/auto/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getResenasByAuto_emptyList() throws Exception {
        Mockito.when(resenaService.listarReseñasDeAuto(1L)).thenReturn(List.of());
        mockMvc.perform(get("/resenas/auto/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getResenasByAuto_notFound_exception() throws Exception {
        Mockito.when(resenaService.listarReseñasDeAuto(1L))
                .thenThrow(new ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException("Auto no encontrado"));
        mockMvc.perform(get("/resenas/auto/1"))
                .andExpect(status().isNotFound());
    }
}

