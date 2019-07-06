package com.example.statemachine.demo;

public enum OrderEvents {
    APPROVE ("Approve order"),
    PROCESS ("Process order"),
    VALIDATE("Validate"),
    REJECT("Rejected"),
    PAY("Pay"),
    COLLECT("Collect"),
    DELIVER("Deliver"),
    COMPLETE("Complete"),
    CANCEL("Cancel"),
    DENY("Deny");

    private String description;

    private OrderEvents(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}