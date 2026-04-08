package br.anhembi.spring_proja3.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

     @Autowired
     private JavaMailSender emailSender;

     public void sendEmail(String to, String subject, String body) {
          SimpleMailMessage message = new SimpleMailMessage();
          message.setTo(to); 
          message.setSubject(subject); 
          message.setText(body);
          emailSender.send(message);
     }
}
