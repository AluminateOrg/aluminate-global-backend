package com.aluminate.aluminate_global_backend.service.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${SENDGRID_FROM_ADDRESS}")
    private String fromAddress;

    @Value("${SENDGRID_FROM_NAME}")
    private String appName;

    @Autowired
    private SendGrid sendGrid;

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
        log.info("Initiated Sending OTP to {} with OTP: {}", to, otp);

        Email from = new Email(fromAddress, appName);
        Email toEmail = new Email(to);
        String subject = "OTP code for verification from: " + fromAddress;

        // Load and process template (same as before)
        String htmlContent = getHtmlTemplate("templates/otp-email.html");
        htmlContent = htmlContent.replace("{{OTP}}", otp);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        log.info("Sending OTP email to {}", to);
        Response response = sendGrid.api(request);

        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            log.info("OTP email sent successfully to {} - Status: {}", to, response.getStatusCode());
        } else {
            log.error("Failed to send OTP email to {} - Status: {}, Body: {}",
                    to, response.getStatusCode(), response.getBody());
            throw new RuntimeException("SendGrid API returned status: " + response.getStatusCode());
        }

    }

    public String getHtmlTemplate(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }


}
