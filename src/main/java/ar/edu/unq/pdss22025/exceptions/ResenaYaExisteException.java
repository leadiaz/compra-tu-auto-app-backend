package ar.edu.unq.pdss22025.exceptions;

public class ResenaYaExisteException extends RuntimeException {
    
    public ResenaYaExisteException(String message) {
        super(message);
    }
    
    public ResenaYaExisteException() {
        super("El usuario ya tiene una reseña para este auto");
    }
}

