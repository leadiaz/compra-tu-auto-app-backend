package ar.edu.unq.pdss22025.exceptions;

public class PuntajeInvalidoException extends RuntimeException {
    
    public PuntajeInvalidoException(String message) {
        super(message);
    }
    
    public PuntajeInvalidoException() {
        super("El puntaje debe estar entre 0 y 10");
    }
}

