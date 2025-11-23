package ar.edu.unq.pdss22025.exceptions;

public class EntidadNoEncontradaException extends RuntimeException {
    
    public EntidadNoEncontradaException(String message) {
        super(message);
    }
    
    public EntidadNoEncontradaException() {
        super("Entidad no encontrada");
    }
}

