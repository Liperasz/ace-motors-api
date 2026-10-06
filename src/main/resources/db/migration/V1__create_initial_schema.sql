-- =====================================================================
-- Ace Motors - V1: schema inicial
-- Ordem: tabelas independentes primeiro, dependentes depois.
-- =====================================================================

-- ---------------------------------------------------------------------
-- Pessoa e herança (JOINED: PK compartilhada)
-- ---------------------------------------------------------------------
CREATE TABLE person (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_person_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE client (
    id     BIGINT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_client_person FOREIGN KEY (id) REFERENCES person (id)
) ENGINE=InnoDB;

CREATE TABLE administrator (
    id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_administrator_person FOREIGN KEY (id) REFERENCES person (id)
) ENGINE=InnoDB;

CREATE TABLE client_status_history (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    client_id       BIGINT       NOT NULL,
    changed_by_id   BIGINT       NOT NULL,
    previous_status ENUM('ACTIVE', 'INACTIVE') NOT NULL,
    new_status      ENUM('ACTIVE', 'INACTIVE') NOT NULL,
    update_notes    VARCHAR(255) NOT NULL,
    update_time     DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_client_status_history_client FOREIGN KEY (client_id)     REFERENCES client (id),
    CONSTRAINT fk_client_status_history_admin  FOREIGN KEY (changed_by_id) REFERENCES administrator (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Endereço, telefone e recuperação de senha
-- ---------------------------------------------------------------------
CREATE TABLE address (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    person_id    BIGINT       NOT NULL,
    street       VARCHAR(255) NOT NULL,
    number       VARCHAR(255) NOT NULL,
    complement   VARCHAR(255),
    neighborhood VARCHAR(255) NOT NULL,
    city         VARCHAR(255) NOT NULL,
    state        VARCHAR(255) NOT NULL,
    zip_code     VARCHAR(255) NOT NULL,
    active       BIT          NOT NULL DEFAULT 1,
    created_at   DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_address_person FOREIGN KEY (person_id) REFERENCES person (id)
) ENGINE=InnoDB;

CREATE TABLE phone (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    person_id  BIGINT       NOT NULL,
    number     VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_phone_person FOREIGN KEY (person_id) REFERENCES person (id)
) ENGINE=InnoDB;

CREATE TABLE password_recovery (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    person_id  BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL,
    used       BIT          NOT NULL DEFAULT 0,
    expires_at DATETIME(6)  NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_password_recovery_token  UNIQUE (token),
    CONSTRAINT fk_password_recovery_person FOREIGN KEY (person_id) REFERENCES person (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Catálogo
-- ---------------------------------------------------------------------
CREATE TABLE category (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE product (
    id          BIGINT         NOT NULL AUTO_INCREMENT,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(255),
    price       DECIMAL(12, 2) NOT NULL,
    image_url   VARCHAR(255),
    status      ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') NOT NULL,
    created_at  DATETIME(6)    NOT NULL,
    updated_at  DATETIME(6)    NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE product_category (
    product_id  BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (category_id, product_id),
    CONSTRAINT fk_product_category_product  FOREIGN KEY (product_id)  REFERENCES product (id),
    CONSTRAINT fk_product_category_category FOREIGN KEY (category_id) REFERENCES category (id)
) ENGINE=InnoDB;

CREATE TABLE product_status_history (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    product_id      BIGINT       NOT NULL,
    changed_by_id   BIGINT       NOT NULL,
    previous_status ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') NOT NULL,
    new_status      ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') NOT NULL,
    update_notes    VARCHAR(255) NOT NULL,
    update_time     DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_status_history_product FOREIGN KEY (product_id)    REFERENCES product (id),
    CONSTRAINT fk_product_status_history_admin   FOREIGN KEY (changed_by_id) REFERENCES administrator (id)
) ENGINE=InnoDB;

CREATE TABLE favorite (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    client_id  BIGINT      NOT NULL,
    product_id BIGINT      NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_favorite_client_product UNIQUE (client_id, product_id),
    CONSTRAINT fk_favorite_client  FOREIGN KEY (client_id)  REFERENCES client (id),
    CONSTRAINT fk_favorite_product FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Carrinho
-- ---------------------------------------------------------------------
CREATE TABLE cart (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    client_id  BIGINT      NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cart_client UNIQUE (client_id),
    CONSTRAINT fk_cart_client FOREIGN KEY (client_id) REFERENCES client (id)
) ENGINE=InnoDB;

CREATE TABLE cart_item (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    cart_id    BIGINT      NOT NULL,
    product_id BIGINT      NOT NULL,
    quantity   INT         NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_cart_item_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT chk_cart_item_quantity CHECK (quantity > 0),
    CONSTRAINT fk_cart_item_cart    FOREIGN KEY (cart_id)    REFERENCES cart (id),
    CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Cupom e formas de pagamento salvas
-- ---------------------------------------------------------------------
CREATE TABLE coupon (
    id              BIGINT         NOT NULL AUTO_INCREMENT,
    code            VARCHAR(255)   NOT NULL,
    type            ENUM('FIXED_VALUE', 'PERCENTAGE') NOT NULL,
    value           DECIMAL(12, 2) NOT NULL,
    start_date      DATETIME(6)    NOT NULL,
    expiration_date DATETIME(6)    NOT NULL,
    max_usage       INT            NOT NULL,
    current_usage   INT            NOT NULL DEFAULT 0,
    active          BIT            NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    CONSTRAINT uk_coupon_code UNIQUE (code)
) ENGINE=InnoDB;

CREATE TABLE saved_payment_method (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    client_id   BIGINT       NOT NULL,
    type        ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PIX') NOT NULL,
    nickname    VARCHAR(255) NOT NULL,
    masked_data VARCHAR(255) NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_saved_payment_method_client FOREIGN KEY (client_id) REFERENCES client (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Pedido
-- ---------------------------------------------------------------------
CREATE TABLE orders (
    id         BIGINT         NOT NULL AUTO_INCREMENT,
    client_id  BIGINT         NOT NULL,
    address_id BIGINT         NOT NULL,
    coupon_id  BIGINT,
    status     ENUM('AWAITING_PAYMENT', 'CANCELLED', 'DELIVERED', 'PAID', 'SHIPPED') NOT NULL,
    subtotal   DECIMAL(12, 2) NOT NULL,
    discount   DECIMAL(12, 2) NOT NULL,
    total      DECIMAL(12, 2) NOT NULL,
    created_at DATETIME(6)    NOT NULL,
    updated_at DATETIME(6)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_orders_client  FOREIGN KEY (client_id)  REFERENCES client (id),
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES address (id),
    CONSTRAINT fk_orders_coupon  FOREIGN KEY (coupon_id)  REFERENCES coupon (id)
) ENGINE=InnoDB;

CREATE TABLE order_item (
    id         BIGINT         NOT NULL AUTO_INCREMENT,
    order_id   BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    quantity   INT            NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    subtotal   DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_order_item_quantity CHECK (quantity > 0),
    CONSTRAINT fk_order_item_order   FOREIGN KEY (order_id)   REFERENCES orders (id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product (id)
) ENGINE=InnoDB;

CREATE TABLE order_snapshot (
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    order_id         BIGINT         NOT NULL,
    address_snapshot VARCHAR(255)   NOT NULL,
    total_snapshot   DECIMAL(12, 2) NOT NULL,
    snapshot_date    DATETIME(6)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_order_snapshot_order UNIQUE (order_id),
    CONSTRAINT fk_order_snapshot_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB;

CREATE TABLE order_status_history (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    order_id        BIGINT       NOT NULL,
    changed_by_id   BIGINT       NOT NULL,
    previous_status ENUM('AWAITING_PAYMENT', 'CANCELLED', 'DELIVERED', 'PAID', 'SHIPPED') NOT NULL,
    new_status      ENUM('AWAITING_PAYMENT', 'CANCELLED', 'DELIVERED', 'PAID', 'SHIPPED') NOT NULL,
    update_notes    VARCHAR(255) NOT NULL,
    update_time     DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_status_history_order FOREIGN KEY (order_id)      REFERENCES orders (id),
    CONSTRAINT fk_order_status_history_admin FOREIGN KEY (changed_by_id) REFERENCES administrator (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Avaliação
-- ---------------------------------------------------------------------
CREATE TABLE review (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    order_item_id BIGINT       NOT NULL,
    rating        INT          NOT NULL,
    comment       VARCHAR(255),
    created_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_review_order_item UNIQUE (order_item_id),
    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_review_order_item FOREIGN KEY (order_item_id) REFERENCES order_item (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Pagamento
-- ---------------------------------------------------------------------
CREATE TABLE payment (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    order_id          BIGINT         NOT NULL,
    payment_method_id BIGINT,
    method            ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PIX') NOT NULL,
    status            ENUM('APPROVED', 'DECLINED', 'PENDING', 'REFUNDED') NOT NULL,
    amount            DECIMAL(12, 2) NOT NULL,
    transaction_code  VARCHAR(255)   NOT NULL,
    created_at        DATETIME(6)    NOT NULL,
    updated_at        DATETIME(6)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_payment_order  FOREIGN KEY (order_id)          REFERENCES orders (id),
    CONSTRAINT fk_payment_method FOREIGN KEY (payment_method_id) REFERENCES saved_payment_method (id)
) ENGINE=InnoDB;

CREATE TABLE payment_status_history (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    payment_id      BIGINT      NOT NULL,
    changed_by_id   BIGINT      NOT NULL,
    previous_status ENUM('APPROVED', 'DECLINED', 'PENDING', 'REFUNDED') NOT NULL,
    new_status      ENUM('APPROVED', 'DECLINED', 'PENDING', 'REFUNDED') NOT NULL,
    update_time     DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_payment_status_history_payment FOREIGN KEY (payment_id)    REFERENCES payment (id),
    CONSTRAINT fk_payment_status_history_admin   FOREIGN KEY (changed_by_id) REFERENCES administrator (id)
) ENGINE=InnoDB;
