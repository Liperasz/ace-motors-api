-- ---------------------------------------------------------------------
-- Históricos de status: administrador opcional
-- (vazio quando a mudança é automática, ex: confirmação de pagamento)
-- ---------------------------------------------------------------------
ALTER TABLE client_status_history  MODIFY changed_by_id BIGINT NULL;
ALTER TABLE product_status_history MODIFY changed_by_id BIGINT NULL;
ALTER TABLE order_status_history   MODIFY changed_by_id BIGINT NULL;
ALTER TABLE payment_status_history MODIFY changed_by_id BIGINT NULL;

-- ---------------------------------------------------------------------
-- Cupom: janela de validade passa a ser só data (LocalDate)
-- ---------------------------------------------------------------------
ALTER TABLE coupon MODIFY start_date      DATE NOT NULL;
ALTER TABLE coupon MODIFY expiration_date DATE NOT NULL;
