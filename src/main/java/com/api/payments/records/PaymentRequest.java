package com.api.payments.records;

public record PaymentRequest(
        String id,
        String productId
) {
}