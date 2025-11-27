package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.exceptions.UsuarioNoValidoException;
import ar.edu.unq.pdss22025.models.Concesionaria;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.repositories.ConcesionariaRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConcesionariaService {

    private final ConcesionariaRepository concesionariaRepository;
    private final UsuarioRepository usuarioRepository;

    public ConcesionariaService(ConcesionariaRepository concesionariaRepository, UsuarioRepository usuarioRepository) {
        this.concesionariaRepository = concesionariaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Concesionaria crearConcesionaria(String nombre, String cuit, String telefono, 
                                           String email, String direccion, Long usuarioId) {
        // Validar que el CUIT no exista
        if (concesionariaRepository.existsByCuit(cuit)) {
            throw new EntidadNoEncontradaException("Ya existe una concesionaria con el CUIT: " + cuit);
        }

        // Crear la concesionaria
        Concesionaria concesionaria = Concesionaria.builder()
                .nombre(nombre)
                .cuit(cuit)
                .telefono(telefono)
                .email(email)
                .direccion(direccion)
                .activa(true)
                .build();

        // Guardar primero la concesionaria
        concesionaria = concesionariaRepository.save(concesionaria);

        // Si se proporciona un usuarioId, validar y relacionar
        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new EntidadNoEncontradaException("Usuario con ID " + usuarioId + " no encontrado"));

            // Validar que el usuario sea de tipo CONCESIONARIA
            if (!usuario.esConcesionaria()) {
                throw new UsuarioNoValidoException("El usuario con ID " + usuarioId + " no es de tipo CONCESIONARIA");
            }

            // Validar que el usuario no tenga ya una concesionaria asociada
            if (usuario.getConcesionaria() != null) {
                throw new UsuarioNoValidoException("El usuario con ID " + usuarioId + " ya tiene una concesionaria asociada");
            }

            // Establecer la relación bidireccional
            // Como Usuario es el propietario de la relación (tiene el JoinColumn),
            // establecemos la relación en Usuario y guardamos el usuario
            usuario.setConcesionaria(concesionaria);
            usuarioRepository.save(usuario);
            
            // Actualizar la referencia en la concesionaria para reflejar el cambio
            concesionaria.setUsuario(usuario);
        }

        return concesionaria;
    }

    public List<Concesionaria> obtenerTodasLasConcesionarias() {
        return concesionariaRepository.findAll();
    }
}

