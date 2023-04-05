package com.project.reddit.serviceImplementaion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String toEmail, String text) {


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bhara433@gmail.com");
        message.setTo(toEmail);
        message.setText(text);
        message.setSubject("Email verification");

        mailSender.send(message);
        System.out.println("mail send");
    }

}

