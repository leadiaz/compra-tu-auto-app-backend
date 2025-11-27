BEGIN;

-- Ajusta zona horaria si es necesario
SET LOCAL TIME ZONE 'UTC-3';

INSERT INTO public.usuario (email, password, nombre, apellido, activo, created_at, updated_at, dtype, concesionaria_id)
VALUES ('admin@compraauto.com', '$2a$10$6vbWj79e.84JkOBgPZnlleETinMh7nnYM8zKm6eJfqQK2mwSg5OrO', 'Administrador', 'Sistema', true, '2025-11-23 21:44:01.591166 +00:00', '2025-11-23 21:44:01.591166 +00:00', 'ADMIN', null)
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.usuario (email, password, nombre, apellido, activo, created_at, updated_at, dtype, concesionaria_id)
VALUES ('leadiaz@compraauto.com', '$2a$10$Q/OgFIKdvnriv.v5uG.7Uu8Mqb3rXCnHtNOa0I6SQFsduvdcx69cq', 'leandro', 'diaz', true, '2025-11-24 20:06:31.621683 +00:00', '2025-11-24 20:06:31.621683 +00:00', 'COMPRADOR', null)
ON CONFLICT (email) DO NOTHING;

INSERT INTO public.usuario (email, password, nombre, apellido, activo, created_at, updated_at, dtype, concesionaria_id)
VALUES ('concesionaria@compraauto.com', '$2a$10$8V0I2LjFxeS4uyirIk.S9.rRCGOGqcs8uOKvt8HeNI8rVQXD6QTIi', 'concesionaria', 'BSAS', true, '2025-11-25 03:08:39.128962 +00:00', '2025-11-25 03:08:39.128962 +00:00', 'CONCESIONARIA', null)
ON CONFLICT (email) DO NOTHING;

COMMIT;

