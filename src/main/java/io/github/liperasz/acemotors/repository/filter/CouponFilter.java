package io.github.liperasz.acemotors.repository.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// Parâmetros opcionais da pesquisa de cupons (/coupons?code=&active=&validOn=2026-12-25)
public class CouponFilter {

    private String code;

    private Boolean active;

    // Cupons cuja janela de validade inclui esta data
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validOn;

    public CouponFilter () {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getValidOn() {
        return validOn;
    }

    public void setValidOn(LocalDate validOn) {
        this.validOn = validOn;
    }
}
