-- ---------------------------------------------------------------------
-- O primeiro registro do histórico (pedido/pagamento recém-criado)
-- não tem status anterior
-- ---------------------------------------------------------------------
ALTER TABLE order_status_history   MODIFY previous_status ENUM('AWAITING_PAYMENT', 'CANCELLED', 'DELIVERED', 'PAID', 'SHIPPED') NULL;
ALTER TABLE payment_status_history MODIFY previous_status ENUM('APPROVED', 'DECLINED', 'PENDING', 'REFUNDED') NULL;
