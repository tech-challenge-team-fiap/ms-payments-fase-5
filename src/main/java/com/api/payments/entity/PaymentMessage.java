package com.api.payments.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMessage {

    private String id;
    private String productId;
    private LocalDate createdAt;

}
