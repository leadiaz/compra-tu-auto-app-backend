package ar.edu.unq.pdss22025.services;

import ar.edu.unq.pdss22025.exceptions.EntidadNoEncontradaException;
import ar.edu.unq.pdss22025.models.Compra;
import ar.edu.unq.pdss22025.models.OfertaAuto;
import ar.edu.unq.pdss22025.models.usuario.Usuario;
import ar.edu.unq.pdss22025.models.dto.CrearCompraRequest;
import ar.edu.unq.pdss22025.repositories.CompraRepository;
import ar.edu.unq.pdss22025.repositories.OfertaAutoRepository;
import ar.edu.unq.pdss22025.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CompraService {
    public List<Compra> listarPorCompradorId(Long compradorId) {
        return compraRepository.findByCompradorId(compradorId);
    }

    private final CompraRepository compraRepository;
    private final OfertaAutoRepository ofertaAutoRepository;
    private final UsuarioRepository usuarioRepository;

    public CompraService(CompraRepository compraRepository, OfertaAutoRepository ofertaAutoRepository, UsuarioRepository usuarioRepository) {
        this.compraRepository = compraRepository;
        this.ofertaAutoRepository = ofertaAutoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Compra crear(CrearCompraRequest request) {
        OfertaAuto oferta = ofertaAutoRepository.findById(request.getOfertaId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Oferta no encontrada"));

        Usuario comprador = usuarioRepository.findById(request.getCompradorId())
                .orElseThrow(() -> new EntidadNoEncontradaException("Comprador no encontrado"));

        if (oferta.getStock() == null || oferta.getStock() <= 0) {
            throw new IllegalStateException("Oferta sin stock disponible");
        }

        BigDecimal precioCerrado = request.getPrecioCerrado() != null ? request.getPrecioCerrado() : oferta.getPrecioActual();
        if (precioCerrado == null) {
            throw new IllegalStateException("Precio no disponible");
        }

        Compra compra = Compra.builder()
                .oferta(oferta)
                .comprador(comprador)
                .precioUnitario(precioCerrado)
                .cantidad(1)
                .total(precioCerrado) // cantidad fija 1
                .build();

        // La fechaCompra se setea vía auditing (@CreatedDate -> fecha_compra)
        // Reducimos stock de la oferta
        oferta.setStock(oferta.getStock() - 1);

        return compraRepository.save(compra);
    }

    @Transactional(readOnly = true)
    public List<Compra> listarPorComprador(Long compradorId) {
        return compraRepository.findByCompradorId(compradorId);
    }
}
