package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.coupon.CouponRequest;
import io.github.liperasz.acemotors.dto.coupon.CouponValidationResponse;
import io.github.liperasz.acemotors.entity.Coupon;
import io.github.liperasz.acemotors.enums.CouponType;
import io.github.liperasz.acemotors.repository.CouponRepository;
import io.github.liperasz.acemotors.repository.filter.CouponFilter;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService (CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Page<Coupon> filter(CouponFilter filter, Pageable pageable) {
        return couponRepository.filter(filter, pageable);
    }

    public Coupon findById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cupom", id));
    }

    @Transactional
    public Coupon create(CouponRequest request) {
        String code = request.code().toUpperCase();
        if (couponRepository.existsByCode(code)) {
            throw new BusinessException("coupon.code-in-use", "Cupom " + code + " já existe", HttpStatus.CONFLICT);
        }

        Coupon coupon = new Coupon();
        copyToEntity(request, coupon);
        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon update(Long id, CouponRequest request) {
        Coupon coupon = findById(id);

        String code = request.code().toUpperCase();
        if (couponRepository.existsByCodeAndIdNot(code, id)) {
            throw new BusinessException("coupon.code-in-use", "Cupom " + code + " já existe", HttpStatus.CONFLICT);
        }

        if (request.maxUsage() < coupon.getCurrentUsage()) {
            throw new BusinessException("coupon.max-usage-below-current",
                    "Cupom já foi usado " + coupon.getCurrentUsage() + " vezes");
        }

        copyToEntity(request, coupon);
        return couponRepository.save(coupon);
    }

    // Cupom já usado em pedido não pode ser excluído (o banco barra -> 400); nesse caso, desativar
    @Transactional
    public void delete(Long id) {
        couponRepository.delete(findById(id));
        couponRepository.flush();
    }

    // Atualização parcial: ativa/desativa o cupom
    @Transactional
    public void changeActive(Long id, Boolean active) {
        Coupon coupon = findById(id);
        coupon.setActive(active);
        couponRepository.save(coupon);
    }

    // Simula o cupom em um subtotal, sem usá-lo (para o carrinho mostrar o desconto)
    public CouponValidationResponse simulate(String code, BigDecimal subtotal) {
        Coupon coupon = findValidForUse(code);
        BigDecimal discount = calculateDiscount(coupon, subtotal);

        return new CouponValidationResponse(coupon.getCode(), coupon.getType(), coupon.getValue(), subtotal,
                discount, subtotal.subtract(discount));
    }

    // Regras de uso do cupom: existe, ativo, dentro da validade e com usos sobrando.
    // Também usado no checkout (Passo 6)
    public Coupon findValidForUse(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessException("coupon.not-found", "Cupom " + code + " não existe"));

        LocalDate today = LocalDate.now();

        if (!coupon.getActive()) {
            throw new BusinessException("coupon.inactive", "Cupom " + code + " está desativado");
        }
        if (today.isBefore(coupon.getStartDate())) {
            throw new BusinessException("coupon.not-started", "Cupom válido a partir de " + coupon.getStartDate());
        }
        if (today.isAfter(coupon.getExpirationDate())) {
            throw new BusinessException("coupon.expired", "Cupom expirou em " + coupon.getExpirationDate());
        }
        if (coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            throw new BusinessException("coupon.usage-exhausted", "Cupom atingiu " + coupon.getMaxUsage() + " usos");
        }

        return coupon;
    }

    // Percentual: subtotal x valor / 100. Fixo: o valor, mas nunca mais que o subtotal
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal) {
        BigDecimal discount;

        if (coupon.getType() == CouponType.PERCENTAGE) {
            discount = subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getValue().min(subtotal);
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private void copyToEntity(CouponRequest request, Coupon coupon) {
        if (request.expirationDate().isBefore(request.startDate())) {
            throw new BusinessException("coupon.invalid-period", "Data de expiração antes da data de início");
        }

        if (request.type() == CouponType.PERCENTAGE && request.value().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("coupon.invalid-percentage", "Percentual maior que 100");
        }

        coupon.setCode(request.code().toUpperCase());
        coupon.setType(request.type());
        coupon.setValue(request.value());
        coupon.setStartDate(request.startDate());
        coupon.setExpirationDate(request.expirationDate());
        coupon.setMaxUsage(request.maxUsage());
    }
}
