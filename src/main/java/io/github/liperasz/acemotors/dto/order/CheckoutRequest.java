package io.github.liperasz.acemotors.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// Finalização da compra (POST /clients/{clientId}/orders)
public record CheckoutRequest(

        // Endereço de entrega (precisa ser do cliente)
        @NotNull
        Long addressId,

        // Opcional
        @Size(max = 30)
        String couponCode,

        // Opcional: itens do carrinho a comprar. Vazio/ausente = compra o carrinho inteiro
        List<Long> cartItemIds
) {
}
