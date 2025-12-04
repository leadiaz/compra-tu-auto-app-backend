package ar.edu.unq.pdss22025.exceptions;

public class FavoritoYaExisteException extends RuntimeException {
    
    public FavoritoYaExisteException(String message) {
        super(message);
    }
    
    public FavoritoYaExisteException() {
        super("El usuario ya tiene esta oferta marcada como favorito");
    }
}

