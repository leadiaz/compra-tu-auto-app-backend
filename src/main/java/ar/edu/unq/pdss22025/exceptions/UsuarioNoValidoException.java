package ar.edu.unq.pdss22025.exceptions;

public class UsuarioNoValidoException extends RuntimeException {
    
    public UsuarioNoValidoException(String message) {
        super(message);
    }
    
    public UsuarioNoValidoException() {
        super("El usuario no es válido para esta operación");
    }
}

