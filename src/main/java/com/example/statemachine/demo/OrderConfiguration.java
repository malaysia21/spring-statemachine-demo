package com.example.statemachine.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.math.BigDecimal;


@EnableStateMachineFactory
@Configuration
public class OrderConfiguration extends EnumStateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    private final OrderService orderService;
    private static final String ORDER_ID = "OrderId";
    private static final String PAYMENT = "Payment";
    private final Logger log = LoggerFactory.getLogger(getClass());

    public OrderConfiguration(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }


    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
        states
                .withStates()
                .initial(OrderStates.INITIALIZED)
                .state(OrderStates.INITIALIZED, orderValidation(), null)
                .state(OrderStates.IN_PROCESS)
                .choice(OrderStates.PAID)
                .state(OrderStates.SENT)
                .state(OrderStates.VALIDATED)
                .state(OrderStates.READY_TO_COLLECT)
                .state(OrderStates.PICKED_UP)
                .state(OrderStates.DELIVERED)
                .end(OrderStates.CANCELLED)
                .end(OrderStates.REJECTED)
                .end(OrderStates.COMPLETED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderStates.INITIALIZED).target(OrderStates.REJECTED).event(OrderEvents.REJECT)
                .and()
                .withExternal()
                .source(OrderStates.INITIALIZED).target(OrderStates.IN_PROCESS).event(OrderEvents.VALIDATE)
                .and()
                .withExternal().source(OrderStates.IN_PROCESS).target(OrderStates.PAID).event(OrderEvents.PAY).guard(paidGuard())
                .and()
                .withChoice().source(OrderStates.PAID).first(OrderStates.SENT, deliveryMethodGuard()).last(OrderStates.READY_TO_COLLECT)
                .and()
                .withExternal()
                .source(OrderStates.READY_TO_COLLECT).event(OrderEvents.DELIVER).target(OrderStates.PICKED_UP).action(completeOrder())
                .and()
                .withExternal().source(OrderStates.SENT).event(OrderEvents.DELIVER).target(OrderStates.DELIVERED).action(completeOrder());
    }


    private Action<OrderStates, OrderEvents> completeOrder() {
        return context -> {
            context.getStateMachine().sendEvent(OrderEvents.COMPLETE);
        };
    }

    private Action<OrderStates, OrderEvents> orderValidation() {
        return context -> {
            String orderId = context.getStateMachine().getId().substring(6);
            if (orderService.validateOrder(Long.valueOf(orderId))) {
                context.getStateMachine().sendEvent(OrderEvents.VALIDATE);
            } else
                context.getStateMachine().sendEvent(OrderEvents.REJECT);
        };
    }

    private Guard<OrderStates, OrderEvents> paidGuard() {
        return context -> {
            Object orderId = context.getStateMachine().getExtendedState().getVariables().getOrDefault(ORDER_ID, -1L);
            Object payment = context.getMessage().getHeaders().getOrDefault(PAYMENT, BigDecimal.ZERO);
            return orderService.validateOrderPayment((Long) orderId, (BigDecimal) payment);
        };
    }

    private Guard<OrderStates, OrderEvents> deliveryMethodGuard() {
        return context -> {
            Object orderId = context.getExtendedState().getVariables().getOrDefault(ORDER_ID, -1L);
            return orderService.validateDeliveryMethod((Long) orderId);
        };
    }

    @Bean
    public StateMachineListener<OrderStates, OrderEvents> listener() {
        return new StateMachineListenerAdapter<OrderStates, OrderEvents>() {
            @Override
            public void stateChanged(State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {
                log.info("STATE CHANGE TO: " + to.getId());
            }
        };
    }
}