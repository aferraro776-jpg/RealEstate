package it.unical.progettoweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private int id;
    private String title;
    private String description;
    private String author;
    private int rating;
    private LocalDateTime createdAt;
    private int userId;
    private int postId;
}
