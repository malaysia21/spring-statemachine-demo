package com.example.statemachine.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
@Component
public class DemoApplication implements CommandLineRunner {

    private final OrderManager orderManager;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public DemoApplication(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Order order = new Order(2L, "order1", 456765,  BigDecimal.valueOf(199.99), false,
                LocalDate.now(), DeliveryMethod.COLLECT);
        StateMachine<OrderStates, OrderEvents> stateMachine = orderManager.createOrder(order);
        orderManager.pay(stateMachine, OrderEvents.PAY, BigDecimal.valueOf(199.99));
        orderManager.sendEvent(stateMachine, OrderEvents.DELIVER);
        log.info(orderManager.getTransitions(stateMachine).toString());
    }
}








