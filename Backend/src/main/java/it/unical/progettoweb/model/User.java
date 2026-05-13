package it.unical.progettoweb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class User extends Person {
    private Date birthDate;
    private String authProvider;
    private boolean isBanned = false;  // ← Boolean maiuscolo, nome "banned" senza "is"
}