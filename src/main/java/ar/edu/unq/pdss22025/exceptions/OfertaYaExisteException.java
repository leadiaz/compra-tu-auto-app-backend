package ar.edu.unq.pdss22025.exceptions;

public class OfertaYaExisteException extends RuntimeException {
    
    public OfertaYaExisteException(String message) {
        super(message);
    }
    
    public OfertaYaExisteException() {
        super("Ya existe una oferta para esta concesionaria y este auto");
    }
}


