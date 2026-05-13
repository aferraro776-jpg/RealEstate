package it.unical.progettoweb.controller;

import it.unical.progettoweb.dto.request.SellerRequest;
import it.unical.progettoweb.dto.request.UserRequest;
import it.unical.progettoweb.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    @PostMapping("/register/user")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest dto) {
        try {
            authService.registraUser(dto);
            return ResponseEntity.ok("Registrazione avvenuta con successo.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/seller")
    public ResponseEntity<String> registerSeller(@RequestBody SellerRequest dto) {
        try {
            authService.registraSeller(dto);
            return ResponseEntity.ok("Registrazione venditore avvenuta con successo.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String token = authService.login(body.get("email"), body.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authService.getMe(authHeader));
    }

    @PostMapping("/register/request-otp")
    public ResponseEntity<String> requestRegistrationOtp(@RequestBody Map<String, String> body) {
        try {
            authService.inviaOtpRegistrazione(body.get("email"));
            return ResponseEntity.ok("OTP inviato.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            authService.inviaOtpRecuperoPassword(body.get("email"));
            return ResponseEntity.ok("OTP inviato.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body) {
        try {
            authService.resetPassword(body.get("email"), body.get("otp"), body.get("newPassword"));
            return ResponseEntity.ok("Password aggiornata.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}