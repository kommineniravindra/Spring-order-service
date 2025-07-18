package com.sathya.controller;

import com.sathya.dto.OrderDTO;
import com.sathya.dto.OrderItemDTO;
import com.sathya.dto.OrderRequest;
import com.sathya.entity.Order;
import com.sathya.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest,
                                             @RequestHeader("Authorization") String token) {
        orderService.placeOrder(orderRequest, token);
        return ResponseEntity.ok("Order placed successfully");
    }

    // ✅ Get orders of the logged-in user
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(orderService.getMyOrders(token));
    }

    // ✅ Get all orders for analytics-service
    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();

        List<OrderDTO> orderDTOs = orders.stream().map(order -> {
            List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item ->
                new OrderItemDTO(item.getProductName(), item.getQuantity(), item.getPrice())
            ).collect(Collectors.toList());

            return new OrderDTO(
                order.getId(),
                order.getUserId(),
                itemDTOs,
                order.getTotalAmount(),
                order.getOrderDate()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOs);
    }
}
