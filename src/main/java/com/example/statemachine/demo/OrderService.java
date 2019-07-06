package com.example.statemachine.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private Map<String, Boolean> availability = new HashMap<>();

    @PostConstruct
    private void loadAvailabilityMap() {
        availability.put("order1", true);
        availability.put("order2", false);
        availability.put("order3", true);
    }

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean validateOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (availability.containsKey(order.getName())) {
                return (availability.get(order.getName()));
            }
            return false;
        }
        return false;
    }

    public boolean validateOrderPayment(Long orderId, BigDecimal payment) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if (payment.compareTo(order.getPrice()) >= 0) {
                order.setIsPaid(true);
                orderRepository.save(order);
                return true;
            } else return false;
        }
        return false;
    }

    public boolean validateDeliveryMethod(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            return DeliveryMethod.COURIER.equals(order.getDeliveryMethod());
        }
        return false;
    }

}
