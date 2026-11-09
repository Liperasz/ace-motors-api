package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.product.ProductRequest;
import io.github.liperasz.acemotors.dto.product.ProductStatusRequest;
import io.github.liperasz.acemotors.dto.product.ProductSummary;
import io.github.liperasz.acemotors.entity.Category;
import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.entity.ProductStatusHistory;
import io.github.liperasz.acemotors.enums.ProductStatus;
import io.github.liperasz.acemotors.repository.CategoryRepository;
import io.github.liperasz.acemotors.repository.ProductRepository;
import io.github.liperasz.acemotors.repository.ProductStatusHistoryRepository;
import io.github.liperasz.acemotors.repository.filter.ProductFilter;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final ProductStatusHistoryRepository historyRepository;

    private final AdministratorService administratorService;

    private final ImageStorageService imageStorageService;

    public ProductService (ProductRepository productRepository, CategoryRepository categoryRepository,
                           ProductStatusHistoryRepository historyRepository,
                           AdministratorService administratorService, ImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.historyRepository = historyRepository;
        this.administratorService = administratorService;
        this.imageStorageService = imageStorageService;
    }

    public Page<Product> filter(ProductFilter filter, Pageable pageable) {
        return productRepository.filter(filter, pageable);
    }

    public Page<ProductSummary> summarize(ProductFilter filter, Pageable pageable) {
        return productRepository.summarize(filter, pageable);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", id));
    }

    // Regra de negócio: só produto ACTIVE pode ir para o carrinho ou para um pedido
    public Product findAvailable(Long id) {
        Product product = findById(id);

        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new BusinessException("product.unavailable",
                    "Produto " + id + " está com status " + product.getStatus());
        }

        return product;
    }

    @Transactional
    public Product create(ProductRequest request) {
        Product product = new Product();
        product.setStatus(ProductStatus.ACTIVE);
        copyToEntity(request, product);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        copyToEntity(request, product);
        return productRepository.save(product);
    }

    // Produto com histórico, pedidos, carrinho ou favoritos não pode ser excluído (o banco barra -> 400).
    // Nesses casos o certo é mudar o status para INACTIVE
    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
        productRepository.flush();
        imageStorageService.delete(product.getImageUrl());
    }

    // Atualização parcial: muda só o status e registra no histórico
    @Transactional
    public Product changeStatus(Long id, ProductStatusRequest request) {
        Product product = findById(id);

        if (product.getStatus() == request.status()) {
            throw new BusinessException("status.unchanged", "O produto já está com status " + request.status());
        }

        ProductStatusHistory history = new ProductStatusHistory();
        history.setProduct(product);
        history.setChangedBy(administratorService.findForAction(request.adminId()));
        history.setPreviousStatus(product.getStatus());
        history.setNewStatus(request.status());
        history.setUpdateNotes(request.notes());
        historyRepository.save(history);

        product.setStatus(request.status());
        return productRepository.save(product);
    }

    public Page<ProductStatusHistory> findStatusHistory(Long id, Pageable pageable) {
        findById(id);
        return historyRepository.findByProductIdOrderByUpdateTimeDesc(id, pageable);
    }

    // Troca a imagem do produto (apaga a antiga da pasta)
    @Transactional
    public Product updateImage(Long id, MultipartFile file) {
        Product product = findById(id);
        String oldUrl = product.getImageUrl();

        product.setImageUrl(imageStorageService.saveWebp(file, "products"));
        productRepository.save(product);

        imageStorageService.delete(oldUrl);
        return product;
    }

    private void copyToEntity(ProductRequest request, Product product) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());

        // Todas as categorias informadas precisam existir
        List<Category> categories = categoryRepository.findAllById(request.categoryIds());
        if (categories.size() != request.categoryIds().size()) {
            throw new BusinessException("product.category-not-found",
                    "Uma ou mais categorias não existem: " + request.categoryIds());
        }
        product.setCategories(new HashSet<>(categories));
    }
}
