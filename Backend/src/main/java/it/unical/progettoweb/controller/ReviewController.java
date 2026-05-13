package it.unical.progettoweb.controller;

import it.unical.progettoweb.dto.request.ReviewRequest;
import it.unical.progettoweb.dto.response.ReviewDto;
import it.unical.progettoweb.service.JwtUtil;
import it.unical.progettoweb.service.ReviewsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewsService reviewsService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ReviewDto> create(
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            int userId = jwtUtil.extractUserId(authHeader.substring(7));
            ReviewDto savedReview = reviewsService.create(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> update(
            @PathVariable Integer id,
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            int userId = jwtUtil.extractUserId(authHeader.substring(7));
            ReviewDto updated = reviewsService.update(id, request, userId);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            int userId = jwtUtil.extractUserId(authHeader.substring(7));
            String role = jwtUtil.extractRole(authHeader.substring(7));
            reviewsService.delete(id, userId,role);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(reviewsService.getById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReviewDto>> getByPost(@PathVariable Integer postId) {
        try {
            return ResponseEntity.ok(reviewsService.getByPostId(postId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDto>> getByUser(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(reviewsService.getByUserId(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAll() {
        try {
            return ResponseEntity.ok(reviewsService.getAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
