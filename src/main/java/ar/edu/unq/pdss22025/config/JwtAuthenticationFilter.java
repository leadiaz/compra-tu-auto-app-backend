package ar.edu.unq.pdss22025.config;

import ar.edu.unq.pdss22025.services.JwtService;
import ar.edu.unq.pdss22025.services.UsuarioDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioDetailsService usuarioDetailsService) {
        this.jwtService = jwtService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Saltar el filtro para rutas públicas (Swagger, login, etc.)
        // getServletPath() devuelve el path sin el context-path
        String path = request.getServletPath();
        // Si está vacío, usar getRequestURI() y remover el context-path si existe
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
            // Remover context-path si está presente
            String contextPath = request.getContextPath();
            if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
                path = path.substring(contextPath.length());
            }
        }
        String method = request.getMethod();
        if (isPublicPath(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = usuarioDetailsService.loadUserByUsername(userEmail);

                if (jwtService.validateToken(jwt, userEmail)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            // Usuario no encontrado en la base de datos, continuar sin autenticación
            // Esto es seguro porque no se establece el contexto de seguridad
        } catch (io.jsonwebtoken.JwtException e) {
            // Token inválido, malformado o con firma incorrecta, continuar sin autenticación
            // Esto es seguro porque no se establece el contexto de seguridad
        } catch (Exception e) {
            // Cualquier otro error (parsing, null pointer, etc.), continuar sin autenticación
            // Esto es seguro porque no se establece el contexto de seguridad
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path, String method) {
        // Rutas de Swagger/OpenAPI
        if (path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-resources") ||
            path.startsWith("/webjars") ||
            path.startsWith("/favicon.ico") ||
            path.equals("/auth/login") ||
            path.startsWith("/health")) {
            return true;
        }
        // POST /usuarios es público (registro)
        return path.equals("/usuarios") && "POST".equalsIgnoreCase(method);
    }
}

