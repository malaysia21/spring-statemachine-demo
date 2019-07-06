package com.example.statemachine.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="ORDERS")
public class Order {

    @Id
    private Long id;
    private String name;
    private Integer number;
    private BigDecimal price;
    private Boolean isPaid;
    private LocalDate orderDate;
    private DeliveryMethod deliveryMethod;

}
