package it.unical.progettoweb.controller;

import it.unical.progettoweb.dto.request.PostRequest;
import it.unical.progettoweb.dto.request.PostWithRealEstateCreateDto;
import it.unical.progettoweb.dto.response.PostDto;
import it.unical.progettoweb.service.JwtUtil;
import it.unical.progettoweb.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<PostDto>> getAll(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction) {
        return ResponseEntity.ok(postService.getAll(sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(postService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getBySeller(@PathVariable int sellerId) {
        try {
            return ResponseEntity.ok(postService.getBySellerId(sellerId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/realestate/{realEstateId}")
    public ResponseEntity<?> getByRealEstate(@PathVariable int realEstateId) {
        try {
            return ResponseEntity.ok(postService.getByRealEstateId(realEstateId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PostDto> create(
            @RequestBody PostRequest post,
            @RequestHeader("Authorization") String authHeader) {
        int sellerId = jwtUtil.extractUserId(authHeader.substring(7));
        PostDto savedPost = postService.save(post, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody PostRequest postDto,
                                    @RequestHeader("Authorization") String authHeader) {
        try {
            int sellerId = jwtUtil.extractUserId(authHeader.substring(7));
            PostDto updated = postService.update(id, postDto,sellerId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/reduce-price")
    public ResponseEntity<?> reducePrice(
                                        @PathVariable int id,
                                        @RequestBody Map<String, Double> body,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            double newPrice = body.get("newPrice");
            int sellerId = jwtUtil.extractUserId(authHeader.substring(7));
            PostDto updated = postService.reducePrice(id, newPrice,sellerId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id,@RequestHeader("Authorization") String authHeader) {
        try {
            int sellerId = jwtUtil.extractUserId(authHeader.substring(7));
            postService.delete(id,sellerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/with-realestate")
    public ResponseEntity<PostDto> createWithRealEstate(
            @RequestBody PostWithRealEstateCreateDto dto,
            @RequestHeader("Authorization") String authHeader) {
        int sellerId = jwtUtil.extractUserId(authHeader.substring(7));
        PostDto savedPost = postService.saveWithRealEstate(dto, sellerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }
}