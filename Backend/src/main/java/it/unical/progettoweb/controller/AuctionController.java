package it.unical.progettoweb.controller;

import it.unical.progettoweb.dto.request.AuctionRequest;
import it.unical.progettoweb.dto.request.BidRequest;
import it.unical.progettoweb.dto.response.AuctionDto;
import it.unical.progettoweb.dto.response.BidDto;
import it.unical.progettoweb.service.AuctionService;
import it.unical.progettoweb.service.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auctions")
@AllArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createAuction(
            @RequestBody AuctionRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            int sellerId = jwtUtil.extractUserId(token);
            AuctionDto dto = auctionService.createAuction(request, sellerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getAuctionByPost(@PathVariable int postId) {
        try {
            AuctionDto dto = auctionService.getAuctionByPostId(postId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{auctionId}/bids")
    public ResponseEntity<?> placeBid(
            @PathVariable int auctionId,
            @RequestBody BidRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            int userId = jwtUtil.extractUserId(token);
            BidDto dto = auctionService.placeBid(auctionId, request.getAmount(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<?> deleteAuction(
            @PathVariable int auctionId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            int sellerId = jwtUtil.extractUserId(token);
            auctionService.deleteAuction(auctionId, sellerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}