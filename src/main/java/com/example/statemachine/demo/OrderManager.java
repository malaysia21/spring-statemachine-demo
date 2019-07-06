package com.example.statemachine.demo;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class OrderManager {

    private final StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory;
    private final OrderRepository orderRepository;
    private static final String ORDER_ID = "OrderId";
    private static final String PAYMENT = "Payment";

    public OrderManager(StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory, OrderRepository orderRepository) {
        this.stateMachineFactory = stateMachineFactory;
        this.orderRepository = orderRepository;
    }

    public StateMachine<OrderStates, OrderEvents> createOrder(Order order) {
        orderRepository.save(order);
        return initialOrder(order);
    }

    public StateMachine<OrderStates, OrderEvents> initialOrder(Order order) {
        StateMachine<OrderStates, OrderEvents> stateMachine = stateMachineFactory.getStateMachine("ORDER-" + order.getId());
        stateMachine.getExtendedState().getVariables().put(ORDER_ID, order.getId());
        stateMachine.start();
        return stateMachine;
    }

    public boolean sendEvent(StateMachine<OrderStates, OrderEvents> stateMachine, OrderEvents event) {
        return stateMachine.sendEvent(event);
    }

    public boolean pay(StateMachine<OrderStates, OrderEvents> stateMachine, OrderEvents event, BigDecimal payment) {
        Message<OrderEvents> eventsMessage = MessageBuilder.withPayload(OrderEvents.PAY)
                .setHeader(PAYMENT, payment).build();
        return stateMachine.sendEvent(eventsMessage);
    }

    @SuppressWarnings("unchecked")
    public List<Transition<OrderStates, OrderEvents>> getTransitions(StateMachine<OrderStates, OrderEvents> stateMachine) {
        List<Transition<OrderStates, OrderEvents>> transitions = new ArrayList<>();
        for (Object objTrans : stateMachine.getTransitions()) {
            Transition<OrderStates, OrderEvents> transition = (Transition<OrderStates, OrderEvents>) objTrans;
            if (transition.getSource().getId().equals(stateMachine.getState().getId())) {
                transitions.add(transition);
            }
        }
        return transitions;
    }
}
