package com.example.demo;

import com.example.demo.Laiskai.Laiskas;
import com.example.demo.Laiskai.LaiskasRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class RabbitMQService {

    private static final String QUEUE_NAME = "pavyzdine_eile";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;
    private Connection connection;
    private Channel channel;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private boolean listenerStarted = false;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LaiskasRepository laiskasRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    public RabbitMQService() {
        this.factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public synchronized void startListener() throws Exception {
        if (!listenerStarted) {
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println("Listening for messages...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                byte[] messageBody = delivery.getBody();
                processMessage(messageBody);
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
                System.out.println("Consumer Tag: " + consumerTag + " stopped");
            });

            listenerStarted = true;
        }
    }

    private void processMessage(byte[] messageBody) {
        executorService.submit(() -> {
            try {
                String jsonMessage = new String(messageBody, "UTF-8");
                JsonNode rootNode = objectMapper.readTree(jsonMessage);

                if (isEmailMessage(rootNode)) {
                    Laiskas laiskas = objectMapper.treeToValue(rootNode, Laiskas.class);
                    processEmail(laiskas);
                } else {
                    processImage(jsonMessage);
                }
            } catch (Exception e) {
                System.err.println("Error processing message: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private boolean isEmailMessage(JsonNode rootNode) {
        return rootNode.has("gavejas");
    }

    private void processEmail(Laiskas laiskas) {
        try {
            executorService.submit(() -> {
                try {
                    emailService.sendEmail(laiskas);
                    System.out.println("Processed email for: " + laiskas.getGavejas());

                    executorService.submit(() -> {
                        try {
                            laiskasRepository.updateIssiusta(laiskas.getGavejas(), laiskas.getTurinys());
                            System.out.println("Updated email status for: " + laiskas.getGavejas());
                        } catch (Exception e) {
                            System.err.println("Error updating email status: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error processing email: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error submitting email task: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void processImage(String base64Image) {
        try {
            executorService.submit(() -> {
                try {
                    final String finalBase64Image = base64Image.replace("\"", "").trim();
                    byte[] imageBytes = Base64.getDecoder().decode(finalBase64Image);
                    executorService.submit(() -> {
                        try {
                            imageUploadService.uploadImage(imageBytes);
                            System.out.println("Processed and uploaded image.");
                        } catch (Exception e) {
                            System.err.println("Error uploading image: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error processing image: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error submitting image task: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @PreDestroy
    public void shutdownExecutor() {
        System.out.println("Shutting down ExecutorService...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing RabbitMQ connection: " + e.getMessage());
        }

        System.out.println("ExecutorService and RabbitMQ connection shutdown complete.");
    }
}
