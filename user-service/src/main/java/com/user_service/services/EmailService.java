package com.user_service.services;

import com.user_service.exceptions.EmailException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new EmailException("Exception while sending mail");
        }
    }

    public String formatEmailBody(String username, String otp) {
        return """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
            <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; background-color: #f9f9f9;">
                <h2 style="color: #4CAF50; text-align: center;">Password Reset Request</h2>
                <p>Dear <strong>%s</strong>,</p>
                <p>We received a request to reset your password.</p>
                <p style="font-size: 18px; color: #555;">
                    Your One-Time Password (OTP) for password reset is:
                </p>
                <div style="text-align: center; margin: 20px 0;">
                    <span style="font-size: 24px; color: #333; font-weight: bold; border: 2px dashed #4CAF50; padding: 10px 20px; border-radius: 8px;">%s</span>
                </div>
                <p>This OTP is valid for the next <strong>10 minutes</strong>. Please keep this OTP confidential and do not share it with anyone for your security.</p>
                <p>If you did not request a password reset, please ignore this email or contact our support team immediately.</p>
                <p style="text-align: center; margin-top: 20px;">
                    <a href="mailto:support@example.com" style="text-decoration: none; color: #fff; background-color: #4CAF50; padding: 10px 20px; border-radius: 4px;">Contact Support</a>
                </p>
                <p>Thank you,<br><strong>The Support Team</strong></p>
            </div>
        </body>
        </html>
        """.formatted(username, otp);
    }
}
