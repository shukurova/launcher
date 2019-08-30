package ru.itpark.gameslauncher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public void sendSimpleMessage(String[] emailList, String subject, String content) {
            var message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(emailList);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
    }
}
