package ar.edu.unq.pdss22025.controllers;

import ar.edu.unq.pdss22025.models.dto.ReporteTop5Response;
import ar.edu.unq.pdss22025.services.JwtService;
import ar.edu.unq.pdss22025.services.ReporteService;
import ar.edu.unq.pdss22025.services.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReporteController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReporteService reporteService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void obtenerAutosMasVendidos_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerAutosMasVendidos(null, null, null)).thenReturn(response);

        mockMvc.perform(get("/reports/autos-mas-vendidos"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerAutosMasVendidos_conFiltros_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerAutosMasVendidos("2024-01-01", "2024-12-31", null))
                .thenReturn(response);

        mockMvc.perform(get("/reports/autos-mas-vendidos")
                        .param("fechaDesde", "2024-01-01")
                        .param("fechaHasta", "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerAutosMasVendidos_conPeriodo_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerAutosMasVendidos(null, null, "mes"))
                .thenReturn(response);

        mockMvc.perform(get("/reports/autos-mas-vendidos")
                        .param("periodo", "mes"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerUsuariosMasCompras_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerUsuariosMasCompras(null, null, null)).thenReturn(response);

        mockMvc.perform(get("/reports/usuarios-mas-compras"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerUsuariosMasCompras_conFiltros_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerUsuariosMasCompras("2024-01-01", "2024-12-31", null))
                .thenReturn(response);

        mockMvc.perform(get("/reports/usuarios-mas-compras")
                        .param("fechaDesde", "2024-01-01")
                        .param("fechaHasta", "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerAutosMejoresRankeados_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerAutosMejoresRankeados(null, null, null)).thenReturn(response);

        mockMvc.perform(get("/reports/autos-mejores-rankeados"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerAutosMejoresRankeados_conFiltros_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerAutosMejoresRankeados("2024-01-01", "2024-12-31", null))
                .thenReturn(response);

        mockMvc.perform(get("/reports/autos-mejores-rankeados")
                        .param("fechaDesde", "2024-01-01")
                        .param("fechaHasta", "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerAgenciasMasVentas_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerAgenciasMasVentas(null, null, null)).thenReturn(response);

        mockMvc.perform(get("/reports/agencias-mas-ventas"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerAgenciasMasVentas_conFiltros_ok() throws Exception {
        ReporteTop5Response response = new ReporteTop5Response();
        Mockito.when(reporteService.obtenerAgenciasMasVentas("2024-01-01", "2024-12-31", null))
                .thenReturn(response);

        mockMvc.perform(get("/reports/agencias-mas-ventas")
                        .param("fechaDesde", "2024-01-01")
                        .param("fechaHasta", "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    void exportarReporte_ok() throws Exception {
        mockMvc.perform(get("/reports/exportar")
                        .param("tipo", "autos-mas-vendidos")
                        .param("formato", "excel"))
                .andExpect(status().isOk());
    }
}

