package it.unical.progettoweb.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {
    private int id;
    private String title;
    private String description;
    private int rating;
    private LocalDateTime createdAt;
    private int userId;
    private int postId;
}
