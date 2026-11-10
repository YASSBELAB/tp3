
package com.example.lab3.controller;

import com.example.lab3.dto.OrderDto;
import com.example.lab3.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // CREATE ORDER - USER only
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderDto> create(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody OrderDto dto) {

        OrderDto created = orderService.create(
                user.getUsername(),
                dto
        );

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET ALL ORDERS - USER sees own, MANAGER and ADMIN see all
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<OrderDto>> list(
            @AuthenticationPrincipal UserDetails user) {

        boolean canReadAll = user.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN")
                                || a.getAuthority().equals("ROLE_MANAGER")
                );

        List<OrderDto> orders;

        if (canReadAll) {
            orders = orderService.findAll();
        } else {
            orders = orderService.findByUsername(user.getUsername());
        }

        return ResponseEntity.ok(orders);
    }

    // GET ORDER BY ID - MANAGER and ADMIN can access all
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<OrderDto> get(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {

        boolean canReadAll = user.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN")
                                || a.getAuthority().equals("ROLE_MANAGER")
                );

        return ResponseEntity.ok(
                orderService.getById(
                        id,
                        user.getUsername(),
                        canReadAll
                )
        );
    }

    // DELETE ORDER - ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
