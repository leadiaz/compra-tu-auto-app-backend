package ar.edu.unq.pdss22025.mapper;

import ar.edu.unq.pdss22025.models.Compra;
import ar.edu.unq.pdss22025.models.dto.CompraResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CompraMapper {

    @Mappings({
        @Mapping(source = "oferta.id", target = "ofertaId"),
        @Mapping(source = "comprador.id", target = "compradorId"),
        @Mapping(source = "precioUnitario", target = "precioCerrado"),
        @Mapping(source = "fechaCompra", target = "fechaCompra")
    })
    CompraResponse toResponse(Compra entity);
}
