package it.unical.progettoweb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.channel.id}")
    private String channelId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void inviaAnnuncio(String titolo, String descrizione,
                              double prezzo, String indirizzo, int postId) {
        try {
            String messaggio = String.format("""
                🏠 *Nuovo annuncio*
                
                *%s*
                📍 %s
                💶 %.0f€
                
                %s
                """, titolo, indirizzo, prezzo,
                     descrizione.length() > 100 ? descrizione.substring(0, 100) + "..." : descrizione);

            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", channelId);
            body.put("text", messaggio);
            body.put("parse_mode", "Markdown");

            restTemplate.postForObject(url, body, String.class);

        } catch (Exception e) {
            System.err.println("Errore invio Telegram: " + e.getMessage());
        }
    }
}
