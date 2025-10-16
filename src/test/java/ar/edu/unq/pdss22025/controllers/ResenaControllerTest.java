package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.ResenaResponse;
import ar.edu.unq.pdss22025.models.dto.CrearResenaRequest;
import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.services.ResenaService;
import ar.edu.unq.pdss22025.mapper.ResenaMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResenaController.class)
class ResenaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResenaService resenaService;
    @MockitoBean
    private ResenaMapper resenaMapper;

    @Test
    void crearResena_ok() throws Exception {
        Resena resena = new Resena();
        ResenaResponse response = new ResenaResponse();
        Mockito.when(resenaService.crear(Mockito.any())).thenReturn(resena);
        Mockito.when(resenaMapper.toResponse(resena)).thenReturn(response);
        mockMvc.perform(post("/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void crearResena_notFound() throws Exception {
        Mockito.when(resenaService.crear(Mockito.any())).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearResena_illegalState() throws Exception {
        Mockito.when(resenaService.crear(Mockito.any())).thenThrow(new IllegalStateException());
        mockMvc.perform(post("/resenas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getResenasByAuto_ok() throws Exception {
        Resena resena = new Resena();
        ResenaResponse response = new ResenaResponse();
        Mockito.when(resenaService.listarPorAuto(1L)).thenReturn(List.of(resena));
        Mockito.when(resenaMapper.toResponse(resena)).thenReturn(response);
        mockMvc.perform(get("/resenas/autos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getResenasByAuto_notFound_emptyList() throws Exception {
        Mockito.when(resenaService.listarPorAuto(1L)).thenReturn(List.of());
        mockMvc.perform(get("/resenas/autos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getResenasByAuto_notFound_exception() throws Exception {
        Mockito.when(resenaService.listarPorAuto(1L)).thenThrow(new IllegalArgumentException());
        mockMvc.perform(get("/resenas/autos/1"))
                .andExpect(status().isNotFound());
    }
}

