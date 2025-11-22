package ar.edu.unq.pdss22025.exceptions;

public class CredencialesInvalidasException extends RuntimeException {
    
    public CredencialesInvalidasException(String message) {
        super(message);
    }
    
    public CredencialesInvalidasException() {
        super("Credenciales inválidas");
    }
}

