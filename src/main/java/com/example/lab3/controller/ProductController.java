
package com.example.lab3.controller;

import com.example.lab3.dto.ProductDto;
import com.example.lab3.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // CREATE - MANAGER and ADMIN
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProductDto> create(
            @Valid @RequestBody ProductDto dto) {

        ProductDto created = productService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // READ ONE - USER, MANAGER and ADMIN
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ProductDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    // UPDATE - ADMIN only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto dto) {

        return ResponseEntity.ok(productService.update(id, dto));
    }

    // DELETE - ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET ALL - USER, MANAGER and ADMIN
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    // SEARCH
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> search(@RequestParam String name) {
        return ResponseEntity.ok(productService.search(name));
    }

    // PAGINATION
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public Page<ProductDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sort) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sort)
        );

        return productService.findAll(pageable);
    }

    // FILTER BY CATEGORY
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public List<ProductDto> findByCategory(
            @PathVariable Long categoryId) {

        return productService.findByCategoryId(categoryId);
    }
}
