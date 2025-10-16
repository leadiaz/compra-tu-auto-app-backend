-- V2: Jerarquía de usuarios con SINGLE_TABLE y discriminador
-- Base de datos: PostgreSQL

-- a) Agregar columna discriminador
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS dtype VARCHAR(20) NOT NULL DEFAULT 'COMPRADOR';

-- b) Asegurar unicidad de favorito por usuario (constraint con nombre específico)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uk_favorito_usuario'
    ) THEN
        ALTER TABLE favorito ADD CONSTRAINT uk_favorito_usuario UNIQUE (usuario_id);
    END IF;
END $$;

-- c) Trigger: Solo COMPRADOR puede tener favorito
CREATE OR REPLACE FUNCTION check_favorito_usuario_comprador() RETURNS trigger AS $$
DECLARE v_dtype TEXT;
BEGIN
  SELECT dtype INTO v_dtype FROM usuario WHERE id = NEW.usuario_id;
  IF v_dtype IS DISTINCT FROM 'COMPRADOR' THEN
    RAISE EXCEPTION 'Solo UsuarioComprador puede tener favorito (usuario_id=%)', NEW.usuario_id;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS tr_check_favorito ON favorito;
CREATE TRIGGER tr_check_favorito
BEFORE INSERT OR UPDATE ON favorito
FOR EACH ROW
EXECUTE FUNCTION check_favorito_usuario_comprador();

-- d) Agregar columna concesionaria_id
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS concesionaria_id BIGINT;

-- e) Agregar clave foránea solo si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_concesionaria'
    ) THEN
        ALTER TABLE usuario ADD CONSTRAINT fk_concesionaria FOREIGN KEY (concesionaria_id) REFERENCES concesionaria(id);
    END IF;
END $$;