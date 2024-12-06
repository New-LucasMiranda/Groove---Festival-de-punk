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
          message.setTo(to); // Destinatário
          message.setSubject(subject); // Assunto
          message.setText(body); // Corpo do e-mail
          emailSender.send(message); // Envia o e-mail
     }
}
