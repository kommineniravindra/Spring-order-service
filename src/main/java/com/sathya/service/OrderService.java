package com.sathya.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sathya.dto.OrderRequest;
import com.sathya.entity.Order;
import com.sathya.entity.OrderItem;
import com.sathya.entity.OrderStatus;

import com.sathya.repository.OrderRepository;
import com.sathya.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException; 

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public void placeOrder(OrderRequest request, String token) {
        Long userId = JwtUtil.extractUserId(token);

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setUserId(userId);
        order.setTotalAmount(request.getTotalAmount());
        // --- MODIFY HERE: Set initial status from request ---
        try {
            order.setStatus(OrderStatus.valueOf(request.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            // If the status from the frontend is invalid, default to PENDING
            order.setStatus(OrderStatus.PENDING);
            System.err.println("Warning: Invalid initial status received for order. Defaulting to PENDING. " + e.getMessage());
        }


        List<OrderItem> items = request.getCartItems().stream().map(item -> {
            OrderItem i = new OrderItem();
            i.setProductName(item.getProductName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            i.setOrder(order);
            return i;
        }).collect(Collectors.toList());

        order.setItems(items);

        orderRepository.save(order);
    }

    public List<Order> getMyOrders(String token) {
        Long userId = JwtUtil.extractUserId(token);
        // The returned Order objects will now include the 'status' field automatically
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        // The returned Order objects will now include the 'status' field automatically
        return orderRepository.findAll();
    }

    // --- ADD THIS NEW METHOD ---
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        try {
            // Convert the string newStatus to the OrderStatus enum
            OrderStatus statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());
            order.setStatus(statusEnum);
            orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            // Handle cases where an invalid status string is provided (e.g., "invalid" instead of "ACCEPTED")
            throw new IllegalArgumentException("Invalid status provided: " + newStatus, e);
        }
    }
  
}