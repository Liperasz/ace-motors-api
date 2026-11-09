package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.product.*;
import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.repository.filter.ProductFilter;
import io.github.liperasz.acemotors.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final ApplicationEventPublisher publisher;

    public ProductController (ProductService productService, ApplicationEventPublisher publisher) {
        this.productService = productService;
        this.publisher = publisher;
    }

    // GET /products?name=&minPrice=&maxPrice=&categoryId=&status=&page=&size=&sort=
    @GetMapping
    public Page<ProductResponse> filter(ProductFilter filter, Pageable pageable) {
        return productService.filter(filter, pageable).map(ProductResponse::from);
    }

    // GET /products?summary -> mesmos filtros, resposta resumida para o catálogo
    @GetMapping(params = "summary")
    public Page<ProductSummary> summarize(ProductFilter filter, Pageable pageable) {
        return productService.summarize(filter, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return ProductResponse.from(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request,
                                                  HttpServletResponse response) {
        Product product = productService.create(request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, product.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    // Atualização parcial: só o status
    @PutMapping("/{id}/status")
    public ProductResponse changeStatus(@PathVariable Long id, @Valid @RequestBody ProductStatusRequest request) {
        return ProductResponse.from(productService.changeStatus(id, request));
    }

    @GetMapping("/{id}/status-history")
    public Page<ProductStatusHistoryResponse> findStatusHistory(@PathVariable Long id, Pageable pageable) {
        return productService.findStatusHistory(id, pageable).map(ProductStatusHistoryResponse::from);
    }

    // Upload da imagem (multipart/form-data, campo "file", só WebP)
    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse updateImage(@PathVariable Long id, @RequestParam MultipartFile file) {
        return ProductResponse.from(productService.updateImage(id, file));
    }
}
