package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.report.SalesByCategory;
import io.github.liperasz.acemotors.dto.report.SalesByDay;
import io.github.liperasz.acemotors.dto.report.SalesByProduct;
import io.github.liperasz.acemotors.service.SalesReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

// Relatórios de vendas (área administrativa). from/to opcionais, formato yyyy-MM-dd
@RestController
@RequestMapping("/reports/sales")
public class SalesReportController {

    private final SalesReportService reportService;

    public SalesReportController (SalesReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/by-day")
    public List<SalesByDay> byDay(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return reportService.byDay(from, to);
    }

    @GetMapping("/by-product")
    public List<SalesByProduct> byProduct(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return reportService.byProduct(from, to);
    }

    @GetMapping("/by-category")
    public List<SalesByCategory> byCategory(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) {
        return reportService.byCategory(from, to);
    }
}
