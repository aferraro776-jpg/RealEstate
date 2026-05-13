package it.unical.progettoweb.dto.request;

import lombok.Data;

@Data
public class ContactRequest {
    private String senderName;
    private String senderSurname;
    private String senderEmail;
    private String message;
    private int postId;
    private String postTitle;
}