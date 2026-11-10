
package com.example.lab3.service;

import com.example.lab3.dto.OrderDto;
import com.example.lab3.dto.OrderItemDto;
import com.example.lab3.entity.Order;
import com.example.lab3.entity.OrderItem;
import com.example.lab3.entity.Product;
import com.example.lab3.repository.OrderRepository;
import com.example.lab3.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    // CREATE ORDER
    public OrderDto create(OrderDto dto) {

        Order order = new Order(
                null,
                dto.orderDate() != null
                        ? dto.orderDate()
                        : LocalDateTime.now(),
                new java.util.ArrayList<>()
        );

        for (OrderItemDto itemDto : dto.items()) {

            Product product = productRepo.findById(itemDto.productId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Product not found"));

            OrderItem item = new OrderItem(
                    null,
                    product,
                    itemDto.quantity(),
                    order
            );

            order.getItems().add(item);
        }

        Order saved = orderRepo.save(order);

        List<OrderItemDto> items = saved.getItems()
                .stream()
                .map(item -> new OrderItemDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderDto(
                saved.getId(),
                saved.getOrderDate(),
                items
        );
    }

    // GET ORDER BY ID
    @Transactional(readOnly = true)
    public OrderDto getById(Long id) {

        Order order = orderRepo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Order not found"));

        List<OrderItemDto> items = order.getItems()
                .stream()
                .map(item -> new OrderItemDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderDto(
                order.getId(),
                order.getOrderDate(),
                items
        );
    }
}
