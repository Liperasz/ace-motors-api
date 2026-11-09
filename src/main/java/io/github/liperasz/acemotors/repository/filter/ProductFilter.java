package io.github.liperasz.acemotors.repository.filter;

import io.github.liperasz.acemotors.enums.ProductStatus;

import java.math.BigDecimal;

// Parâmetros opcionais da pesquisa de produtos (vêm da URL: /products?name=&minPrice=...)
public class ProductFilter {

    private String name;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private Long categoryId;

    private ProductStatus status;

    public ProductFilter () {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
