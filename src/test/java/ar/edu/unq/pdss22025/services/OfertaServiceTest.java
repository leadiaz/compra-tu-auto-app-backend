package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.UsuarioNoValidoException;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfertaServiceTest {

    @Mock
    private OfertaAutoRepository ofertaAutoRepository;

    @Mock
    private AutoRepository autoRepository;

    @InjectMocks
    private OfertaService ofertaService;

    private UsuarioConcesionaria usuarioConcesionaria;
    private UsuarioComprador usuarioComprador;
    private Concesionaria concesionaria;
    private Auto auto;
    private OfertaAuto oferta1;
    private OfertaAuto oferta2;

    @BeforeEach
    void setUp() {
        // Configurar concesionaria
        concesionaria = Concesionaria.builder()
                .id(1L)
                .nombre("Concesionaria Test")
                .cuit("20-12345678-9")
                .activa(true)
                .build();

        // Configurar auto
        auto = Auto.builder()
                .id(1L)
                .marca("Toyota")
                .modelo("Corolla")
                .anioModelo(2024)
                .build();

        // Configurar usuario concesionaria
        usuarioConcesionaria = new UsuarioConcesionaria();
        usuarioConcesionaria.setId(1L);
        usuarioConcesionaria.setEmail("concesionaria@test.com");
        usuarioConcesionaria.setNombre("Concesionaria");
        usuarioConcesionaria.setApellido("Test");
        usuarioConcesionaria.setConcesionaria(concesionaria);

        // Configurar usuario comprador (para test negativo)
        usuarioComprador = new UsuarioComprador();
        usuarioComprador.setId(2L);
        usuarioComprador.setEmail("comprador@test.com");
        usuarioComprador.setNombre("Comprador");
        usuarioComprador.setApellido("Test");

        // Configurar ofertas
        oferta1 = OfertaAuto.builder()
                .id(1L)
                .concesionaria(concesionaria)
                .auto(auto)
                .stock(10)
                .precioActual(new BigDecimal("50000.00"))
                .moneda("ARS")
                .build();

        oferta2 = OfertaAuto.builder()
                .id(2L)
                .concesionaria(concesionaria)
                .auto(auto)
                .stock(5)
                .precioActual(new BigDecimal("60000.00"))
                .moneda("ARS")
                .build();
    }

    @Test
    @DisplayName("Listar ofertas por usuario concesionaria con concesionaria asociada - Debería retornar lista de ofertas")
    void listarPorUsuarioConcesionaria_UsuarioConConcesionaria_DeberiaRetornarOfertas() {
        // Arrange
        List<OfertaAuto> ofertasEsperadas = List.of(oferta1, oferta2);
        when(ofertaAutoRepository.findByConcesionariaId(concesionaria.getId()))
                .thenReturn(ofertasEsperadas);

        // Act
        List<OfertaAuto> resultado = ofertaService.listarPorUsuarioConcesionaria(usuarioConcesionaria);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(oferta1.getId(), resultado.get(0).getId());
        assertEquals(oferta2.getId(), resultado.get(1).getId());
        verify(ofertaAutoRepository, times(1)).findByConcesionariaId(concesionaria.getId());
    }

    @Test
    @DisplayName("Listar ofertas por usuario concesionaria sin ofertas - Debería retornar lista vacía")
    void listarPorUsuarioConcesionaria_SinOfertas_DeberiaRetornarListaVacia() {
        // Arrange
        when(ofertaAutoRepository.findByConcesionariaId(concesionaria.getId()))
                .thenReturn(new ArrayList<>());

        // Act
        List<OfertaAuto> resultado = ofertaService.listarPorUsuarioConcesionaria(usuarioConcesionaria);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(ofertaAutoRepository, times(1)).findByConcesionariaId(concesionaria.getId());
    }

    @Test
    @DisplayName("Listar ofertas por usuario que no es concesionaria - Debería lanzar excepción")
    void listarPorUsuarioConcesionaria_UsuarioNoConcesionaria_DeberiaLanzarExcepcion() {
        // Act & Assert
        UsuarioNoValidoException exception = assertThrows(
                UsuarioNoValidoException.class,
                () -> ofertaService.listarPorUsuarioConcesionaria(usuarioComprador)
        );

        assertEquals("Solo los usuarios de tipo CONCESIONARIA pueden listar sus ofertas", exception.getMessage());
        verify(ofertaAutoRepository, never()).findByConcesionariaId(anyLong());
    }

    @Test
    @DisplayName("Listar ofertas por usuario concesionaria sin concesionaria asociada - Debería lanzar excepción")
    void listarPorUsuarioConcesionaria_UsuarioSinConcesionaria_DeberiaLanzarExcepcion() {
        // Arrange
        UsuarioConcesionaria usuarioSinConcesionaria = new UsuarioConcesionaria();
        usuarioSinConcesionaria.setId(3L);
        usuarioSinConcesionaria.setEmail("sinconcesionaria@test.com");
        usuarioSinConcesionaria.setConcesionaria(null);

        // Act & Assert
        UsuarioNoValidoException exception = assertThrows(
                UsuarioNoValidoException.class,
                () -> ofertaService.listarPorUsuarioConcesionaria(usuarioSinConcesionaria)
        );

        assertEquals("El usuario CONCESIONARIA no tiene una concesionaria asociada", exception.getMessage());
        verify(ofertaAutoRepository, never()).findByConcesionariaId(anyLong());
    }
}

