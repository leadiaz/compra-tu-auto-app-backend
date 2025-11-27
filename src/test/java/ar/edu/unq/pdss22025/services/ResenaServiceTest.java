package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.exceptions.PuntajeInvalidoException;
import ar.edu.unq.pdss22025.exceptions.ResenaYaExisteException;
import ar.edu.unq.pdss22025.models.Auto;
import ar.edu.unq.pdss22025.models.Resena;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.repositories.AutoRepository;
import ar.edu.unq.pdss22025.repositories.ResenaRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private AutoRepository autoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ResenaService resenaService;

    private UsuarioComprador usuario;
    private Auto auto;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioComprador();
        usuario.setId(1L);
        usuario.setEmail("usuario@test.com");
        usuario.setNombre("Usuario");
        usuario.setApellido("Test");

        auto = Auto.builder()
                .id(1L)
                .marca("Toyota")
                .modelo("Corolla")
                .anioModelo(2024)
                .build();
    }

    @Test
    void crearReseña_PuntajeValido_DeberiaCrearResena() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(autoRepository.findById(1L)).thenReturn(Optional.of(auto));
        when(resenaRepository.existsByUsuarioIdAndAutoId(1L, 1L)).thenReturn(false);
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> {
            Resena resena = invocation.getArgument(0);
            resena.setId(1L);
            return resena;
        });

        // Act
        Resena resultado = resenaService.crearReseña(1L, 1L, 8, "Excelente auto");

        // Assert
        assertNotNull(resultado);
        assertEquals(8, resultado.getRating());
        assertEquals("Excelente auto", resultado.getComentario());
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(auto, resultado.getAuto());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void crearReseña_PuntajeMenorACero_DeberiaLanzarExcepcion() {
        // Act & Assert
        assertThrows(PuntajeInvalidoException.class, () -> {
            resenaService.crearReseña(1L, 1L, -1, "Comentario");
        });
    }

    @Test
    void crearReseña_PuntajeMayorADiez_DeberiaLanzarExcepcion() {
        // Act & Assert
        assertThrows(PuntajeInvalidoException.class, () -> {
            resenaService.crearReseña(1L, 1L, 11, "Comentario");
        });
    }

    @Test
    void crearReseña_ResenaYaExiste_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(autoRepository.findById(1L)).thenReturn(Optional.of(auto));
        when(resenaRepository.existsByUsuarioIdAndAutoId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(ResenaYaExisteException.class, () -> {
            resenaService.crearReseña(1L, 1L, 8, "Comentario");
        });
    }

    @Test
    void actualizarReseña_ResenaExiste_DeberiaActualizar() {
        // Arrange
        Resena resena = Resena.builder()
                .id(1L)
                .usuario(usuario)
                .auto(auto)
                .rating(5)
                .comentario("Comentario antiguo")
                .build();
        when(resenaRepository.findByUsuarioIdAndAutoId(1L, 1L)).thenReturn(Optional.of(resena));
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        // Act
        Resena resultado = resenaService.actualizarReseña(1L, 1L, 9, "Comentario nuevo");

        // Assert
        assertEquals(9, resultado.getRating());
        assertEquals("Comentario nuevo", resultado.getComentario());
        verify(resenaRepository, times(1)).save(resena);
    }

    @Test
    void actualizarReseña_PuntajeInvalido_DeberiaLanzarExcepcion() {
        // Act & Assert
        assertThrows(PuntajeInvalidoException.class, () -> {
            resenaService.actualizarReseña(1L, 1L, 15, "Comentario");
        });
    }

    @Test
    void eliminarReseña_ResenaExiste_DeberiaEliminar() {
        // Arrange
        Resena resena = Resena.builder()
                .id(1L)
                .usuario(usuario)
                .auto(auto)
                .rating(8)
                .build();
        when(resenaRepository.findByUsuarioIdAndAutoId(1L, 1L)).thenReturn(Optional.of(resena));

        // Act
        resenaService.eliminarReseña(1L, 1L);

        // Assert
        verify(resenaRepository, times(1)).delete(resena);
    }

    @Test
    void listarResenasDeAuto_AutoExiste_DeberiaRetornarLista() {
        // Arrange
        List<Resena> resenas = new ArrayList<>();
        resenas.add(Resena.builder().id(1L).usuario(usuario).auto(auto).rating(8).build());
        when(autoRepository.findById(1L)).thenReturn(Optional.of(auto));
        when(resenaRepository.findByAutoOrderByCreatedAtDesc(auto)).thenReturn(resenas);

        // Act
        List<Resena> resultado = resenaService.listarReseñasDeAuto(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPromedioPuntajeAuto_AutoConResenas_DeberiaCalcularPromedio() {
        // Arrange
        when(autoRepository.findById(1L)).thenReturn(Optional.of(auto));
        when(resenaRepository.calcularPromedioPuntajePorAutoId(1L)).thenReturn(8.5);

        // Act
        double promedio = resenaService.obtenerPromedioPuntajeAuto(1L);

        // Assert
        assertEquals(8.5, promedio);
    }

    @Test
    void obtenerPromedioPuntajeAuto_AutoSinResenas_DeberiaRetornarCero() {
        // Arrange
        when(autoRepository.findById(1L)).thenReturn(Optional.of(auto));
        when(resenaRepository.calcularPromedioPuntajePorAutoId(1L)).thenReturn(null);

        // Act
        double promedio = resenaService.obtenerPromedioPuntajeAuto(1L);

        // Assert
        assertEquals(0.0, promedio);
    }
}
