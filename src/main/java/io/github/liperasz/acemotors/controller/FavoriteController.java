package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.favorite.FavoriteRequest;
import io.github.liperasz.acemotors.dto.favorite.FavoriteResponse;
import io.github.liperasz.acemotors.entity.Favorite;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.service.FavoriteService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    private final ApplicationEventPublisher publisher;

    public FavoriteController (FavoriteService favoriteService, ApplicationEventPublisher publisher) {
        this.favoriteService = favoriteService;
        this.publisher = publisher;
    }

    @GetMapping
    public Page<FavoriteResponse> findAll(@PathVariable Long clientId, Pageable pageable) {
        return favoriteService.findAll(clientId, pageable).map(FavoriteResponse::from);
    }

    @GetMapping("/{id}")
    public FavoriteResponse findById(@PathVariable Long clientId, @PathVariable Long id) {
        return FavoriteResponse.from(favoriteService.findById(clientId, id));
    }

    @PostMapping
    public ResponseEntity<FavoriteResponse> create(@PathVariable Long clientId,
                                                   @Valid @RequestBody FavoriteRequest request,
                                                   HttpServletResponse response) {
        Favorite favorite = favoriteService.create(clientId, request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, favorite.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(FavoriteResponse.from(favorite));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long clientId, @PathVariable Long id) {
        favoriteService.delete(clientId, id);
    }
}
