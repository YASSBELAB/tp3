
package com.example.lab3.controller;

import com.example.lab3.dto.CategoryDto;
import com.example.lab3.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // CREATE - ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> create(
            @Valid @RequestBody CategoryDto dto) {

        return new ResponseEntity<>(
                categoryService.create(dto),
                HttpStatus.CREATED
        );
    }

    // GET BY ID - USER or ADMIN
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CategoryDto> get(@PathVariable Long id) {

        return ResponseEntity.ok(
                categoryService.getById(id)
        );
    }

    // GET ALL - USER or ADMIN
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CategoryDto>> list() {

        return ResponseEntity.ok(
                categoryService.findAll()
        );
    }

    // UPDATE - ADMIN only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDto dto) {

        return ResponseEntity.ok(
                categoryService.update(id, dto)
        );
    }

    // DELETE - ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
