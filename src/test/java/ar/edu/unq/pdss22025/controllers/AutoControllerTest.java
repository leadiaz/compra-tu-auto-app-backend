package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.dto.CrearAutoRequest;
import ar.edu.unq.pdss22025.services.AutoService;
import ar.edu.unq.pdss22025.services.JwtService;
import ar.edu.unq.pdss22025.services.UsuarioDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AutoController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class AutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AutoService autoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearAuto_ok() throws Exception {
        Auto auto = Auto.builder()
                .id(1L)
                .marca("Toyota")
                .modelo("Corolla")
                .anioModelo(2024)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Mockito.when(autoService.crearAuto(anyString(), anyString(), anyInt())).thenReturn(auto);

        CrearAutoRequest request = new CrearAutoRequest();
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAnioModelo(2024);

        mockMvc.perform(post("/autos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearAuto_conflicto() throws Exception {
        Mockito.when(autoService.crearAuto(anyString(), anyString(), anyInt()))
                .thenThrow(new IllegalStateException("No puede existir más de un auto con la combinación marca-modelo-año"));

        CrearAutoRequest request = new CrearAutoRequest();
        request.setMarca("Toyota");
        request.setModelo("Corolla");
        request.setAnioModelo(2024);

        mockMvc.perform(post("/autos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminarAuto_ok() throws Exception {
        mockMvc.perform(delete("/autos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminarAuto_notFound() throws Exception {
        Mockito.doThrow(new EntidadNoEncontradaException("Auto con ID 1 no encontrado"))
                .when(autoService).eliminarAuto(1L);

        mockMvc.perform(delete("/autos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarAutos_ok() throws Exception {
        Auto auto = Auto.builder()
                .id(1L)
                .marca("Toyota")
                .modelo("Corolla")
                .anioModelo(2024)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        Mockito.when(autoService.listarAutos()).thenReturn(List.of(auto));

        mockMvc.perform(get("/autos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].marca").value("Toyota"));
    }

    @Test
    @WithMockUser(roles = "COMPRADOR")
    void listarMarcas_ok() throws Exception {
        List<String> marcas = List.of("Ford", "Honda", "Toyota");
        Mockito.when(autoService.obtenerMarcas()).thenReturn(marcas);

        mockMvc.perform(get("/autos/marcas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Ford"))
                .andExpect(jsonPath("$[1]").value("Honda"))
                .andExpect(jsonPath("$[2]").value("Toyota"));
    }

    @Test
    @WithMockUser(roles = "CONCESIONARIA")
    void listarMarcas_concesionaria_ok() throws Exception {
        List<String> marcas = List.of("Ford", "Toyota");
        Mockito.when(autoService.obtenerMarcas()).thenReturn(marcas);

        mockMvc.perform(get("/autos/marcas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "COMPRADOR")
    void listarModelos_ok() throws Exception {
        List<String> modelos = List.of("Corolla", "Camry", "RAV4");
        Mockito.when(autoService.obtenerModelosPorMarca("Toyota")).thenReturn(modelos);

        mockMvc.perform(get("/autos/modelos")
                        .param("marca", "Toyota"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Corolla"))
                .andExpect(jsonPath("$[1]").value("Camry"))
                .andExpect(jsonPath("$[2]").value("RAV4"));
    }

    @Test
    @WithMockUser(roles = "COMPRADOR")
    void listarModelos_sinMarca_deberiaRetornar400() throws Exception {
        mockMvc.perform(get("/autos/modelos"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "CONCESIONARIA")
    void listarModelos_concesionaria_ok() throws Exception {
        List<String> modelos = List.of("Focus", "Fiesta");
        Mockito.when(autoService.obtenerModelosPorMarca("Ford")).thenReturn(modelos);

        mockMvc.perform(get("/autos/modelos")
                        .param("marca", "Ford"))
                .andExpect(status().isOk());
    }
}

