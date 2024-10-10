package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@EnableScheduling
public class ScheduledTasks {

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Autowired
    private EmailService emailService;

    @Autowired
    private ImageUploadService imageUploadService;

    @Scheduled(fixedRate = 30000)
    public void skenuojamDuombaze() {
        executorService.submit(emailService::sendPendingEmails);
        executorService.submit(imageUploadService::uploadImages);
    }
}
