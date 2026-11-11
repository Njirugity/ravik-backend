package net.ravik_cms.ravik_backend.common.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * {@link EmailSender} backed by Spring Mail / JavaMailSender (SMTP). Configured via the
 * {@code spring.mail.*} properties and {@code app.mail.from}.
 */
@Component
public class SmtpEmailSender implements EmailSender {
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpEmailSender(JavaMailSender mailSender, @Value("${app.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
