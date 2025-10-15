package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.models.Usuario;
import ar.edu.unq.pdss22025.models.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.UsuarioComprador;
import ar.edu.unq.pdss22025.models.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.repositories.UsuarioAdminRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioCompradorRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioConcesionariaRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioAdminRepository usuarioAdminRepository;

    @Autowired
    private UsuarioConcesionariaRepository usuarioConcesionariaRepository;

    @Autowired
    private UsuarioCompradorRepository usuarioCompradorRepository;

    public Usuario crearUsuario(String email, String password, String nombre, String apellido, String tipoUsuario) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El usuario con email " + email + " ya existe");
        }

        String tipo = (tipoUsuario == null ? "COMPRADOR" : tipoUsuario.trim().toUpperCase(Locale.ROOT));
        Usuario usuario;
        switch (tipo) {
            case "ADMIN":
                usuario = new UsuarioAdmin();
                break;
            case "CONCESIONARIA":
                usuario = new UsuarioConcesionaria();
                break;
            case "COMPRADOR":
            default:
                usuario = new UsuarioComprador();
                break;
        }
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }


    public List<Usuario> obtenerUsuariosPorTipo(String tipoUsuario) {
        String tipo = (tipoUsuario == null ? "COMPRADOR" : tipoUsuario.trim().toUpperCase(Locale.ROOT));
        return switch (tipo) {
            case "ADMIN" -> new ArrayList<>(usuarioAdminRepository.findAll());
            case "CONCESIONARIA" -> new ArrayList<>(usuarioConcesionariaRepository.findAll());
            case "COMPRADOR" -> new ArrayList<>(usuarioCompradorRepository.findAll());
            default -> throw new RuntimeException("Tipo de usuario no soportado: " + tipoUsuario);
        };
    }
}
