package it.unical.progettoweb.controller;

import it.unical.progettoweb.dto.response.SellerDto;
import it.unical.progettoweb.dto.request.SellerRequest;
import it.unical.progettoweb.model.Seller;
import it.unical.progettoweb.service.JwtUtil;
import it.unical.progettoweb.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerController {

    private final SellerService sellerService;
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<?> getProfilo(@RequestHeader("Authorization") String authHeader) {
        try {
            String email = estraiEmail(authHeader);
            SellerDto seller = sellerService.getSellerByEmail(email);
            return ResponseEntity.ok(seller);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/me")
    public ResponseEntity<String> aggiornaProfilo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SellerRequest dto) {
        try {
            String email = estraiEmail(authHeader);
            sellerService.aggiornaProfilo(email, dto);
            return ResponseEntity.ok("Profilo aggiornato con successo.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> cambiaPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        try {
            String email = estraiEmail(authHeader);
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");


            if (oldPassword == null || newPassword == null)
                return ResponseEntity.badRequest()
                        .body("I campi oldPassword e newPassword sono obbligatori.");

            sellerService.cambiaPassword(email, oldPassword, newPassword);
            return ResponseEntity.ok("Password aggiornata con successo.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/me")
    public ResponseEntity<String> cancellaAccount(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String email = estraiEmail(authHeader);
            sellerService.cancellaAccount(email);
            return ResponseEntity.ok("Account eliminato con successo.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String estraiEmail(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractEmail(token);
    }
}