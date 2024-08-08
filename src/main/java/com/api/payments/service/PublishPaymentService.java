package com.api.payments.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.api.payments.entity.PaymentMessage;
import com.api.payments.records.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Log4j2
public class PublishPaymentService {

    private Logger logger = LoggerFactory.getLogger(PublishPaymentService.class);

    private final AmazonSQS amazonSQSClient;
    private final ObjectMapper objectMapper;

    public PublishPaymentService(AmazonSQS amazonSQSClient, ObjectMapper objectMapper) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
    }

    public void publish(PaymentRequest paymentRequest, String queueName) {
        try {
            GetQueueUrlResult queueUrl = amazonSQSClient.getQueueUrl(queueName);
            var message = PaymentMessage.builder()
                    .id(paymentRequest.id())
                    .productId(paymentRequest.productId())
                    .createdAt(LocalDate.now())
                    .build();

            logger.info("Send message for queued: {}", message);

            var result = amazonSQSClient.sendMessage(
                    queueUrl.getQueueUrl(),
                    objectMapper.writeValueAsString(message)
            );

            logger.info("result send message: {}" , result );

        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }

    }

}
