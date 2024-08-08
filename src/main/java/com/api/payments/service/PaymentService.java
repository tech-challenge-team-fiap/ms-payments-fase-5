package com.api.payments.service;

import com.api.payments.records.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService {

    private Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PublishPaymentService publishPaymentService;

    public void validate(PaymentRequest payment) {
        logger.info("Start validation payment");
        boolean statusPayment = new Random().nextBoolean();

        logger.info("Status Payment with id: {} is: {}", payment.id(), statusPayment);
        if(statusPayment){
            publishPaymentService.publish(payment, "payments-success");
            logger.info("send event for success in queue");
        }else{
            publishPaymentService.publish(payment, "payments-error");
            logger.info("send event for fail in queue");
        }
    }
}