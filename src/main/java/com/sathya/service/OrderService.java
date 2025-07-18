package com.sathya.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sathya.dto.OrderRequest;
import com.sathya.entity.Order;
import com.sathya.entity.OrderItem;

import com.sathya.repository.OrderRepository;
import com.sathya.util.JwtUtil;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;



    public void placeOrder(OrderRequest request, String token) {
        Long userId = JwtUtil.extractUserId(token); // extract userId from JWT

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setUserId(userId);
        order.setTotalAmount(request.getTotalAmount());

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
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }



}

