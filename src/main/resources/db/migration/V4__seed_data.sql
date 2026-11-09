-- ---------------------------------------------------------------------
-- Dados de exemplo para testar a API
-- Senhas em texto puro por enquanto ("senha12345"); no checkpoint 3 viram hash BCrypt
-- ---------------------------------------------------------------------

-- Pessoas: 1 administrador e 2 clientes
INSERT INTO person (id, name, email, password_hash, created_at, updated_at) VALUES
    (1, 'Administrador Ace', 'admin@acemotors.com', 'senha12345', NOW(6), NOW(6)),
    (2, 'Ana Souza',         'ana@email.com',       'senha12345', NOW(6), NOW(6)),
    (3, 'Bruno Lima',        'bruno@email.com',     'senha12345', NOW(6), NOW(6));

INSERT INTO administrator (id) VALUES (1);

INSERT INTO client (id, status) VALUES
    (2, 'ACTIVE'),
    (3, 'ACTIVE');

INSERT INTO address (person_id, street, number, complement, neighborhood, city, state, zip_code, active, created_at) VALUES
    (2, 'Av. Brasil',         '1500', 'Ap 12', 'Centro',       'Cascavel', 'PR', '85800-000', 1, NOW(6)),
    (3, 'Rua Paraná',         '320',  NULL,    'Jardim Gramado', 'Toledo', 'PR', '85900-000', 1, NOW(6));

INSERT INTO phone (person_id, number, created_at) VALUES
    (2, '45999998888', NOW(6)),
    (3, '45988887777', NOW(6));

-- Catálogo
INSERT INTO category (id, name, description) VALUES
    (1, 'Hipercarros', 'Carros de altíssimo desempenho e produção limitada'),
    (2, 'Elétricos',   'Veículos 100% elétricos'),
    (3, 'Esportivos',  'Carros esportivos de alta performance'),
    (4, 'Conceito',    'Protótipos e modelos futuristas');

INSERT INTO product (id, name, description, price, image_url, status, created_at, updated_at) VALUES
    (1, 'Ace Phantom X',   'Hipercarro elétrico de 1.500 cv',          3500000.00, NULL, 'ACTIVE',       NOW(6), NOW(6)),
    (2, 'Ace Volt S',      'Sedã esportivo elétrico',                   890000.00, NULL, 'ACTIVE',       NOW(6), NOW(6)),
    (3, 'Ace Thunder V12', 'Esportivo a combustão com motor V12',      1500000.00, NULL, 'ACTIVE',       NOW(6), NOW(6)),
    (4, 'Ace Nebula',      'Conceito autônomo com cabine panorâmica',  5200000.00, NULL, 'OUT_OF_STOCK', NOW(6), NOW(6)),
    (5, 'Ace Spark GT',    'Esportivo elétrico de entrada',             450000.00, NULL, 'ACTIVE',       NOW(6), NOW(6)),
    (6, 'Ace Legacy 1999', 'Edição clássica fora de linha',             780000.00, NULL, 'INACTIVE',     NOW(6), NOW(6));

INSERT INTO product_category (product_id, category_id) VALUES
    (1, 1), (1, 2),
    (2, 2), (2, 3),
    (3, 1), (3, 3),
    (4, 2), (4, 4),
    (5, 2), (5, 3),
    (6, 3);

-- Cupons
INSERT INTO coupon (code, type, value, start_date, expiration_date, max_usage, current_usage, active) VALUES
    ('ACE10',     'PERCENTAGE',  10.00,    '2026-01-01', '2027-12-31', 100, 0, 1),
    ('MENOS5000', 'FIXED_VALUE', 5000.00,  '2026-01-01', '2027-12-31', 20,  0, 1),
    ('BLACK2025', 'PERCENTAGE',  15.00,    '2025-11-01', '2025-11-30', 50,  0, 1);
