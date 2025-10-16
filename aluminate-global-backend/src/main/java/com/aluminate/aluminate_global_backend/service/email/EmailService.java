package com.aluminate.aluminate_global_backend.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromAddress) {
        this.fromAddress = fromAddress;
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
    }

    public void sendOtpMail(String to, String otp) throws MessagingException, IOException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject("OTP code for verification");

        String htmlContent = getHtmlTemplate("templates/otp-email.html");
        htmlContent = htmlContent.replace("{{OTP}}", otp);
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
        System.out.println("Email sent successfully");
    }

    public String getHtmlTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
