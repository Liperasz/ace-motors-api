package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.report.SalesByCategory;
import io.github.liperasz.acemotors.dto.report.SalesByDay;
import io.github.liperasz.acemotors.dto.report.SalesByProduct;
import io.github.liperasz.acemotors.entity.Orders;
import io.github.liperasz.acemotors.repository.report.SalesReportRepository;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

// Relatórios de vendas (RF07). Período padrão: do dia 1 do mês atual até hoje
@Service
public class SalesReportService {

    private final SalesReportRepository reportRepository;

    public SalesReportService (SalesReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<SalesByDay> byDay(LocalDate from, LocalDate to) {
        LocalDate start = startDate(from);
        LocalDate end = endDate(to);
        validatePeriod(start, end);

        // Agrupa os pedidos pelo dia (no fuso do servidor); TreeMap deixa os dias em ordem
        Map<LocalDate, List<Orders>> ordersByDay = reportRepository
                .soldOrders(toInstant(start), toInstant(end.plusDays(1))).stream()
                .collect(Collectors.groupingBy(
                        order -> LocalDate.ofInstant(order.getCreatedAt(), ZoneId.systemDefault()),
                        TreeMap::new, Collectors.toList()));

        return ordersByDay.entrySet().stream()
                .map(entry -> new SalesByDay(entry.getKey(), (long) entry.getValue().size(),
                        entry.getValue().stream().map(Orders::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add)))
                .toList();
    }

    public List<SalesByProduct> byProduct(LocalDate from, LocalDate to) {
        LocalDate start = startDate(from);
        LocalDate end = endDate(to);
        validatePeriod(start, end);
        return reportRepository.byProduct(toInstant(start), toInstant(end.plusDays(1)));
    }

    public List<SalesByCategory> byCategory(LocalDate from, LocalDate to) {
        LocalDate start = startDate(from);
        LocalDate end = endDate(to);
        validatePeriod(start, end);
        return reportRepository.byCategory(toInstant(start), toInstant(end.plusDays(1)));
    }

    private LocalDate startDate(LocalDate from) {
        return from != null ? from : LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate endDate(LocalDate to) {
        return to != null ? to : LocalDate.now();
    }

    private void validatePeriod(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new BusinessException("report.invalid-period", "Data final " + end + " antes da inicial " + start);
        }
    }

    private Instant toInstant(LocalDate day) {
        return day.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
