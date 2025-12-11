package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.dto.OfertaResponse;
import ar.edu.unq.pdss22025.services.OfertaService;
import ar.edu.unq.pdss22025.services.UsuarioService;
import ar.edu.unq.pdss22025.mapper.OfertaMapper;
import ar.edu.unq.pdss22025.services.JwtService;
import ar.edu.unq.pdss22025.services.UsuarioDetailsService;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OfertaController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class OfertaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfertaService ofertaService;
    @MockitoBean
    private OfertaMapper ofertaMapper;
    @MockitoBean
    private UsuarioService usuarioService;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

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

    @Test
    @WithMockUser(roles = "CONCESIONARIA")
    void getMisOfertas_ConOfertas_DeberiaRetornar200() throws Exception {
        // Arrange
        Concesionaria concesionaria = Concesionaria.builder()
                .id(1L)
                .nombre("Concesionaria Test")
                .activa(true)
                .build();

        UsuarioConcesionaria usuario = new UsuarioConcesionaria();
        usuario.setId(1L);
        usuario.setEmail("concesionaria@test.com");
        usuario.setConcesionaria(concesionaria);

        OfertaAuto oferta = new OfertaAuto();
        OfertaResponse response = new OfertaResponse();
        response.setId(1L);

        Mockito.when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(Optional.of(usuario));
        Mockito.when(ofertaService.listarPorUsuarioConcesionaria(usuario)).thenReturn(List.of(oferta));
        Mockito.when(ofertaMapper.toResponse(oferta)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/ofertas/mis-ofertas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CONCESIONARIA")
    void getMisOfertas_SinOfertas_DeberiaRetornar200ConListaVacia() throws Exception {
        // Arrange
        Concesionaria concesionaria = Concesionaria.builder()
                .id(1L)
                .nombre("Concesionaria Test")
                .activa(true)
                .build();

        UsuarioConcesionaria usuario = new UsuarioConcesionaria();
        usuario.setId(1L);
        usuario.setEmail("concesionaria@test.com");
        usuario.setConcesionaria(concesionaria);

        Mockito.when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(Optional.of(usuario));
        Mockito.when(ofertaService.listarPorUsuarioConcesionaria(usuario)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/ofertas/mis-ofertas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CONCESIONARIA")
    void getMisOfertas_UsuarioNoAutenticado_DeberiaRetornar401() throws Exception {
        // Arrange
        Mockito.when(usuarioService.obtenerUsuarioAutenticado()).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/ofertas/mis-ofertas"))
                .andExpect(status().isUnauthorized());
    }
}

