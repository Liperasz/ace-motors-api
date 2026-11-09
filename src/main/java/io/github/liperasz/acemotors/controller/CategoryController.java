package io.github.liperasz.acemotors.controller;

import io.github.liperasz.acemotors.dto.category.CategoryRequest;
import io.github.liperasz.acemotors.dto.category.CategoryResponse;
import io.github.liperasz.acemotors.entity.Category;
import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import io.github.liperasz.acemotors.service.CategoryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final ApplicationEventPublisher publisher;

    public CategoryController (CategoryService categoryService, ApplicationEventPublisher publisher) {
        this.categoryService = categoryService;
        this.publisher = publisher;
    }

    // GET /categories?name=&page=0&size=10&sort=name
    @GetMapping
    public Page<CategoryResponse> findAll(@RequestParam(defaultValue = "") String name, Pageable pageable) {
        return categoryService.findAll(name, pageable).map(CategoryResponse::from);
    }

    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable Long id) {
        return CategoryResponse.from(categoryService.findById(id));
    }

    // 201 Created + header Location (adicionado pelo ResourceCreatedListener)
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request,
                                                   HttpServletResponse response) {
        Category category = categoryService.create(request);
        publisher.publishEvent(new ResourceCreatedEvent(this, response, category.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return CategoryResponse.from(categoryService.update(id, request));
    }

    // 204 No Content: deu certo, mas não há nada para devolver
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
