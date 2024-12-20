package com.user_service.services;

import com.user_service.exceptions.EmailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);

            javaMailSender.send(mail);
        } catch (Exception e) {
            throw new EmailException("Exception while sending mail");
        }
    }

    public String formatEmailBody(String username, String otp) {
        return """
            Dear %s,

            We received a request to reset your password.

            Your One-Time Password (OTP) for password reset is:

            %s

            This OTP is valid for the next 10 minutes. Please keep this OTP confidential and do not share it with anyone for your security.

            If you did not request a password reset, please ignore this email or contact our support team immediately.

            Thank you,
            The Support Team
            """.formatted(username, otp);
    }
}
