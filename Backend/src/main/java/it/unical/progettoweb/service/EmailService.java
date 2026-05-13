package it.unical.progettoweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtp(String to, String code, String purpose) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Il tuo codice OTP - " + purpose);
        msg.setText("Il tuo codice è: " + code + "\nScade tra 10 minuti.");
        mailSender.send(msg);
    }

    public void sendAuctionWon(String to, String postTitle, double amount) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Hai vinto l'asta - " + postTitle);
        msg.setText("Congratulazioni! Hai vinto l'asta per l'immobile \"" + postTitle +
                "\" con un'offerta di € " + String.format("%.2f", amount) + ".\n" +
                "Il venditore ti contatterà a breve.");
        mailSender.send(msg);
    }

    public void sendContactEmail(
            String toSellerEmail,
            String senderName,
            String senderSurname,
            String senderEmail,
            String postId,
            String postTitle,
            String message
    ) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(toSellerEmail);
        mail.setReplyTo(senderEmail);
        mail.setSubject("Richiesta info annuncio #" + postId + " - " + postTitle);
        mail.setText(
                "Hai ricevuto un messaggio da un acquirente interessato al tuo annuncio.\n\n" +
                        "Nome: " + senderName + " " + senderSurname + "\n" +
                        "Email: " + senderEmail + "\n\n" +
                        "Messaggio:\n" + message + "\n\n" +
                        "Puoi rispondere direttamente a questa email."
        );
        mailSender.send(mail);
    }
}