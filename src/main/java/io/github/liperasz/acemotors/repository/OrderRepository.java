package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Orders;
import io.github.liperasz.acemotors.repository.order.OrderRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long>, OrderRepositoryQuery {

    // Endereço já usado em algum pedido não pode ser apagado de verdade
    boolean existsByAddressId(Long addressId);
}
