package it.unical.progettoweb.controller;

import it.unical.progettoweb.dto.request.RealEstateRequest;
import it.unical.progettoweb.service.RealEstateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/realestate")
@RequiredArgsConstructor
public class RealEstateController {

    private final RealEstateService realEstateService;

    @GetMapping
    public ResponseEntity<List<?>> getAll() {
        return ResponseEntity.ok(realEstateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(realEstateService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RealEstateRequest realEstate) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(realEstateService.save(realEstate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody RealEstateRequest realEstate) {
        try {
            return ResponseEntity.ok(realEstateService.update(id, realEstate));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            realEstateService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}