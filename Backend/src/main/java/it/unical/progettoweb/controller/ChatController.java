package it.unical.progettoweb.controller;

import it.unical.progettoweb.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        String domanda = body.get("domanda");
        if (domanda == null || domanda.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("errore", "Domanda mancante"));
        }
        String risposta = chatService.rispondi(domanda);
        return ResponseEntity.ok(Map.of("risposta", risposta));
    }
}