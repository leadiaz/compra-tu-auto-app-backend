-- Flyway V5: Actualizar esquema de Favoritos y Reseñas
-- Cambios:
-- 1. Favorito: actualizar restricción única de (usuario_id) a (usuario_id, oferta_id) para permitir múltiples favoritos por usuario
-- 2. Resena: cambiar rating de 1-5 a 0-10, agregar restricción única (usuario_id, auto_id)

-- ========== ACTUALIZAR FAVORITO ==========

-- Eliminar índice único antiguo (solo usuario_id)
DROP INDEX IF EXISTS ux_favorito_usuario;

-- Crear nueva restricción única: (usuario_id, oferta_id)
-- Esto permite que un usuario tenga múltiples favoritos, pero solo uno por oferta
CREATE UNIQUE INDEX IF NOT EXISTS ux_favorito_usuario_oferta ON favorito(usuario_id, oferta_id);

-- Asegurar que existe el índice para oferta_id (ya debería existir, pero por si acaso)
CREATE INDEX IF NOT EXISTS idx_favorito_oferta ON favorito(oferta_id);

-- Asegurar que existe el índice para usuario_id
CREATE INDEX IF NOT EXISTS idx_favorito_usuario ON favorito(usuario_id);

-- ========== ACTUALIZAR RESENA ==========

-- Eliminar restricción CHECK antigua de rating
ALTER TABLE resena DROP CONSTRAINT IF EXISTS resena_rating_check;

-- Actualizar restricción CHECK para permitir 0-10
ALTER TABLE resena ADD CONSTRAINT resena_rating_check 
    CHECK (rating >= 0 AND rating <= 10);

-- Agregar restricción única: (usuario_id, auto_id)
-- Primero eliminar duplicados si existen (mantener el más reciente)
DELETE FROM resena r1
WHERE EXISTS (
    SELECT 1 FROM resena r2
    WHERE r2.usuario_id = r1.usuario_id
    AND r2.auto_id = r1.auto_id
    AND r2.id > r1.id
);

-- Crear índice único
CREATE UNIQUE INDEX ux_resena_usuario_auto ON resena(usuario_id, auto_id);

