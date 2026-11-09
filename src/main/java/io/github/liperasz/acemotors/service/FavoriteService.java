package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.favorite.FavoriteRequest;
import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.entity.Favorite;
import io.github.liperasz.acemotors.entity.Product;
import io.github.liperasz.acemotors.repository.FavoriteRepository;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final ClientService clientService;

    private final ProductService productService;

    public FavoriteService (FavoriteRepository favoriteRepository, ClientService clientService,
                            ProductService productService) {
        this.favoriteRepository = favoriteRepository;
        this.clientService = clientService;
        this.productService = productService;
    }

    public Page<Favorite> findAll(Long clientId, Pageable pageable) {
        clientService.findById(clientId);
        return favoriteRepository.findByClientId(clientId, pageable);
    }

    public Favorite findById(Long clientId, Long id) {
        return favoriteRepository.findByIdAndClientId(id, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito", id));
    }

    // Só cliente ativo favorita; o produto pode estar esgotado (lista de desejos), mas tem que existir
    @Transactional
    public Favorite create(Long clientId, FavoriteRequest request) {
        Client client = clientService.findActive(clientId);
        Product product = productService.findById(request.productId());

        if (favoriteRepository.existsByClientIdAndProductId(clientId, product.getId())) {
            throw new BusinessException("favorite.already-exists",
                    "Produto " + product.getId() + " já está nos favoritos", HttpStatus.CONFLICT);
        }

        Favorite favorite = new Favorite();
        favorite.setClient(client);
        favorite.setProduct(product);
        return favoriteRepository.save(favorite);
    }

    @Transactional
    public void delete(Long clientId, Long id) {
        favoriteRepository.delete(findById(clientId, id));
    }
}
