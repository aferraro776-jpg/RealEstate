package it.unical.progettoweb.controller;

import it.unical.progettoweb.dto.response.UserDto;
import it.unical.progettoweb.dto.request.UserRequest;
import it.unical.progettoweb.model.User;
import it.unical.progettoweb.service.JwtUtil;
import it.unical.progettoweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    public ResponseEntity<?> getProfilo(@RequestHeader("Authorization") String authHeader) {
        try {
            String email = estraiEmail(authHeader);
            User user = userService.getUtenteByEmail(email);

            UserDto dto = new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getSurname(),
                    user.getEmail(),
                    user.getBirthDate(),
                    user.getAuthProvider(),
                    "BUYER"
            );
            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/me")
    public ResponseEntity<String> aggiornaProfilo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserRequest dto) {
        try {
            String email = estraiEmail(authHeader);
            userService.aggiornaProfilo(email, dto);
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

            userService.cambiaPassword(email, oldPassword, newPassword);
            return ResponseEntity.ok("Password aggiornata con successo.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> cancellaAccount(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String email = estraiEmail(authHeader);
            userService.cancellaAccount(email);
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