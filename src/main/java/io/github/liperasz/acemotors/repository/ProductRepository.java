package io.github.liperasz.acemotors.repository;

import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.repository.product.ProductRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

// Estende também ProductRepositoryQuery, que tem a pesquisa com filtro (implementada em ProductRepositoryImpl)
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryQuery {
}
