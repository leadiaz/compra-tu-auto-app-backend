package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.CredencialesInvalidasException;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.usuario.menu.MenuItem;
import ar.edu.unq.pdss22025.models.usuario.UsuarioAdmin;
import ar.edu.unq.pdss22025.models.usuario.UsuarioComprador;
import ar.edu.unq.pdss22025.models.usuario.UsuarioConcesionaria;
import ar.edu.unq.pdss22025.repositories.UsuarioAdminRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioCompradorRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioConcesionariaRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;    
    private final UsuarioAdminRepository usuarioAdminRepository;
    private final UsuarioConcesionariaRepository usuarioConcesionariaRepository;
    private final UsuarioCompradorRepository usuarioCompradorRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, UsuarioAdminRepository usuarioAdminRepository, UsuarioCompradorRepository usuarioCompradorRepository, UsuarioConcesionariaRepository usuarioConcesionariaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioCompradorRepository = usuarioCompradorRepository;
        this.usuarioConcesionariaRepository = usuarioConcesionariaRepository;
        this.usuarioAdminRepository = usuarioAdminRepository;
    }

    public Usuario crearUsuario(String email, String password, String nombre, String apellido, String tipoUsuario) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El usuario con email " + email + " ya existe");
        }

        String tipo = (tipoUsuario == null ? "COMPRADOR" : tipoUsuario.trim().toUpperCase(Locale.ROOT));
        Usuario usuario = switch (tipo) {
            case "ADMIN" -> new UsuarioAdmin();
            case "CONCESIONARIA" -> new UsuarioConcesionaria();
            default -> new UsuarioComprador();
        };
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> obtenerUsuariosConFiltros(String tipoUsuario, Boolean sinConcesionaria) {
        // Si se solicita filtrar usuarios CONCESIONARIA sin concesionaria asignada
        if (sinConcesionaria != null && sinConcesionaria) {
            // Si también se especifica tipoUsuario, validar que sea CONCESIONARIA
            if (tipoUsuario != null && !tipoUsuario.trim().isEmpty()) {
                String tipo = tipoUsuario.trim().toUpperCase(Locale.ROOT);
                if (!tipo.equals("CONCESIONARIA")) {
                    // Si el tipo no es CONCESIONARIA, retornar lista vacía
                    return new ArrayList<>();
                }
            }
            // Filtrar usuarios CONCESIONARIA sin concesionaria usando JPA
            return new ArrayList<>(usuarioConcesionariaRepository.findByConcesionariaIsNull());
        }

        // Si se especifica tipo de usuario, filtrar por tipo usando JPA
        if (tipoUsuario != null && !tipoUsuario.trim().isEmpty()) {
            return obtenerUsuariosPorTipo(tipoUsuario);
        }

        // Si no hay filtros, retornar todos los usuarios
        return obtenerTodosLosUsuarios();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
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

    public Usuario autenticar(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> u.getPassword() != null && passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new CredencialesInvalidasException());
    }

    /**
     * Obtiene el usuario autenticado desde el SecurityContext.
     * @return Optional con el usuario autenticado, o empty si no está autenticado
     */
    public Optional<Usuario> obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String email = authentication.getName();
        return obtenerUsuarioPorEmail(email);
    }

    /**
     * Obtiene el menú del usuario autenticado.
     * @return Optional con la lista de items del menú, o empty si no está autenticado o no se encuentra el usuario
     */
    public Optional<List<MenuItem>> obtenerMenuUsuarioAutenticado() {
        return obtenerUsuarioAutenticado()
                .map(Usuario::getMenuItems);
    }
}
