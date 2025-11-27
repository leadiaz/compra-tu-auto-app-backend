package ar.edu.unq.pdss22025.exceptions;

import ar.edu.unq.pdss22025.models.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        // Determinar el código de estado según el contexto
        // Para autenticación, usar 401, para otros casos 404
        HttpStatus status = determineStatusForIllegalArgument(ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Argumento inválido",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        
        // Determinar el código de estado según el contexto
        // Para reglas de negocio, usar 422, para otros casos 404
        HttpStatus status = determineStatusForIllegalState(ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Estado inválido",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Error en la solicitud",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        if (errorMessage.isEmpty()) {
            errorMessage = "Error de validación en los argumentos";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.joining(", "));
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredencialesInvalidasException(
            CredencialesInvalidasException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Credenciales inválidas",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleEntidadNoEncontradaException(
            EntidadNoEncontradaException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "Entidad no encontrada",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UsuarioNoValidoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNoValidoException(
            UsuarioNoValidoException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "El usuario no es válido para esta operación",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    @ExceptionHandler(FavoritoYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleFavoritoYaExisteException(
            FavoritoYaExisteException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "El usuario ya tiene esta oferta marcada como favorito",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ResenaYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleResenaYaExisteException(
            ResenaYaExisteException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "El usuario ya tiene una reseña para este auto",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(PuntajeInvalidoException.class)
    public ResponseEntity<ErrorResponse> handlePuntajeInvalidoException(
            PuntajeInvalidoException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage() != null ? ex.getMessage() : "El puntaje debe estar entre 0 y 10",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Determina el código de estado HTTP para IllegalArgumentException.
     * Retorna 404 para argumentos inválidos.
     */
    private HttpStatus determineStatusForIllegalArgument(String message) {
        return HttpStatus.NOT_FOUND;
    }

    /**
     * Determina el código de estado HTTP para IllegalStateException.
     * Si el mensaje contiene palabras relacionadas con reglas de negocio o restricciones,
     * retorna 422. De lo contrario, retorna 404.
     * 
     * Nota: "sin stock" y "precio no disponible" devuelven 404 porque son estados
     * inconsistentes en la creación de compras, no reglas de negocio.
     */
    private HttpStatus determineStatusForIllegalState(String message) {
        if (message != null) {
            String lowerMessage = message.toLowerCase();
            // Solo reglas de negocio específicas devuelven 422
            if (lowerMessage.contains("solo los usuarios") || 
                lowerMessage.contains("pueden definir") ||
                lowerMessage.contains("no permitido") ||
                lowerMessage.contains("no puede") ||
                lowerMessage.contains("regla")) {
                return HttpStatus.UNPROCESSABLE_ENTITY;
            }
        }
        // Por defecto, estados inconsistentes devuelven 404
        return HttpStatus.NOT_FOUND;
    }
}

