package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.exceptions.FavoritoYaExisteException;
import ar.edu.unq.pdss22025.exceptions.UsuarioNoValidoException;
import ar.edu.unq.pdss22025.models.Favorito;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.usuario.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.repositories.FavoritoRepository;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritoServiceTest {

    @Mock
    private FavoritoRepository favoritoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private OfertaAutoRepository ofertaAutoRepository;

    @InjectMocks
    private FavoritoService favoritoService;

    private UsuarioComprador usuarioComprador;
    private UsuarioAdmin usuarioAdmin;
    private OfertaAuto oferta;

    @BeforeEach
    void setUp() {
        usuarioComprador = new UsuarioComprador();
        usuarioComprador.setId(1L);
        usuarioComprador.setEmail("comprador@test.com");
        usuarioComprador.setNombre("Comprador");
        usuarioComprador.setApellido("Test");

        usuarioAdmin = new UsuarioAdmin();
        usuarioAdmin.setId(2L);
        usuarioAdmin.setEmail("admin@test.com");

        oferta = OfertaAuto.builder()
                .id(1L)
                .stock(10)
                .precioActual(new BigDecimal("50000.00"))
                .moneda("ARS")
                .build();
    }

    @Test
    void agregarFavorito_UsuarioComprador_DeberiaCrearFavorito() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioComprador));
        when(ofertaAutoRepository.findById(1L)).thenReturn(Optional.of(oferta));
        when(favoritoRepository.existsByUsuarioIdAndOfertaId(1L, 1L)).thenReturn(false);
        when(favoritoRepository.save(any(Favorito.class))).thenAnswer(invocation -> {
            Favorito favorito = invocation.getArgument(0);
            favorito.setId(1L);
            return favorito;
        });

        // Act
        Favorito resultado = favoritoService.agregarFavorito(1L, 1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioComprador, resultado.getUsuario());
        assertEquals(oferta, resultado.getOferta());
        verify(favoritoRepository, times(1)).save(any(Favorito.class));
    }

    @Test
    void agregarFavorito_UsuarioNoComprador_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioAdmin));

        // Act & Assert
        assertThrows(UsuarioNoValidoException.class, () -> {
            favoritoService.agregarFavorito(2L, 1L);
        });
    }

    @Test
    void agregarFavorito_FavoritoYaExiste_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioComprador));
        when(ofertaAutoRepository.findById(1L)).thenReturn(Optional.of(oferta));
        when(favoritoRepository.existsByUsuarioIdAndOfertaId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(FavoritoYaExisteException.class, () -> {
            favoritoService.agregarFavorito(1L, 1L);
        });
    }

    @Test
    void eliminarFavorito_FavoritoExiste_DeberiaEliminar() {
        // Arrange
        Favorito favorito = Favorito.builder()
                .id(1L)
                .usuario(usuarioComprador)
                .oferta(oferta)
                .build();
        when(favoritoRepository.findByUsuarioIdAndOfertaId(1L, 1L)).thenReturn(Optional.of(favorito));

        // Act
        favoritoService.eliminarFavorito(1L, 1L);

        // Assert
        verify(favoritoRepository, times(1)).delete(favorito);
    }

    @Test
    void eliminarFavorito_FavoritoNoExiste_DeberiaLanzarExcepcion() {
        // Arrange
        when(favoritoRepository.findByUsuarioIdAndOfertaId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntidadNoEncontradaException.class, () -> {
            favoritoService.eliminarFavorito(1L, 1L);
        });
    }

    @Test
    void listarFavoritosDeUsuario_UsuarioExiste_DeberiaRetornarLista() {
        // Arrange
        List<Favorito> favoritos = new ArrayList<>();
        favoritos.add(Favorito.builder().id(1L).usuario(usuarioComprador).oferta(oferta).build());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioComprador));
        when(favoritoRepository.findByUsuarioId(1L)).thenReturn(favoritos);

        // Act
        List<Favorito> resultado = favoritoService.listarFavoritosDeUsuario(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void listarFavoritosDeUsuario_UsuarioNoExiste_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntidadNoEncontradaException.class, () -> {
            favoritoService.listarFavoritosDeUsuario(999L);
        });
    }
}
