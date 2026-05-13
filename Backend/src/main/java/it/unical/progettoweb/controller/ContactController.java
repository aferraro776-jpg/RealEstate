package it.unical.progettoweb.controller;

import it.unical.progettoweb.dao.impl.PostDaoImpl;
import it.unical.progettoweb.dao.impl.SellerDao;
import it.unical.progettoweb.dto.request.ContactRequest;
import it.unical.progettoweb.model.Post;
import it.unical.progettoweb.model.Seller;
import it.unical.progettoweb.service.EmailService;
import it.unical.progettoweb.service.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final PostDaoImpl postDao;
    private final SellerDao sellerDao;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> contattaVenditore(
            @RequestBody ContactRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        String senderEmail = jwtUtil.extractEmail(authHeader.substring(7));

        Post post = postDao.get(req.getPostId())
                .orElseThrow(() -> new RuntimeException("Annuncio non trovato"));

        Seller seller = sellerDao.get(post.getSellerId())
                .orElseThrow(() -> new RuntimeException("Venditore non trovato"));

        emailService.sendContactEmail(
                seller.getEmail(),
                req.getSenderName(),
                req.getSenderSurname(),
                senderEmail,
                String.valueOf(req.getPostId()),
                req.getPostTitle(),
                req.getMessage()
        );

        return ResponseEntity.ok("Email inviata con successo al venditore.");
    }
}