package io.github.liperasz.acemotors.repository.product;

import io.github.liperasz.acemotors.dto.product.ProductSummary;
import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.repository.filter.ProductFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryQuery {

    Page<Product> filter(ProductFilter filter, Pageable pageable);

    // Mesma pesquisa, mas devolvendo só os campos usados na listagem do catálogo
    Page<ProductSummary> summarize(ProductFilter filter, Pageable pageable);
}
