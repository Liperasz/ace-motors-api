package io.github.liperasz.acemotors.repository.filter;

import io.github.liperasz.acemotors.enums.OrderStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Parâmetros opcionais da pesquisa de pedidos (/orders?clientId=&status=&from=2026-11-01&to=2026-11-30)
public class OrderFilter {

    private Long clientId;

    private OrderStatus status;

    // Pedidos criados a partir deste dia (inclusive)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    // Pedidos criados até este dia (inclusive)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    public OrderFilter () {

    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
}
