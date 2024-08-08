package com.api.payments.controller;


import com.api.payments.records.PaymentRequest;
import com.api.payments.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @PostMapping
    public ResponseEntity pay(@RequestBody PaymentRequest payment) {
        service.validate(payment);
        return  ResponseEntity.accepted().build();
    }
}