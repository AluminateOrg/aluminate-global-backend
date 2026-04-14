package com.aluminate.aluminate_global_backend.service.email;

import com.aluminate.aluminate_global_backend.controller.AuthController;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Logger;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Value("${SENDGRID_FROM_ADDRESS}")
    private String fromAddress;

    @Value("${SENDGRID_FROM_NAME}")
    private String appName;

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
        logger.info("Initiated Sending OTP to " + to + " with OTP: " + otp);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject("OTP code for verification from: " + appName);
        logger.info("Created helper for OTP email, now loading HTML template");
        String htmlContent = getHtmlTemplate("templates/otp-email.html");
        htmlContent = htmlContent.replace("{{OTP}}", otp);
        helper.setText(htmlContent, true);
        logger.info("Sending OTP email to " + to);
        mailSender.send(mimeMessage);

    }

    public String getHtmlTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }


}
