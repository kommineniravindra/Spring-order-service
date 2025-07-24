package com.sathya.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "orders") 
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDateTime orderDate;
    private double totalAmount;

    // --- ADD THIS NEW FIELD ---
    @Enumerated(EnumType.STRING) 
    private OrderStatus status;
    // -------------------------

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> items;
}