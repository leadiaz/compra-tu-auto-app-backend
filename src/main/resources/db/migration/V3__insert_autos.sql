BEGIN;

-- Ajusta zona horaria si es necesario
SET LOCAL TIME ZONE 'UTC-3';

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Toyota', 'Corolla', 2020, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Ford', 'Focus', 2018, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Volkswagen', 'Golf', 2019, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Chevrolet', 'Cruze', 2017, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Honda', 'Civic', 2021, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Renault', 'Clio', 2016, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('BMW', 'Series 3', 2022, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Audi', 'A4', 2020, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Mercedes-Benz', 'C-Class', 2019, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

INSERT INTO auto (marca, modelo, anio_modelo, created_at, updated_at)
VALUES
    ('Hyundai', 'Elantra', 2018, NOW(), NOW())
ON CONFLICT (marca, modelo, anio_modelo) DO NOTHING;

COMMIT;

