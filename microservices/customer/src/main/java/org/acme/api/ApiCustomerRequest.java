package org.acme.api;

public record ApiCustomerRequest (Long fiscalNumber, String location, String name) {}
