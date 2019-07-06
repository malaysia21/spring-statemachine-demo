package com.example.statemachine.demo;

public enum OrderStates {

    INITIALIZED("Initialized"),
    VALIDATED("Validated"),
    APPROVED ("Approved"),
    PAID("Paid"),
    IN_PROCESS("In process"),
    SENT("Sent"),
    READY_TO_COLLECT("Ready to collect"),
    DELIVERED("Delivered"),
    PICKED_UP("Picked up"),
    COMPLETED("Completed"),
    CANCELLED ("Cancelled"),
    PROCESSED ("Processed"),
    REJECTED("Rejected"),
    CREATED("Created"),
    DENIED("Denied");

    private String description;

    private OrderStates(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
