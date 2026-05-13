package it.unical.progettoweb.controller;

import it.unical.progettoweb.dao.impl.AdminDao;
import it.unical.progettoweb.dao.impl.BlacklistDao;
import it.unical.progettoweb.dao.impl.SellerDao;
import it.unical.progettoweb.dao.impl.UserDao;
import it.unical.progettoweb.dto.request.BlacklistRequest;
import it.unical.progettoweb.model.Admin;
import it.unical.progettoweb.model.Seller;
import it.unical.progettoweb.model.User;
import it.unical.progettoweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserDao userDao;
    private final SellerDao sellerDao;
    private final AdminDao adminDao;
    private final BlacklistDao blacklistDao;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userDao.getAll());
    }

    @GetMapping("/sellers")
    public ResponseEntity<List<Seller>> getAllSellers() {
        return ResponseEntity.ok(sellerDao.getAll());
    }

    @GetMapping("/admins")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminDao.getAll());
    }

    @GetMapping("/blacklist")
    public ResponseEntity<List<String>> getBlacklist() {
        return ResponseEntity.ok(blacklistDao.getAll());
    }


    @PostMapping("/ban")
    public ResponseEntity<String> banUser(@RequestBody BlacklistRequest request) {
        try {
            adminService.banUser(request.getEmail());
            return ResponseEntity.ok("Utente bannato con successo.");
        } catch (IllegalArgumentException e) {
            // Email non trovata in nessuna tabella
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            // Utente già bannato
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unban")
    public ResponseEntity<String> unbanUser(@RequestBody BlacklistRequest request) {
        try {
            adminService.unbanUser(request.getEmail());
            return ResponseEntity.ok("Ban rimosso con successo.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        if (userDao.get(id).isEmpty())
            return ResponseEntity.notFound().build();
        userDao.delete(id);
        return ResponseEntity.ok("Utente eliminato.");
    }

    @DeleteMapping("/sellers/{id}")
    public ResponseEntity<String> deleteSeller(@PathVariable int id) {
        if (sellerDao.get(id).isEmpty())
            return ResponseEntity.notFound().build();
        sellerDao.delete(id);
        return ResponseEntity.ok("Venditore eliminato.");
    }

    @PostMapping("/promote")
    public ResponseEntity<String> promoteToAdmin(@RequestBody BlacklistRequest request) {
        try {
            adminService.promuoviAdAdmin(request.getEmail());
            return ResponseEntity.ok("Utente promosso ad amministratore.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}