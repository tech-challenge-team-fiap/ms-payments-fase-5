package com.api.payments.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.api.payments.records.PaymentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Log4j2
public class ConsumerSQS {

    private final AmazonSQS amazonSQSClient;
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    public ConsumerSQS(AmazonSQS amazonSQSClient, ObjectMapper objectMapper, PaymentService paymentService) {
        this.amazonSQSClient = amazonSQSClient;
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @Scheduled(cron = "0 */1 * ? * *") // It runs every 5 seconds.
    public void consumeMessages() {
        try {
            String queueUrl = amazonSQSClient.getQueueUrl("payments-order").getQueueUrl();

            ReceiveMessageResult receiveMessageResult = amazonSQSClient.receiveMessage(queueUrl);

            if (!receiveMessageResult.getMessages().isEmpty()) {
                com.amazonaws.services.sqs.model.Message message = receiveMessageResult.getMessages().get(0);
                log.info("Read Message from queue: {}", message.getBody());

                String[] itens = message.getBody().split(",");
                String idProduct = itens[1].split(":")[1].replace("\"", "");
                String idPayment = itens[14].split("=")[1];

                paymentService.validate(new PaymentRequest(idPayment, idProduct));
                amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
            }
        } catch (Exception e) {
            log.error("Queue Exception Message: {}", e.getMessage());
        }
    }
}
