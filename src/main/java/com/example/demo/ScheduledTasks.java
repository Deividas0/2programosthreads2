//package com.example.demo;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//@Component
//public class ScheduledTasks {
//
//    @Autowired
//    private RabbitMQService rabbitMQService;
//
//    @PostConstruct
//    public void startRabbitListener() {
//        try {
//            // Start the RabbitMQ listener
//            rabbitMQService.startListener();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
