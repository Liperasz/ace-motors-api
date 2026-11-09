package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.coupon.CouponRequest;
import io.github.liperasz.acemotors.dto.coupon.CouponResponse;
import io.github.liperasz.acemotors.dto.coupon.CouponValidationResponse;
import io.github.liperasz.acemotors.entity.Coupon;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.repository.filter.CouponFilter;
import io.github.liperasz.acemotors.service.CouponService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    private final ApplicationEventPublisher publisher;

    public CouponController (CouponService couponService, ApplicationEventPublisher publisher) {
        this.couponService = couponService;
        this.publisher = publisher;
    }

    // GET /coupons?code=&active=&validOn=2026-12-25&page=&size=&sort=
    @GetMapping
    public Page<CouponResponse> filter(CouponFilter filter, Pageable pageable) {
        return couponService.filter(filter, pageable).map(CouponResponse::from);
    }

    @GetMapping("/{id}")
    public CouponResponse findById(@PathVariable Long id) {
        return CouponResponse.from(couponService.findById(id));
    }

    // GET /coupons/validate?code=ACE10&subtotal=1000 -> desconto calculado ou 400 com o motivo
    @GetMapping("/validate")
    public CouponValidationResponse validate(@RequestParam String code,
                                             @RequestParam @NotNull @Positive BigDecimal subtotal) {
        return couponService.simulate(code, subtotal);
    }

    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CouponRequest request,
                                                 HttpServletResponse response) {
        Coupon coupon = couponService.create(request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, coupon.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponse.from(coupon));
    }

    @PutMapping("/{id}")
    public CouponResponse update(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return CouponResponse.from(couponService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        couponService.delete(id);
    }

    // Atualização parcial: corpo é só true ou false
    @PutMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeActive(@PathVariable Long id, @RequestBody @NotNull Boolean active) {
        couponService.changeActive(id, active);
    }
}
