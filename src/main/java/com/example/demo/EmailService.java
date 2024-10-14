package com.example.demo;

import com.example.demo.Laiskai.Laiskas;
import com.example.demo.Laiskai.LaiskasRepository;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class EmailService {

    private LaiskasRepository laiskasRepository = new LaiskasRepository();


    public static void sendEmail(Laiskas laiskas) {

        Email email = new Email();

        email.setFrom("thread test", "zemaitis514@trial-7dnvo4dpyqr45r86.mlsender.net");
        email.addRecipient("", laiskas.getGavejas());
        email.setSubject("thread test");
        email.setHtml(laiskas.getTurinys());

        MailerSend ms = new MailerSend();

        ms.setToken("mlsn.3c75551516438ef10410618d5b9034d0aab17787c776732aef8f66c2c0cf6b77");

        try {

            MailerSendResponse response = ms.emails().send(email);
            System.out.println(response.messageId);
        } catch (MailerSendException e) {

            e.printStackTrace();
        }
    }
    public void sendPendingEmails() {
        try {
            System.out.println("Tikrinami  neišsiūsti laiškai.");
            List<Laiskas> emails = laiskasRepository.neissiustiLaiskai();
            System.out.println("Rasti " + emails.size() + " laiškai laukiantys siuntimo.");

            for (Laiskas laiskas : emails) {
                System.out.println(Thread.currentThread().getName() + " Siunčiamas laiškas su ID: " + laiskas.getId() + " gavėjui " + laiskas.getGavejas());
                sendEmail(laiskas);
                System.out.println("Laiškas sėkmingai išsiūstas, atnaujinama duombazė su ID: " + laiskas.getId());
                laiskasRepository.updateIssiusta(laiskas.getGavejas(), laiskas.getTurinys());

                System.out.println("Sekundės pertraukėlė :)");
                Thread.sleep(3000);
            }

            System.out.println("Visi laukiantys laiškai sėkmingai išsiūsti.");

        } catch (SQLException e) {
            System.out.println("Database error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted.");
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

}