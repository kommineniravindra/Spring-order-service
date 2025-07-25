package com.sathya.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sathya.dto.OrderRequest;
import com.sathya.entity.Order;
import com.sathya.entity.OrderItem;
import com.sathya.entity.OrderStatus; // Ensure this import exists

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

        // --- START OF CORRECTED LOGIC FOR STATUS HANDLING ---
        String initialStatus = request.getStatus(); // Get the status string from the request
        if (initialStatus == null || initialStatus.trim().isEmpty()) {
            // If the frontend did not send a status, or it's empty, default to PENDING
            order.setStatus(OrderStatus.PENDING);
        } else {
            try {
                // Attempt to parse the provided status string into an enum value
                order.setStatus(OrderStatus.valueOf(initialStatus.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // If the provided string is not a valid OrderStatus enum name, default to PENDING
                order.setStatus(OrderStatus.PENDING);
                System.err.println("Warning: Invalid initial order status received ('" + initialStatus + "'). Defaulting to PENDING. Error: " + e.getMessage());
            }
        }
        // --- END OF CORRECTED LOGIC ---

        List<OrderItem> items = request.getCartItems().stream().map(item -> {
            OrderItem i = new OrderItem();
            i.setProductName(item.getProductName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            i.setOrder(order); // Link the OrderItem to its parent Order
            return i;
        }).collect(Collectors.toList());

        order.setItems(items); // Set the list of items on the order

        orderRepository.save(order); // Save the order, which cascades to save order items
    }

    public List<Order> getMyOrders(String token) {
        Long userId = JwtUtil.extractUserId(token);
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        try {
            OrderStatus statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());
            order.setStatus(statusEnum);
            orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status provided: " + newStatus, e);
        }
    }
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

}