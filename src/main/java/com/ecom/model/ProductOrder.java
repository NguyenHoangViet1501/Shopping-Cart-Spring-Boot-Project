package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderId;

    private LocalDate orderDate;

    @ManyToOne
    private Product product;

    private Double price;

    private Integer quantity;

    @ManyToOne
    private UserDtls user;

    private String status;

    public String paymentType;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;

}
