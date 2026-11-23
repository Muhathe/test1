package uz.sfera.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public void sendVerificationCode(String to, String code, long expirationMinutes) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Email manzilingizni tasdiqlang");
        message.setText(String.join(System.lineSeparator(),
                "Assalomu alaykum!",
                "",
                "Ro'yxatdan o'tishni yakunlash uchun tasdiqlash kodingiz: " + code,
                "Kod " + expirationMinutes + " daqiqa davomida amal qiladi.",
                "",
                "Agar bu so'rovni siz yubormagan bo'lsangiz, xabarni e'tiborsiz qoldiring."));
        mailSender.send(message);
    }
}
