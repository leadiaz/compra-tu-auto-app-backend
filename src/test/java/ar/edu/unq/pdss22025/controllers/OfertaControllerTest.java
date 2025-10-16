package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.dto.OfertaResponse;
import ar.edu.unq.pdss22025.services.OfertaService;
import ar.edu.unq.pdss22025.mapper.OfertaMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OfertaController.class)
class OfertaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfertaService ofertaService;
    @MockitoBean
    private OfertaMapper ofertaMapper;

    @Test
    void getOfertasByConcesionaria_ok() throws Exception {
        OfertaAuto oferta = new OfertaAuto();
        OfertaResponse response = new OfertaResponse();
        Mockito.when(ofertaService.listarPorConcesionaria(1L)).thenReturn(List.of(oferta));
        Mockito.when(ofertaMapper.toResponse(oferta)).thenReturn(response);
        mockMvc.perform(get("/ofertas?concesionariaId=1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOfertasByConcesionaria_notFound() throws Exception {
        Mockito.when(ofertaService.listarPorConcesionaria(1L)).thenReturn(List.of());
        mockMvc.perform(get("/ofertas?concesionariaId=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOfertasByAuto_ok() throws Exception {
        OfertaAuto oferta = new OfertaAuto();
        OfertaResponse response = new OfertaResponse();
        Mockito.when(ofertaService.listarPorAuto(2L)).thenReturn(List.of(oferta));
        Mockito.when(ofertaMapper.toResponse(oferta)).thenReturn(response);
        mockMvc.perform(get("/ofertas/autos/2"))
                .andExpect(status().isOk());
    }

    @Test
    void getOfertasByAuto_notFound() throws Exception {
        Mockito.when(ofertaService.listarPorAuto(2L)).thenReturn(List.of());
        mockMvc.perform(get("/ofertas/autos/2"))
                .andExpect(status().isNotFound());
    }
}

