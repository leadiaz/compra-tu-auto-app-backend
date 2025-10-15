-- Flyway V1: Initial schema with BIGSERIAL primary keys (Long)
-- PostgreSQL dialect

-- USUARIO
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(320) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX ux_usuario_email ON usuario(email);

-- CONCESIONARIA
CREATE TABLE concesionaria (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    cuit VARCHAR(20) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(320),
    direccion VARCHAR(255),
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX ux_concesionaria_cuit ON concesionaria(cuit);

-- AUTO
CREATE TABLE auto (
    id BIGSERIAL PRIMARY KEY,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    anio_modelo INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ux_auto_marca_modelo_anio UNIQUE (marca, modelo, anio_modelo)
);

-- OFERTA_AUTO
CREATE TABLE oferta_auto (
    id BIGSERIAL PRIMARY KEY,
    concesionaria_id BIGINT NOT NULL,
    auto_id BIGINT NOT NULL,
    stock INT NOT NULL,
    precio_actual NUMERIC(15,2) NOT NULL,
    moneda VARCHAR(10) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_oferta_auto_concesionaria FOREIGN KEY (concesionaria_id) REFERENCES concesionaria(id),
    CONSTRAINT fk_oferta_auto_auto FOREIGN KEY (auto_id) REFERENCES auto(id),
    CONSTRAINT ux_oferta_auto_concesionaria_auto UNIQUE (concesionaria_id, auto_id)
);
CREATE INDEX idx_oferta_auto_concesionaria ON oferta_auto(concesionaria_id);
CREATE INDEX idx_oferta_auto_auto ON oferta_auto(auto_id);

-- USUARIO_CONCESIONARIA (Many-to-many)
CREATE TABLE usuario_concesionaria (
    usuario_id BIGINT NOT NULL,
    concesionaria_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, concesionaria_id),
    CONSTRAINT fk_usuario_concesionaria_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_concesionaria_concesionaria FOREIGN KEY (concesionaria_id) REFERENCES concesionaria(id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX ux_usuario_concesionaria ON usuario_concesionaria(usuario_id, concesionaria_id);

-- RESENA
CREATE TABLE resena (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    auto_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comentario VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_resena_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_resena_auto FOREIGN KEY (auto_id) REFERENCES auto(id)
);
CREATE INDEX idx_resena_usuario ON resena(usuario_id);
CREATE INDEX idx_resena_auto ON resena(auto_id);

-- COMPRA
CREATE TABLE compra (
    id BIGSERIAL PRIMARY KEY,
    oferta_id BIGINT NOT NULL,
    comprador_id BIGINT NOT NULL,
    precio_unitario NUMERIC(15,2) NOT NULL,
    cantidad INT NOT NULL,
    total NUMERIC(15,2) NOT NULL,
    fecha_compra TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_compra_oferta FOREIGN KEY (oferta_id) REFERENCES oferta_auto(id),
    CONSTRAINT fk_compra_comprador FOREIGN KEY (comprador_id) REFERENCES usuario(id)
);
CREATE INDEX idx_compra_oferta ON compra(oferta_id);
CREATE INDEX idx_compra_comprador ON compra(comprador_id);

-- FAVORITO
CREATE TABLE favorito (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    oferta_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_favorito_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorito_oferta FOREIGN KEY (oferta_id) REFERENCES oferta_auto(id)
);
CREATE UNIQUE INDEX ux_favorito_usuario ON favorito(usuario_id);
CREATE INDEX idx_favorito_oferta ON favorito(oferta_id);
